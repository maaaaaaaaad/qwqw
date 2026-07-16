package com.mad.jellomarkserver.apigateway.adapter.driving.web.request

data class CreateDesignerRequest(
    val name: String,
    val nickname: String?,
    val intro: String?,
    val photoUrls: List<String>?
)
