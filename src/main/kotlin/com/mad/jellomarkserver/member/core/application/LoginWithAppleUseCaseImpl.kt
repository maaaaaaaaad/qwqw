package com.mad.jellomarkserver.member.core.application

import com.mad.jellomarkserver.auth.core.domain.model.TokenPair
import com.mad.jellomarkserver.auth.port.driven.AppleApiClient
import com.mad.jellomarkserver.auth.port.driving.IssueTokenCommand
import com.mad.jellomarkserver.auth.port.driving.IssueTokenUseCase
import com.mad.jellomarkserver.member.core.domain.model.Member
import com.mad.jellomarkserver.member.core.domain.model.MemberDisplayName
import com.mad.jellomarkserver.member.core.domain.model.MemberNickname
import com.mad.jellomarkserver.member.core.domain.model.SocialId
import com.mad.jellomarkserver.member.core.domain.model.SocialProvider
import com.mad.jellomarkserver.member.port.driven.MemberPort
import com.mad.jellomarkserver.member.port.driving.LoginWithAppleCommand
import com.mad.jellomarkserver.member.port.driving.LoginWithAppleUseCase
import org.springframework.stereotype.Service
import java.time.Clock

@Service
class LoginWithAppleUseCaseImpl(
    private val appleApiClient: AppleApiClient,
    private val memberPort: MemberPort,
    private val issueTokenUseCase: IssueTokenUseCase,
    private val clock: Clock = Clock.systemUTC()
) : LoginWithAppleUseCase {

    override fun execute(command: LoginWithAppleCommand): TokenPair {
        val userInfo = appleApiClient.verifyIdentityToken(command.identityToken)

        val socialId = SocialId.fromAppleSub(userInfo.sub)

        memberPort.findBySocial(SocialProvider.APPLE, socialId)
            ?: createNewMember(socialId, command.fullName)

        return issueTokenUseCase.execute(
            IssueTokenCommand(
                identifier = socialId.value,
                userType = "MEMBER",
                socialProvider = "APPLE",
                socialId = socialId.value
            )
        )
    }

    private fun createNewMember(socialId: SocialId, fullName: String?): Member {
        val baseName = fullName?.trim()?.takeIf { it.isNotEmpty() } ?: "Apple User"
        val displayName = MemberDisplayName.of(baseName)
        val uniqueSuffix = socialId.value.replace(".", "").takeLast(6)
        val memberNickname = MemberNickname.generate(baseName, uniqueSuffix)

        val member = Member.create(
            socialProvider = SocialProvider.APPLE,
            socialId = socialId,
            memberNickname = memberNickname,
            displayName = displayName,
            clock = clock
        )
        return memberPort.save(member)
    }
}
