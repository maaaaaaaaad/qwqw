package com.mad.jellomarkserver.auth.port.driving

import com.mad.jellomarkserver.auth.core.domain.model.TokenPair

fun interface RefreshTokenUseCase {
    fun execute(command: RefreshTokenCommand): TokenPair
}
