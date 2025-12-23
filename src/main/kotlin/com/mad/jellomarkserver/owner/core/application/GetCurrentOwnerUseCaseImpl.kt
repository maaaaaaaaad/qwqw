package com.mad.jellomarkserver.owner.core.application

import com.mad.jellomarkserver.owner.core.domain.exception.OwnerNotFoundException
import com.mad.jellomarkserver.owner.core.domain.model.Owner
import com.mad.jellomarkserver.owner.core.domain.model.OwnerEmail
import com.mad.jellomarkserver.owner.port.driven.OwnerPort
import com.mad.jellomarkserver.owner.port.driving.GetCurrentOwnerCommand
import com.mad.jellomarkserver.owner.port.driving.GetCurrentOwnerUseCase
import org.springframework.stereotype.Service

@Service
class GetCurrentOwnerUseCaseImpl(
    private val ownerPort: OwnerPort
) : GetCurrentOwnerUseCase {
    override fun execute(command: GetCurrentOwnerCommand): Owner {
        val email = OwnerEmail.of(command.email)
        return ownerPort.findByEmail(email) ?: throw OwnerNotFoundException(command.email)
    }
}
