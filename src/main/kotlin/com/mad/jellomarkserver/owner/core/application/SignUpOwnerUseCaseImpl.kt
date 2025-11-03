package com.mad.jellomarkserver.owner.core.application

import com.mad.jellomarkserver.owner.core.domain.model.BusinessNumber
import com.mad.jellomarkserver.owner.core.domain.model.Owner
import com.mad.jellomarkserver.owner.core.domain.model.PhoneNumber
import com.mad.jellomarkserver.owner.port.driven.OwnerPort
import com.mad.jellomarkserver.owner.port.driving.SignUpOwnerCommand
import com.mad.jellomarkserver.owner.port.driving.SignUpOwnerUseCase
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional

@Service
class SignUpOwnerUseCaseImpl(
    private val ownerPort: OwnerPort
) : SignUpOwnerUseCase {

    @Transactional(isolation = Isolation.READ_COMMITTED)
    override fun signUp(command: SignUpOwnerCommand): Owner {
        val businessNumber = BusinessNumber.of(command.businessNumber)
        val phoneNumber = PhoneNumber.of(command.phoneNumber)
        val owner = Owner.create(businessNumber, phoneNumber)
        return ownerPort.save(owner)
    }
}
