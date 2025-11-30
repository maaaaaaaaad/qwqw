package com.mad.jellomarkserver.auth.core.domain.model

import java.time.Clock
import java.time.Instant

class Auth private constructor(
    val id: AuthId,
    val email: AuthEmail,
    val hashedPassword: HashedPassword,
    val userType: UserType,
    val createdAt: Instant,
    val updatedAt: Instant
) {
    companion object {
        fun create(
            email: AuthEmail,
            rawPassword: RawPassword,
            userType: UserType,
            clock: Clock = Clock.systemUTC()
        ): Auth {
            val now = Instant.now(clock)
            val hashedPassword = HashedPassword.fromRaw(rawPassword)
            return Auth(AuthId.new(), email, hashedPassword, userType, now, now)
        }

        fun reconstruct(
            id: AuthId,
            email: AuthEmail,
            hashedPassword: HashedPassword,
            userType: UserType,
            createdAt: Instant,
            updatedAt: Instant
        ): Auth {
            return Auth(id, email, hashedPassword, userType, createdAt, updatedAt)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Auth) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}
