package com.mad.jellomarkserver.beautishop.port.driving

data class DeleteBeautishopCommand(
    val shopId: String,
    val ownerId: String
)
