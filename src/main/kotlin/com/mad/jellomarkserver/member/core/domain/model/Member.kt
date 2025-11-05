package com.mad.jellomarkserver.member.core.domain.model

import java.time.Clock
import java.time.Instant

class Member private constructor(
    val id: MemberId,
    val memberNickname: MemberNickname,
    val memberEmail: MemberEmail,
    val createdAt: Instant,
    val updatedAt: Instant
) {
    companion object {
        fun create(memberNickname: MemberNickname, memberEmail: MemberEmail, clock: Clock = Clock.systemUTC()): Member {
            val now = Instant.now(clock)
            return Member(MemberId.new(), memberNickname, memberEmail, now, now)
        }

        fun reconstruct(
            id: MemberId,
            memberNickname: MemberNickname,
            memberEmail: MemberEmail,
            createdAt: Instant,
            updatedAt: Instant
        ): Member {
            return Member(id, memberNickname, memberEmail, createdAt, updatedAt)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Member) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}
