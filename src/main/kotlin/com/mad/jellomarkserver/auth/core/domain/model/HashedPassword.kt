package com.mad.jellomarkserver.auth.core.domain.model

import org.mindrot.jbcrypt.BCrypt

@JvmInline
value class HashedPassword private constructor(val value: String) {
    companion object {
        private const val BCRYPT_STRENGTH = 10

        fun fromRaw(rawPassword: RawPassword): HashedPassword {
            val hash = BCrypt.hashpw(rawPassword.value, BCrypt.gensalt(BCRYPT_STRENGTH))
            return HashedPassword(hash)
        }

        fun from(hash: String): HashedPassword {
            return HashedPassword(hash)
        }
    }

    fun matches(rawPassword: RawPassword): Boolean {
        return BCrypt.checkpw(rawPassword.value, value)
    }
}
