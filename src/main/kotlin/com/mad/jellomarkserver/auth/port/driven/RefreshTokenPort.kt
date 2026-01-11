package com.mad.jellomarkserver.auth.port.driven

import com.mad.jellomarkserver.auth.core.domain.model.RefreshToken

interface RefreshTokenPort {
    fun save(refreshToken: RefreshToken): RefreshToken
    fun findByIdentifier(identifier: String): RefreshToken?
    fun findByToken(token: String): RefreshToken?
    fun deleteByIdentifier(identifier: String)
}
