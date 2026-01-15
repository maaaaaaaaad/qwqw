package com.mad.jellomarkserver.member.core.application

import com.mad.jellomarkserver.auth.core.domain.model.TokenPair
import com.mad.jellomarkserver.auth.port.driven.KakaoApiClient
import com.mad.jellomarkserver.auth.port.driving.IssueTokenCommand
import com.mad.jellomarkserver.auth.port.driving.IssueTokenUseCase
import com.mad.jellomarkserver.member.core.domain.model.*
import com.mad.jellomarkserver.member.port.driven.MemberPort
import com.mad.jellomarkserver.member.port.driving.LoginWithKakaoCommand
import com.mad.jellomarkserver.member.port.driving.LoginWithKakaoUseCase
import org.springframework.stereotype.Service
import java.time.Clock

@Service
class LoginWithKakaoUseCaseImpl(
    private val kakaoApiClient: KakaoApiClient,
    private val memberPort: MemberPort,
    private val issueTokenUseCase: IssueTokenUseCase,
    private val clock: Clock = Clock.systemUTC()
) : LoginWithKakaoUseCase {

    override fun execute(command: LoginWithKakaoCommand): TokenPair {
        kakaoApiClient.verifyAccessToken(command.kakaoAccessToken)

        val userInfo = kakaoApiClient.getUserInfo(command.kakaoAccessToken)

        val socialId = SocialId.fromKakaoId(userInfo.id)

        val member = memberPort.findBySocial(SocialProvider.KAKAO, socialId)
            ?: createNewMember(socialId, userInfo.nickname)

        return issueTokenUseCase.execute(
            IssueTokenCommand(
                identifier = socialId.value,
                userType = "MEMBER",
                socialProvider = "KAKAO",
                socialId = socialId.value
            )
        )
    }

    private fun createNewMember(socialId: SocialId, kakaoNickname: String): Member {
        val displayName = MemberDisplayName.of(kakaoNickname)
        val uniqueSuffix = socialId.value.takeLast(6)
        val memberNickname = MemberNickname.generate(kakaoNickname, uniqueSuffix)

        val member = Member.create(
            socialProvider = SocialProvider.KAKAO,
            socialId = socialId,
            memberNickname = memberNickname,
            displayName = displayName,
            clock = clock
        )
        return memberPort.save(member)
    }
}
