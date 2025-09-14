package com.mad.jellomarkserver.member.domain.model

import java.time.Clock
import java.time.Instant

class Member private constructor(
    val id: MemberId,
    val nickname: Nickname,
    val email: Email,
    val createdAt: Instant,
    val updatedAt: Instant
) {
    companion object {
        fun create(nickname: Nickname, email: Email, clock: Clock = Clock.systemUTC()): Member {
            val now = Instant.now(clock)
            return Member(MemberId.new(), nickname, email, now, now)
        }

        fun reconstruct(
            id: MemberId,
            nickname: Nickname,
            email: Email,
            createdAt: Instant,
            updatedAt: Instant
        ): Member {
            return Member(id, nickname, email, createdAt, updatedAt)
        }
    }

    fun changeNickname(newNickname: Nickname, clock: Clock = Clock.systemUTC()): Member {
        val now = Instant.now(clock)
        return Member(id, newNickname, email, createdAt, now)
    }

    fun changeEmail(newEmail: Email, clock: Clock = Clock.systemUTC()): Member {
        val now = Instant.now(clock)
        return Member(id, nickname, newEmail, createdAt, now)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Member) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}
