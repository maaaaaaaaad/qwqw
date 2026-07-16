package com.mad.jellomarkserver.designer.port.driving

data class DeleteDesignerCommand(
    val designerId: String,
    val ownerId: String
)
