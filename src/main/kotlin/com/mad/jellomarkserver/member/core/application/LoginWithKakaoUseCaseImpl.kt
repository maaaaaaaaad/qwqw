package com.mad.jellomarkserver.member.core.application

import com.mad.jellomarkserver.auth.core.domain.model.TokenPair
import com.mad.jellomarkserver.auth.port.driven.KakaoApiClient
import com.mad.jellomarkserver.auth.port.driving.IssueTokenCommand
import com.mad.jellomarkserver.auth.port.driving.IssueTokenUseCase
import com.mad.jellomarkserver.member.core.domain.model.Member
import com.mad.jellomarkserver.member.core.domain.model.MemberNickname
import com.mad.jellomarkserver.member.core.domain.model.SocialId
import com.mad.jellomarkserver.member.core.domain.model.SocialProvider
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
        // 1. Verify Kakao access token
        kakaoApiClient.verifyAccessToken(command.kakaoAccessToken)

        // 2. Get Kakao user info
        val userInfo = kakaoApiClient.getUserInfo(command.kakaoAccessToken)

        val socialId = SocialId.fromKakaoId(userInfo.id)

        // 3. Find existing member or create new one
        val member = memberPort.findBySocial(SocialProvider.KAKAO, socialId)
            ?: createNewMember(socialId, userInfo.nickname)

        // 4. Issue JWT tokens using socialId as identifier
        return issueTokenUseCase.execute(
            IssueTokenCommand(
                identifier = userInfo.id.toString(),
                userType = "MEMBER"
            )
        )
    }

    private fun createNewMember(socialId: SocialId, nickname: String): Member {
        val member = Member.create(
            socialProvider = SocialProvider.KAKAO,
            socialId = socialId,
            memberNickname = MemberNickname.of(nickname),
            clock = clock
        )
        return memberPort.save(member)
    }
}
