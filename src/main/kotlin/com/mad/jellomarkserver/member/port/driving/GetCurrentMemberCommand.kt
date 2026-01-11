package com.mad.jellomarkserver.member.port.driving

data class GetCurrentMemberCommand(
    val socialProvider: String,
    val socialId: String
)
