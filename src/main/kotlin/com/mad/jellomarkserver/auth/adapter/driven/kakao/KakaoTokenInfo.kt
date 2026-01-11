package com.mad.jellomarkserver.auth.adapter.driven.kakao

data class KakaoTokenInfo(
    val id: Long,
    val expiresIn: Int,
    val appId: Int
)
