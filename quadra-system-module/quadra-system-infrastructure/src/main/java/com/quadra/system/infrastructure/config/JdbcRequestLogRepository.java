package com.quadra.system.infrastructure.config;

import com.quadra.system.application.port.in.dto.PageResult;
import com.quadra.system.application.port.in.dto.ApiStatDTO;
import com.quadra.system.application.port.in.dto.RequestLogDTO;
import com.quadra.system.application.port.out.RequestLogRepositoryPort;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 兜底实现：当 adapter-out 未打包/未生效导致 RequestLogRepositoryPort 缺失时，使用 JDBC 直接写库。
 * <p>
 * 注意：后续 adapter-out 的 MyBatis 实现就绪后，会自动替换该 Bean（ConditionalOnMissingBean）。
 */
@Repository
@ConditionalOnMissingBean(RequestLogRepositoryPort.class)
public class JdbcRequestLogRepository implements RequestLogRepositoryPort {

    private final DataSource dataSource;

    public JdbcRequestLogRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void save(RequestLogDTO log) {
        final String sql = """
                INSERT INTO sys_request_log
                (id, service, trace_id, admin_id, method, path, query_string, status_code, duration_ms,
                 ip_address, user_agent, request_headers, request_body, response_body)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        long id = nextId();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.setString(2, nullToDefault(log.service(), "quadra-system"));
            ps.setString(3, log.traceId());
            if (log.adminId() == null) ps.setObject(4, null);
            else ps.setLong(4, log.adminId());
            ps.setString(5, log.method());
            ps.setString(6, log.path());
            ps.setString(7, log.queryString());
            ps.setInt(8, log.statusCode() == null ? 0 : log.statusCode());
            ps.setInt(9, log.durationMs() == null ? 0 : log.durationMs());
            ps.setString(10, log.ipAddress());
            ps.setString(11, log.userAgent());
            ps.setString(12, log.requestHeaders());
            ps.setString(13, log.requestBody());
            ps.setString(14, log.responseBody());
            ps.executeUpdate();
        } catch (Exception ignored) {
            // 日志写库失败不影响主流程
        }
    }

    @Override
    public PageResult<ApiStatDTO> statsPage(String keyword, String method, int page, int size) {
        int safePage = Math.max(1, page);
        int safeSize = Math.max(1, size);
        int offset = (safePage - 1) * safeSize;

        StringBuilder where = new StringBuilder(" WHERE 1=1 ");
        List<Object> params = new ArrayList<>();
        if (notBlank(method)) {
            where.append(" AND method = ? ");
            params.add(method);
        }
        if (notBlank(keyword)) {
            where.append(" AND path LIKE ? ");
            params.add("%" + keyword + "%");
        }

        long total = queryApiStatsTotal(where.toString(), params);

        final String sql = """
                SELECT CONCAT(method, ':', path) AS id,
                       method,
                       path,
                       COUNT(1) AS cnt,
                       CAST(AVG(duration_ms) AS SIGNED) AS avg_time,
                       NULL AS p95_time,
                       (SUM(CASE WHEN status_code >= 400 THEN 1 ELSE 0 END) / COUNT(1)) AS error_rate,
                       MAX(created_at) AS last_called_at
                FROM sys_request_log
                %s
                GROUP BY method, path
                ORDER BY last_called_at DESC
                LIMIT ? OFFSET ?
                """.formatted(where);

        List<ApiStatDTO> records = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            int idx = bindParams(ps, params);
            ps.setInt(idx++, safeSize);
            ps.setInt(idx, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    records.add(new ApiStatDTO(
                            rs.getString("id"),
                            rs.getString("method"),
                            rs.getString("path"),
                            rs.getLong("cnt"),
                            rs.getLong("avg_time"),
                            null,
                            rs.getDouble("error_rate"),
                            rs.getTimestamp("last_called_at") == null ? null : rs.getTimestamp("last_called_at").toLocalDateTime()
                    ));
                }
            }
        } catch (Exception ignored) {
        }

        return PageResult.of(records, total, safePage, safeSize);
    }

    @Override
    public PageResult<RequestLogDTO> page(
            String service,
            Long adminId,
            Integer statusCode,
            String method,
            String pathKeyword,
            String traceId,
            LocalDateTime startTime,
            LocalDateTime endTime,
            int page,
            int size
    ) {
        int safePage = Math.max(1, page);
        int safeSize = Math.max(1, size);
        int offset = (safePage - 1) * safeSize;

        StringBuilder where = new StringBuilder(" WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (notBlank(service)) {
            where.append(" AND service = ? ");
            params.add(service);
        }
        if (adminId != null) {
            where.append(" AND admin_id = ? ");
            params.add(adminId);
        }
        if (statusCode != null) {
            where.append(" AND status_code = ? ");
            params.add(statusCode);
        }
        if (notBlank(method)) {
            where.append(" AND method = ? ");
            params.add(method);
        }
        if (notBlank(pathKeyword)) {
            where.append(" AND path LIKE ? ");
            params.add("%" + pathKeyword + "%");
        }
        if (notBlank(traceId)) {
            where.append(" AND trace_id = ? ");
            params.add(traceId);
        }
        if (startTime != null) {
            where.append(" AND created_at >= ? ");
            params.add(Timestamp.valueOf(startTime));
        }
        if (endTime != null) {
            where.append(" AND created_at <= ? ");
            params.add(Timestamp.valueOf(endTime));
        }

        long total = queryTotal(where.toString(), params);

        final String sql = "SELECT id, service, trace_id, admin_id, method, path, query_string, status_code, duration_ms, " +
                "ip_address, user_agent, request_headers, request_body, response_body, created_at " +
                "FROM sys_request_log " + where + " ORDER BY created_at DESC LIMIT ? OFFSET ?";

        List<RequestLogDTO> records = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            int idx = bindParams(ps, params);
            ps.setInt(idx++, safeSize);
            ps.setInt(idx, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    records.add(new RequestLogDTO(
                            rs.getLong("id"),
                            rs.getString("service"),
                            rs.getString("trace_id"),
                            rs.getObject("admin_id") == null ? null : rs.getLong("admin_id"),
                            rs.getString("method"),
                            rs.getString("path"),
                            rs.getString("query_string"),
                            rs.getInt("status_code"),
                            rs.getInt("duration_ms"),
                            rs.getString("ip_address"),
                            rs.getString("user_agent"),
                            rs.getString("request_headers"),
                            rs.getString("request_body"),
                            rs.getString("response_body"),
                            rs.getTimestamp("created_at") == null ? null : rs.getTimestamp("created_at").toLocalDateTime()
                    ));
                }
            }
        } catch (Exception ignored) {
        }

        return PageResult.of(records, total, safePage, safeSize);
    }

    private long queryApiStatsTotal(String whereSql, List<Object> params) {
        final String sql = """
                SELECT COUNT(1) FROM (
                    SELECT 1
                    FROM sys_request_log %s
                    GROUP BY method, path
                ) t
                """.formatted(whereSql);
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            bindParams(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong(1);
            }
        } catch (Exception ignored) {
        }
        return 0;
    }

    private long queryTotal(String whereSql, List<Object> params) {
        final String sql = "SELECT COUNT(1) FROM sys_request_log " + whereSql;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            bindParams(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong(1);
            }
        } catch (Exception ignored) {
        }
        return 0;
    }

    private int bindParams(PreparedStatement ps, List<Object> params) throws Exception {
        int idx = 1;
        for (Object p : params) {
            if (p instanceof Timestamp) {
                ps.setTimestamp(idx++, (Timestamp) p);
            } else {
                ps.setObject(idx++, p);
            }
        }
        return idx;
    }

    private long nextId() {
        // 简单雪花替代：毫秒 * 1000 + 随机(0..999)
        return System.currentTimeMillis() * 1000L + ThreadLocalRandom.current().nextInt(1000);
    }

    private boolean notBlank(String s) {
        return s != null && !s.trim().isEmpty();
    }

    private String nullToDefault(String s, String def) {
        return s == null || s.isBlank() ? def : s;
    }
}
