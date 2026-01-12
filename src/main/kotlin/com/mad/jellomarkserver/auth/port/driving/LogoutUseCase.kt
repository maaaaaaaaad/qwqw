package com.mad.jellomarkserver.auth.port.driving

fun interface LogoutUseCase {
    fun execute(command: LogoutCommand)
}
