package com.mad.jellomarkserver.member.port.driving

import com.mad.jellomarkserver.auth.core.domain.model.TokenPair

fun interface LoginWithKakaoUseCase {
    fun execute(command: LoginWithKakaoCommand): TokenPair
}
