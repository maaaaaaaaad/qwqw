package com.mad.jellomarkserver.auth.adapter.driven.kakao

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class KakaoUserInfoTest {

    @Test
    fun `should create KakaoUserInfo with all properties`() {
        val userInfo = KakaoUserInfo(
            id = 3456789012345L,
            nickname = "테스트유저"
        )

        assertThat(userInfo.id).isEqualTo(3456789012345L)
        assertThat(userInfo.nickname).isEqualTo("테스트유저")
    }

    @Test
    fun `should verify data class equality`() {
        val userInfo1 = KakaoUserInfo(id = 123L, nickname = "user1")
        val userInfo2 = KakaoUserInfo(id = 123L, nickname = "user1")

        assertThat(userInfo1).isEqualTo(userInfo2)
        assertThat(userInfo1.hashCode()).isEqualTo(userInfo2.hashCode())
    }

    @Test
    fun `should not be equal when ids are different`() {
        val userInfo1 = KakaoUserInfo(id = 123L, nickname = "user1")
        val userInfo2 = KakaoUserInfo(id = 456L, nickname = "user1")

        assertThat(userInfo1).isNotEqualTo(userInfo2)
    }

    @Test
    fun `should not be equal when nicknames are different`() {
        val userInfo1 = KakaoUserInfo(id = 123L, nickname = "user1")
        val userInfo2 = KakaoUserInfo(id = 123L, nickname = "user2")

        assertThat(userInfo1).isNotEqualTo(userInfo2)
    }
}
