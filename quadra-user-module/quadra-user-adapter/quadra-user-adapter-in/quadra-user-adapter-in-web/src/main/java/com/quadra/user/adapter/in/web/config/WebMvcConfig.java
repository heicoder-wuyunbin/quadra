package com.quadra.user.adapter.in.web.config;

import com.quadra.user.adapter.in.web.interceptor.AuthInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;

    public WebMvcConfig(AuthInterceptor authInterceptor) {
        this.authInterceptor = authInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/v1/users/**", "/v1/blacklists/**", "/v1/questions/**")
                .excludePathPatterns(
                        "/v1/users/register",
                        "/v1/users/login",
                        "/v1/users/refresh"
                );
    }
}
