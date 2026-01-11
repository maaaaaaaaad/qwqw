package com.mad.jellomarkserver.auth.adapter.driven.kakao

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

@Configuration
class KakaoConfig {

    @Bean
    fun restTemplate(): RestTemplate {
        return RestTemplate()
    }
}
