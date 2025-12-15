package com.mad.jellomarkserver.auth.port.driving

import com.mad.jellomarkserver.auth.core.domain.model.TokenPair

fun interface IssueTokenUseCase {
    fun execute(command: IssueTokenCommand): TokenPair
}
