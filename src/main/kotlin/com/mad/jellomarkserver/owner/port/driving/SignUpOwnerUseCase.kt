package com.mad.jellomarkserver.owner.port.driving

import com.mad.jellomarkserver.owner.core.domain.model.Owner

fun interface SignUpOwnerUseCase {
    fun signUp(command: SignUpOwnerCommand): Owner
}
