package com.mad.jellomarkserver.auth.port.driving

import com.mad.jellomarkserver.auth.core.domain.model.Auth

fun interface SignUpAuthUseCase {
    fun signUp(command: SignUpAuthCommand): Auth
}
