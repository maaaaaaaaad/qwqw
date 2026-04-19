package com.mad.jellomarkserver.owner.port.driving

data class WithdrawOwnerCommand(
    val email: String,
    val password: String,
    val reason: String
)
