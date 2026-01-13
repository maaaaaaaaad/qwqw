package com.mad.jellomarkserver.beautishop.port.driving

import com.mad.jellomarkserver.beautishop.core.domain.model.Beautishop

fun interface UpdateBeautishopUseCase {
    fun update(command: UpdateBeautishopCommand): Beautishop
}
