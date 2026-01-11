package com.mad.jellomarkserver.auth.core.domain.model

import java.time.Clock
import java.time.Instant

class RefreshToken private constructor(
    val id: RefreshTokenId,
    val identifier: String,
    val userType: String,
    val token: String,
    val expiresAt: Instant,
    val createdAt: Instant
) {
    companion object {
        fun create(
            identifier: String,
            userType: String,
            token: String,
            expirationMillis: Long,
            clock: Clock = Clock.systemUTC()
        ): RefreshToken {
            val now = Instant.now(clock)
            val expiresAt = now.plusMillis(expirationMillis)
            return RefreshToken(RefreshTokenId.new(), identifier, userType, token, expiresAt, now)
        }

        fun reconstruct(
            id: RefreshTokenId,
            identifier: String,
            userType: String,
            token: String,
            expiresAt: Instant,
            createdAt: Instant
        ): RefreshToken {
            return RefreshToken(id, identifier, userType, token, expiresAt, createdAt)
        }
    }

    fun isExpired(clock: Clock = Clock.systemUTC()): Boolean {
        return Instant.now(clock).isAfter(expiresAt)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is RefreshToken) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}
