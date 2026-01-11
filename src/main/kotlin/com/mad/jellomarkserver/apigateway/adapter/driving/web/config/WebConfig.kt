package com.mad.jellomarkserver.apigateway.adapter.driving.web.config

import com.mad.jellomarkserver.apigateway.adapter.driving.web.interceptor.JwtAuthenticationInterceptor
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig(
    private val jwtAuthenticationInterceptor: JwtAuthenticationInterceptor
) : WebMvcConfigurer {
    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(jwtAuthenticationInterceptor)
            .addPathPatterns("/api/**")
            .excludePathPatterns(
                "/api/sign-up/**",
                "/api/owners/sign-up",
                "/api/auth/authenticate",
                "/api/auth/refresh",
                "/api/auth/kakao"
            )
    }
}
