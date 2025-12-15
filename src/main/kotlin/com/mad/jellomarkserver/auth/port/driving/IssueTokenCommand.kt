package com.mad.jellomarkserver.auth.port.driving

data class IssueTokenCommand(
    val email: String,
    val userType: String
)
