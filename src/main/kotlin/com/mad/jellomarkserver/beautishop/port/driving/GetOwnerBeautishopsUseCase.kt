package com.mad.jellomarkserver.beautishop.port.driving

import com.mad.jellomarkserver.beautishop.core.domain.model.Beautishop

fun interface GetOwnerBeautishopsUseCase {
    fun execute(command: GetOwnerBeautishopsCommand): List<Beautishop>
}
