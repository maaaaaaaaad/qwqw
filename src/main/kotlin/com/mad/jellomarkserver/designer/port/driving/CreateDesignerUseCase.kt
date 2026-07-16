package com.mad.jellomarkserver.designer.port.driving

import com.mad.jellomarkserver.designer.core.domain.model.Designer

fun interface CreateDesignerUseCase {
    fun create(command: CreateDesignerCommand): Designer
}
