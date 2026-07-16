package com.mad.jellomarkserver.designer.port.driving

import com.mad.jellomarkserver.designer.core.domain.model.Designer

fun interface ListDesignersUseCase {
    fun execute(command: ListDesignersCommand): List<Designer>
}
