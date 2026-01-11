package com.mad.jellomarkserver.member.core.domain.model

import java.time.Clock
import java.time.Instant

class Member private constructor(
    val id: MemberId,
    val socialProvider: SocialProvider,
    val socialId: SocialId,
    val memberNickname: MemberNickname,
    val createdAt: Instant,
    val updatedAt: Instant
) {
    companion object {
        fun create(
            socialProvider: SocialProvider,
            socialId: SocialId,
            memberNickname: MemberNickname,
            clock: Clock = Clock.systemUTC()
        ): Member {
            val now = Instant.now(clock)
            return Member(MemberId.new(), socialProvider, socialId, memberNickname, now, now)
        }

        fun reconstruct(
            id: MemberId,
            socialProvider: SocialProvider,
            socialId: SocialId,
            memberNickname: MemberNickname,
            createdAt: Instant,
            updatedAt: Instant
        ): Member {
            return Member(id, socialProvider, socialId, memberNickname, createdAt, updatedAt)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Member) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}
