package com.mad.jellomarkserver.auth.port.driven

import com.mad.jellomarkserver.auth.core.domain.model.AuthEmail
import com.mad.jellomarkserver.auth.core.domain.model.RefreshToken

interface RefreshTokenPort {
    fun save(refreshToken: RefreshToken): RefreshToken
    fun findByEmail(email: AuthEmail): RefreshToken?
    fun findByToken(token: String): RefreshToken?
    fun deleteByEmail(email: AuthEmail)
}
