package com.mad.jellomarkserver.apigateway.adapter.driving.web.request

data class CreateBeautishopRequest(
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
