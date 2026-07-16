package com.mad.jellomarkserver.designer.port.driving

data class UpdateDesignerCommand(
    val designerId: String,
    val ownerId: String,
    val name: String?,
    val nickname: String?,
    val intro: String?,
    val photoUrls: List<String>?
)
