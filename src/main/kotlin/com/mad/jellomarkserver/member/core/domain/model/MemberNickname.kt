package com.mad.jellomarkserver.member.core.domain.model

import com.mad.jellomarkserver.member.core.domain.exception.InvalidMemberNicknameException

@JvmInline
value class MemberNickname private constructor(val value: String) {
    companion object {
        private val pattern = Regex("^\\S{2,8}$")
        fun of(input: String): MemberNickname {
            val trimmed = input.trim()
            try {
                require(trimmed.isNotBlank())
                require(pattern.matches(trimmed))
                return MemberNickname(trimmed)
            } catch (ex: IllegalArgumentException) {
                throw InvalidMemberNicknameException(trimmed)
            }
        }
    }
}
