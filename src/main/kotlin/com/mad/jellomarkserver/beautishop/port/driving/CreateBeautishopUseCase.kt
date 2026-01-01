package com.mad.jellomarkserver.beautishop.port.driving

import com.mad.jellomarkserver.beautishop.core.domain.model.Beautishop

fun interface CreateBeautishopUseCase {
    fun create(command: CreateBeautishopCommand): Beautishop
}
