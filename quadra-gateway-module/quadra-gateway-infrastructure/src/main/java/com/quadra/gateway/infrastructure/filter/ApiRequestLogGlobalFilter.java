package com.quadra.gateway.infrastructure.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quadra.gateway.infrastructure.mq.message.ApiRequestLogMessage;
import com.quadra.gateway.infrastructure.mq.producer.ApiRequestLogPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 网关全局接口日志采集（优先走 RocketMQ 异步落库）
 *
 * 注意：为避免影响主流程，任何异常都会被吞掉；body 会做限长截断。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ApiRequestLogGlobalFilter implements GlobalFilter, Ordered {

    private static final int BODY_MAX_LEN = 20_000;
    private static final DateTimeFormatter DT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final ObjectMapper objectMapper;
    private final ApiRequestLogPublisher publisher;

    @Override
    public int getOrder() {
        // 尽量靠后，拿到最终状态码与 route 信息
        return Ordered.LOWEST_PRECEDENCE;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        if (shouldSkip(path)) {
            return chain.filter(exchange);
        }

        long start = System.currentTimeMillis();
        String traceId = getOrCreateTraceId(exchange.getRequest().getHeaders());
        MDC.put("traceId", traceId);

        AtomicReference<String> reqBodyRef = new AtomicReference<>(null);
        AtomicReference<String> respBodyRef = new AtomicReference<>(null);
        AtomicReference<Throwable> errorRef = new AtomicReference<>(null);

        ServerHttpRequest rawRequest = exchange.getRequest();

        return DataBufferUtils.join(rawRequest.getBody())
                .defaultIfEmpty(exchange.getResponse().bufferFactory().wrap(new byte[0]))
                .flatMap(dataBuffer -> {
                    byte[] reqBytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(reqBytes);
                    DataBufferUtils.release(dataBuffer);

                    String requestBody = readBody(reqBytes);
                    reqBodyRef.set(requestBody);

                    ServerHttpRequest decoratedRequest = new ServerHttpRequestDecorator(rawRequest) {
                        @Override
                        public Flux<DataBuffer> getBody() {
                            return Flux.defer(() -> Flux.just(exchange.getResponse().bufferFactory().wrap(reqBytes)));
                        }
                    };

                    ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(exchange.getResponse()) {
                        @Override
                        public Mono<Void> writeWith(org.reactivestreams.Publisher<? extends DataBuffer> body) {
                            if (!(body instanceof Flux<?> fluxBody)) {
                                return super.writeWith(body);
                            }
                            Flux<DataBuffer> flux = ((Flux<DataBuffer>) fluxBody)
                                    .map(dataBuffer1 -> {
                                        byte[] content = new byte[dataBuffer1.readableByteCount()];
                                        dataBuffer1.read(content);
                                        DataBufferUtils.release(dataBuffer1);

                                        // 采集响应体（限长）
                                        String current = respBodyRef.get();
                                        String piece = readBody(content);
                                        if (piece != null && !piece.isBlank()) {
                                            String merged = (current == null ? piece : current + piece);
                                            respBodyRef.set(truncate(merged));
                                        }

                                        return exchange.getResponse().bufferFactory().wrap(content);
                                    });
                            return super.writeWith(flux);
                        }

                        @Override
                        public Mono<Void> writeAndFlushWith(org.reactivestreams.Publisher<? extends org.reactivestreams.Publisher<? extends DataBuffer>> body) {
                            return writeWith(Flux.from(body).flatMapSequential(p -> p));
                        }
                    };

                    ServerWebExchange decoratedExchange = exchange.mutate()
                            .request(decoratedRequest)
                            .response(decoratedResponse)
                            .build();

                    return chain.filter(decoratedExchange)
                            .doOnError(errorRef::set)
                            .doFinally(signalType -> {
                                try {
                                    publishLog(decoratedExchange, traceId, start, reqBodyRef.get(), respBodyRef.get(), errorRef.get());
                                } catch (Exception ignored) {
                                } finally {
                                    MDC.remove("traceId");
                                }
                            });
                });
    }

    private void publishLog(ServerWebExchange exchange,
                            String traceId,
                            long start,
                            String requestBody,
                            String responseBody,
                            Throwable error) {
        int durationMs = (int) (System.currentTimeMillis() - start);
        Integer statusCode = exchange.getResponse().getStatusCode() == null ? null : exchange.getResponse().getStatusCode().value();
        if (statusCode == null && error != null) statusCode = 500;
        if (statusCode == null) statusCode = 200;

        ApiRequestLogMessage msg = new ApiRequestLogMessage();
        msg.setTraceId(traceId);
        msg.setService(resolveService(exchange));
        msg.setMethod(exchange.getRequest().getMethod() == null ? null : exchange.getRequest().getMethod().name());
        msg.setPath(exchange.getRequest().getURI().getPath());
        msg.setQueryString(exchange.getRequest().getURI().getRawQuery());
        msg.setStatusCode(statusCode);
        msg.setDurationMs(durationMs);
        msg.setIpAddress(getClientIp(exchange.getRequest().getHeaders(), exchange.getRequest()));
        msg.setUserAgent(exchange.getRequest().getHeaders().getFirst("User-Agent"));
        msg.setRequestHeaders(toJson(maskHeaders(exchange.getRequest().getHeaders())));
        msg.setRequestBody(truncate(requestBody));
        msg.setResponseBody(truncate(responseBody));

        if (error != null) {
            msg.setErrorMessage(error.getMessage());
            msg.setErrorStack(truncate(stackTraceToString(error)));
        }
        msg.setCreatedAt(LocalDateTime.now().format(DT));

        publisher.publish(msg);
    }

    private boolean shouldSkip(String path) {
        if (path == null) return true;
        return path.startsWith("/v3/api-docs")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/swagger-ui.html")
                || path.startsWith("/actuator");
    }

    private String resolveService(ServerWebExchange exchange) {
        try {
            Route route = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
            if (route != null) {
                if (route.getUri() != null && "lb".equalsIgnoreCase(route.getUri().getScheme())) {
                    String host = route.getUri().getHost();
                    if (host != null && !host.isBlank()) return host;
                }
                return route.getId();
            }
        } catch (Exception ignored) {
        }
        return "unknown";
    }

    private String getOrCreateTraceId(HttpHeaders headers) {
        String traceId = headers.getFirst("X-Request-Id");
        if (traceId == null || traceId.isBlank()) traceId = headers.getFirst("X-Trace-Id");
        if (traceId == null || traceId.isBlank()) traceId = UUID.randomUUID().toString().replace("-", "");
        return traceId;
    }

    private Map<String, Object> maskHeaders(HttpHeaders headers) {
        Map<String, Object> map = new LinkedHashMap<>();
        headers.forEach((k, v) -> {
            String lower = k == null ? "" : k.toLowerCase();
            if ("authorization".equals(lower) || "cookie".equals(lower)) {
                map.put(k, "***");
            } else {
                map.put(k, v == null ? null : String.join(",", v));
            }
        });
        return map;
    }

    private String toJson(Object obj) {
        if (obj == null) return null;
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            return null;
        }
    }

    private String readBody(byte[] bytes) {
        if (bytes == null || bytes.length == 0) return null;
        return truncate(new String(bytes, StandardCharsets.UTF_8));
    }

    private String truncate(String s) {
        if (s == null) return null;
        if (s.length() <= BODY_MAX_LEN) return s;
        return s.substring(0, BODY_MAX_LEN) + "...(truncated)";
    }

    private String getClientIp(HttpHeaders headers, ServerHttpRequest request) {
        String xff = headers.getFirst("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            int idx = xff.indexOf(',');
            return idx > 0 ? xff.substring(0, idx).trim() : xff.trim();
        }
        return request.getRemoteAddress() == null ? null : request.getRemoteAddress().getAddress().getHostAddress();
    }

    private String stackTraceToString(Throwable t) {
        StringBuilder sb = new StringBuilder();
        sb.append(t.getClass().getName()).append(": ").append(t.getMessage()).append("\n");
        StackTraceElement[] stack = t.getStackTrace();
        if (stack != null) {
            for (StackTraceElement e : stack) {
                sb.append("    at ").append(e).append("\n");
                if (sb.length() > BODY_MAX_LEN) break;
            }
        }
        return sb.toString();
    }
}

