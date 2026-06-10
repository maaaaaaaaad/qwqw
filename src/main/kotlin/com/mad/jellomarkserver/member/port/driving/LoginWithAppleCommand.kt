package com.mad.jellomarkserver.member.port.driving

data class LoginWithAppleCommand(
    val identityToken: String,
    val fullName: String? = null
)
