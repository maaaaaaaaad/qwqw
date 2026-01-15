package com.mad.jellomarkserver.member.core.domain.model

@JvmInline
value class MemberDisplayName(val value: String) {
    companion object {
        fun of(input: String): MemberDisplayName {
            return MemberDisplayName(input.trim())
        }
    }
}
