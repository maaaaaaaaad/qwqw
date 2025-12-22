package com.mad.jellomarkserver.owner.core.application

import com.mad.jellomarkserver.owner.core.domain.model.*
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
        val ownerPhoneNumber = OwnerPhoneNumber.of(command.phoneNumber)
        val ownerNickname = OwnerNickname.of(command.nickname)
        val ownerEmail = OwnerEmail.of(command.email)
        val owner = Owner.create(businessNumber, ownerPhoneNumber, ownerNickname, ownerEmail)
        return ownerPort.save(owner)
    }
}
