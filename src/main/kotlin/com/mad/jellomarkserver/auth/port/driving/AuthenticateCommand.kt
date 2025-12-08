package com.mad.jellomarkserver.auth.port.driving

data class AuthenticateCommand(
    val email: String,
    val password: String
)
