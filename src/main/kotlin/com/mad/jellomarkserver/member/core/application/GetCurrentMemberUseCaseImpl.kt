package com.mad.jellomarkserver.member.core.application

import com.mad.jellomarkserver.member.core.domain.exception.MemberNotFoundException
import com.mad.jellomarkserver.member.core.domain.model.Member
import com.mad.jellomarkserver.member.core.domain.model.MemberEmail
import com.mad.jellomarkserver.member.port.driven.MemberPort
import com.mad.jellomarkserver.member.port.driving.GetCurrentMemberCommand
import com.mad.jellomarkserver.member.port.driving.GetCurrentMemberUseCase
import org.springframework.stereotype.Service

@Service
class GetCurrentMemberUseCaseImpl(
    private val memberPort: MemberPort
) : GetCurrentMemberUseCase {
    override fun execute(command: GetCurrentMemberCommand): Member {
        val email = MemberEmail.of(command.email)
        return memberPort.findByEmail(email) ?: throw MemberNotFoundException(command.email)
    }
}
