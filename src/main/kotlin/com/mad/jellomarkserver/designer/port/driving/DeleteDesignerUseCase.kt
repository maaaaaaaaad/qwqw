package com.mad.jellomarkserver.designer.port.driving

fun interface DeleteDesignerUseCase {
    fun delete(command: DeleteDesignerCommand)
}
