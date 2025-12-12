package com.mad.jellomarkserver.auth.adapter.driven.jwt

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "jwt")
data class JwtProperties(
    var secretKey: String = "",
    var accessTokenExpiration: Long = 3600000,
    var refreshTokenExpiration: Long = 604800000
)
