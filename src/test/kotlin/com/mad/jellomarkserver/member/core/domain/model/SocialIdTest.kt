package com.mad.jellomarkserver.member.core.domain.model

import com.mad.jellomarkserver.member.core.domain.exception.InvalidSocialIdException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class SocialIdTest {

    @Test
    fun `should create SocialId with valid value`() {
        val socialId = SocialId("1234567890")

        assertThat(socialId.value).isEqualTo("1234567890")
    }

    @Test
    fun `should create SocialId from Kakao Long ID`() {
        val kakaoId = 3456789012345L
        val socialId = SocialId.fromKakaoId(kakaoId)

        assertThat(socialId.value).isEqualTo("3456789012345")
    }

    @Test
    fun `should throw InvalidSocialIdException when value is empty`() {
        assertThatThrownBy { SocialId("") }
            .isInstanceOf(InvalidSocialIdException::class.java)
    }

    @Test
    fun `should throw InvalidSocialIdException when value is blank`() {
        assertThatThrownBy { SocialId("   ") }
            .isInstanceOf(InvalidSocialIdException::class.java)
    }

    @Test
    fun `should be equal when values are same`() {
        val socialId1 = SocialId("123456")
        val socialId2 = SocialId("123456")

        assertThat(socialId1).isEqualTo(socialId2)
        assertThat(socialId1.hashCode()).isEqualTo(socialId2.hashCode())
    }

    @Test
    fun `should not be equal when values are different`() {
        val socialId1 = SocialId("123456")
        val socialId2 = SocialId("654321")

        assertThat(socialId1).isNotEqualTo(socialId2)
    }
}
