package com.mad.jellomarkserver.auth.core.application

import com.mad.jellomarkserver.auth.port.driven.RefreshTokenPort
import com.mad.jellomarkserver.auth.port.driving.LogoutCommand
import com.mad.jellomarkserver.auth.port.driving.LogoutUseCase
import org.springframework.stereotype.Service

@Service
class LogoutUseCaseImpl(
    private val refreshTokenPort: RefreshTokenPort
) : LogoutUseCase {
    override fun execute(command: LogoutCommand) {
        refreshTokenPort.deleteByIdentifier(command.identifier)
    }
}
