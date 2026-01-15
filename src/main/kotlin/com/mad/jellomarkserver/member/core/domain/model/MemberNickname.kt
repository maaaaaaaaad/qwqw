package com.mad.jellomarkserver.member.core.domain.model

import com.mad.jellomarkserver.member.core.domain.exception.InvalidMemberNicknameException

@JvmInline
value class MemberNickname private constructor(val value: String) {
    companion object {
        private val pattern = Regex("^\\S{2,20}$")

        fun of(input: String): MemberNickname {
            val processed = input.trim().replace("\\s+".toRegex(), "")
            try {
                require(processed.isNotBlank())
                require(pattern.matches(processed))
                return MemberNickname(processed)
            } catch (ex: IllegalArgumentException) {
                throw InvalidMemberNicknameException(processed)
            }
        }

        fun generate(baseName: String, suffix: String = ""): MemberNickname {
            val processed = baseName.trim().replace("\\s+".toRegex(), "")
            val truncated = if (processed.length > 12) processed.take(12) else processed
            val result = if (suffix.isNotEmpty()) "$truncated$suffix" else truncated
            return if (result.length >= 2) {
                MemberNickname(result.take(20))
            } else {
                MemberNickname("user$suffix")
            }
        }
    }
}
