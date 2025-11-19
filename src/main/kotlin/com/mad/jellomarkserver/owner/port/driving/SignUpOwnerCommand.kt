package com.mad.jellomarkserver.owner.port.driving

data class SignUpOwnerCommand(
    val businessNumber: String,
    val phoneNumber: String,
    val nickname: String,
)
