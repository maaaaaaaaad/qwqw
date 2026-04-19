package com.mad.jellomarkserver.member.port.driving

data class WithdrawMemberCommand(
    val socialProvider: String,
    val socialId: String,
    val reason: String
)
