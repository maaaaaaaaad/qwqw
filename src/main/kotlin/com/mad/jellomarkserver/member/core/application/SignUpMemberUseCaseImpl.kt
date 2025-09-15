package com.mad.jellomarkserver.member.core.application

import com.mad.jellomarkserver.member.core.domain.exception.DuplicateEmailException
import com.mad.jellomarkserver.member.core.domain.model.BusinessRegistrationNumber
import com.mad.jellomarkserver.member.core.domain.model.Email
import com.mad.jellomarkserver.member.core.domain.model.Member
import com.mad.jellomarkserver.member.core.domain.model.Nickname
import com.mad.jellomarkserver.member.port.driven.MemberPort
import com.mad.jellomarkserver.member.port.driving.SignUpMemberCommand
import com.mad.jellomarkserver.member.port.driving.SignUpMemberUseCase
import org.springframework.stereotype.Service

@Service
class SignUpMemberUseCaseImpl(
    private val memberPort: MemberPort
) : SignUpMemberUseCase {
    override fun signUp(command: SignUpMemberCommand): Member {
        val email = Email.of(command.email)
        if (memberPort.existsByEmail(email)) throw DuplicateEmailException(email.value)
        val nickname = Nickname.of(command.nickname)
        val brn = command.businessRegistrationNumber?.let { BusinessRegistrationNumber.of(it) }
        val member = Member.create(nickname, email, brn)
        return memberPort.save(member)
    }
}
