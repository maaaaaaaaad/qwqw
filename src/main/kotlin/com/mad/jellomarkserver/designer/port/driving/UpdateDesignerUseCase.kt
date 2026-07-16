package com.mad.jellomarkserver.designer.port.driving

import com.mad.jellomarkserver.designer.core.domain.model.Designer

fun interface UpdateDesignerUseCase {
    fun update(command: UpdateDesignerCommand): Designer
}
