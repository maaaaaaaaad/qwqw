package com.mad.jellomarkserver.beautishop.core.application

import com.mad.jellomarkserver.beautishop.core.domain.model.Beautishop
import com.mad.jellomarkserver.beautishop.port.driven.BeautishopPort
import com.mad.jellomarkserver.beautishop.port.driving.GetOwnerBeautishopsCommand
import com.mad.jellomarkserver.beautishop.port.driving.GetOwnerBeautishopsUseCase
import com.mad.jellomarkserver.owner.core.domain.exception.OwnerNotFoundException
import com.mad.jellomarkserver.owner.core.domain.model.OwnerEmail
import com.mad.jellomarkserver.owner.port.driven.OwnerPort
import org.springframework.stereotype.Service

@Service
class GetOwnerBeautishopsUseCaseImpl(
    private val ownerPort: OwnerPort,
    private val beautishopPort: BeautishopPort
) : GetOwnerBeautishopsUseCase {

    override fun execute(command: GetOwnerBeautishopsCommand): List<Beautishop> {
        val email = OwnerEmail.of(command.email)
        val owner = ownerPort.findByEmail(email) ?: throw OwnerNotFoundException(command.email)
        return beautishopPort.findByOwnerId(owner.id)
    }
}
