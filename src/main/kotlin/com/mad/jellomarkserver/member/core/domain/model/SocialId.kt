package com.mad.jellomarkserver.member.core.domain.model

import com.mad.jellomarkserver.member.core.domain.exception.InvalidSocialIdException

data class SocialId(val value: String) {
    init {
        if (value.isBlank()) {
            throw InvalidSocialIdException(value)
        }
    }

    companion object {
        fun fromKakaoId(kakaoId: Long): SocialId {
            return SocialId(kakaoId.toString())
        }
    }
}
