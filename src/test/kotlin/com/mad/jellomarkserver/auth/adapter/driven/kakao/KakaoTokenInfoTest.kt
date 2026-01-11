package com.mad.jellomarkserver.auth.adapter.driven.kakao

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class KakaoTokenInfoTest {

    @Test
    fun `should create KakaoTokenInfo with all properties`() {
        val tokenInfo = KakaoTokenInfo(
            id = 123456789L,
            expiresIn = 3600,
            appId = 987654
        )

        assertThat(tokenInfo.id).isEqualTo(123456789L)
        assertThat(tokenInfo.expiresIn).isEqualTo(3600)
        assertThat(tokenInfo.appId).isEqualTo(987654)
    }

    @Test
    fun `should verify data class equality`() {
        val tokenInfo1 = KakaoTokenInfo(id = 123L, expiresIn = 3600, appId = 456)
        val tokenInfo2 = KakaoTokenInfo(id = 123L, expiresIn = 3600, appId = 456)

        assertThat(tokenInfo1).isEqualTo(tokenInfo2)
        assertThat(tokenInfo1.hashCode()).isEqualTo(tokenInfo2.hashCode())
    }

    @Test
    fun `should not be equal when ids are different`() {
        val tokenInfo1 = KakaoTokenInfo(id = 123L, expiresIn = 3600, appId = 456)
        val tokenInfo2 = KakaoTokenInfo(id = 999L, expiresIn = 3600, appId = 456)

        assertThat(tokenInfo1).isNotEqualTo(tokenInfo2)
    }
}
