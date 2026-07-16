package com.mad.jellomarkserver.designer.port.driving

data class CreateDesignerCommand(
    val shopId: String,
    val ownerId: String,
    val name: String,
    val nickname: String?,
    val intro: String?,
    val photoUrls: List<String>?
)
