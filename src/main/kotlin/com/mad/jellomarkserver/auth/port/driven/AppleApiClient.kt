package com.mad.jellomarkserver.auth.port.driven

import com.mad.jellomarkserver.auth.adapter.driven.apple.AppleUserInfo

interface AppleApiClient {
    fun verifyIdentityToken(identityToken: String): AppleUserInfo
}
