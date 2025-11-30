package com.mad.jellomarkserver.auth.port.driving

data class SignUpAuthCommand(
    val email: String,
    val password: String,
    val userType: String
)
