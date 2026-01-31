package com.mad.jellomarkserver.beautishop.port.driving

data class UpdateBeautishopCommand(
    val shopId: String,
    val ownerId: String,
    val operatingTime: Map<String, String>?,
    val shopDescription: String?,
    val shopImages: List<String>?
)
