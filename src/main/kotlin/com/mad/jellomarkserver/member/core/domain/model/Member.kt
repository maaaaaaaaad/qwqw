package com.mad.jellomarkserver.member.core.domain.model

import java.time.Clock
import java.time.Instant

class Member private constructor(
    val id: MemberId,
    val nickname: Nickname,
    val email: Email,
    val memberType: MemberType,
    val businessRegistrationNumber: BusinessRegistrationNumber?,
    val createdAt: Instant,
    val updatedAt: Instant
) {
    companion object {
        fun create(nickname: Nickname, email: Email, clock: Clock = Clock.systemUTC()): Member {
            val now = Instant.now(clock)
            return Member(MemberId.new(), nickname, email, MemberType.CONSUMER, null, now, now)
        }

        fun create(
            nickname: Nickname,
            email: Email,
            memberType: MemberType,
            businessRegistrationNumber: BusinessRegistrationNumber?,
            clock: Clock = Clock.systemUTC()
        ): Member {
            val now = Instant.now(clock)
            return Member(MemberId.new(), nickname, email, memberType, businessRegistrationNumber, now, now)
        }

        fun reconstruct(
            id: MemberId,
            nickname: Nickname,
            email: Email,
            memberType: MemberType,
            businessRegistrationNumber: BusinessRegistrationNumber?,
            createdAt: Instant,
            updatedAt: Instant
        ): Member {
            return Member(id, nickname, email, memberType, businessRegistrationNumber, createdAt, updatedAt)
        }

        fun reconstruct(
            id: MemberId,
            nickname: Nickname,
            email: Email,
            memberType: MemberType,
            createdAt: Instant,
            updatedAt: Instant
        ): Member {
            return Member(id, nickname, email, memberType, null, createdAt, updatedAt)
        }
    }

    fun changeNickname(newNickname: Nickname, clock: Clock = Clock.systemUTC()): Member {
        val now = Instant.now(clock)
        return Member(id, newNickname, email, memberType, businessRegistrationNumber, createdAt, now)
    }

    fun changeEmail(newEmail: Email, clock: Clock = Clock.systemUTC()): Member {
        val now = Instant.now(clock)
        return Member(id, nickname, newEmail, memberType, businessRegistrationNumber, createdAt, now)
    }

    fun changeBusinessRegistrationNumber(
        newBusinessRegistrationNumber: BusinessRegistrationNumber?, clock: Clock = Clock.systemUTC()
    ): Member {
        val now = Instant.now(clock)
        return Member(id, nickname, email, memberType, newBusinessRegistrationNumber, createdAt, now)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Member) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}
