package com.mad.jellomarkserver.auth.core.domain.model

data class TokenPair(
    val accessToken: String,
    val refreshToken: String
)
