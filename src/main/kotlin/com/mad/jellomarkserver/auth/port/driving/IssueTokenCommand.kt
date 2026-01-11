package com.mad.jellomarkserver.auth.port.driving

data class IssueTokenCommand(
    val identifier: String,
    val userType: String
)
