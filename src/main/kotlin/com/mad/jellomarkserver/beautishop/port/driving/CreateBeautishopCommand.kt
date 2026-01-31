package com.mad.jellomarkserver.beautishop.port.driving

data class CreateBeautishopCommand(
    val ownerId: String,
    val shopName: String,
    val shopRegNum: String,
    val shopPhoneNumber: String,
    val shopAddress: String,
    val latitude: Double,
    val longitude: Double,
    val operatingTime: Map<String, String>,
    val shopDescription: String?,
    val shopImages: List<String>?
)
