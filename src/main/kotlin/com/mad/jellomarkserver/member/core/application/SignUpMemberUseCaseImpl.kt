package com.mad.jellomarkserver.member.core.application

import com.mad.jellomarkserver.member.core.domain.model.MemberEmail
import com.mad.jellomarkserver.member.core.domain.model.Member
import com.mad.jellomarkserver.member.core.domain.model.MemberNickname
import com.mad.jellomarkserver.member.port.driven.MemberPort
import com.mad.jellomarkserver.member.port.driving.SignUpMemberCommand
import com.mad.jellomarkserver.member.port.driving.SignUpMemberUseCase
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional

@Service
class SignUpMemberUseCaseImpl(
    private val memberPort: MemberPort
) : SignUpMemberUseCase {

    @Transactional(isolation = Isolation.READ_COMMITTED)
    override fun signUp(command: SignUpMemberCommand): Member {
        val memberEmail = MemberEmail.of(command.email)
        val memberNickname = MemberNickname.of(command.nickname)
        val member = Member.create(memberNickname, memberEmail)
        return memberPort.save(member)
    }
}
