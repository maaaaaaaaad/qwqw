package com.mad.jellomarkserver.owner.port.driving

import com.mad.jellomarkserver.owner.core.domain.model.Owner

fun interface GetCurrentOwnerUseCase {
    fun execute(command: GetCurrentOwnerCommand): Owner
}
