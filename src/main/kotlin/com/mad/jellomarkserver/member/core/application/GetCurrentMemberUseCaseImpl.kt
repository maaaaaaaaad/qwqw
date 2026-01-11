package com.mad.jellomarkserver.member.core.application

import com.mad.jellomarkserver.member.core.domain.exception.MemberNotFoundException
import com.mad.jellomarkserver.member.core.domain.model.Member
import com.mad.jellomarkserver.member.core.domain.model.SocialId
import com.mad.jellomarkserver.member.core.domain.model.SocialProvider
import com.mad.jellomarkserver.member.port.driven.MemberPort
import com.mad.jellomarkserver.member.port.driving.GetCurrentMemberCommand
import com.mad.jellomarkserver.member.port.driving.GetCurrentMemberUseCase
import org.springframework.stereotype.Service

@Service
class GetCurrentMemberUseCaseImpl(
    private val memberPort: MemberPort
) : GetCurrentMemberUseCase {
    override fun execute(command: GetCurrentMemberCommand): Member {
        val provider = SocialProvider.valueOf(command.socialProvider)
        val socialId = SocialId(command.socialId)
        return memberPort.findBySocial(provider, socialId)
            ?: throw MemberNotFoundException("${command.socialProvider}:${command.socialId}")
    }
}
