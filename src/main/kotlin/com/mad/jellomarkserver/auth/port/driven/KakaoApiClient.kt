package com.mad.jellomarkserver.auth.port.driven

import com.mad.jellomarkserver.auth.adapter.driven.kakao.KakaoTokenInfo
import com.mad.jellomarkserver.auth.adapter.driven.kakao.KakaoUserInfo

interface KakaoApiClient {
    fun verifyAccessToken(accessToken: String): KakaoTokenInfo
    fun getUserInfo(accessToken: String): KakaoUserInfo
}
