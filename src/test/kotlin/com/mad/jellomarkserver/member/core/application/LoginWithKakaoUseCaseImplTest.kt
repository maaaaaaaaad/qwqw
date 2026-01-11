package com.mad.jellomarkserver.member.core.application

import com.mad.jellomarkserver.auth.adapter.driven.kakao.KakaoTokenInfo
import com.mad.jellomarkserver.auth.adapter.driven.kakao.KakaoUserInfo
import com.mad.jellomarkserver.auth.core.domain.exception.InvalidKakaoTokenException
import com.mad.jellomarkserver.auth.core.domain.exception.KakaoApiException
import com.mad.jellomarkserver.auth.core.domain.model.TokenPair
import com.mad.jellomarkserver.auth.port.driven.KakaoApiClient
import com.mad.jellomarkserver.auth.port.driving.IssueTokenCommand
import com.mad.jellomarkserver.auth.port.driving.IssueTokenUseCase
import com.mad.jellomarkserver.member.core.domain.model.*
import com.mad.jellomarkserver.member.port.driven.MemberPort
import com.mad.jellomarkserver.member.port.driving.LoginWithKakaoCommand
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import java.time.Clock
import java.time.Instant
import java.time.ZoneId

class LoginWithKakaoUseCaseImplTest {

    private lateinit var kakaoApiClient: KakaoApiClient
    private lateinit var memberPort: MemberPort
    private lateinit var issueTokenUseCase: IssueTokenUseCase
    private lateinit var useCase: LoginWithKakaoUseCaseImpl

    private val fixedClock = Clock.fixed(Instant.parse("2025-01-01T00:00:00Z"), ZoneId.of("UTC"))

    @BeforeEach
    fun setUp() {
        kakaoApiClient = mock()
        memberPort = mock()
        issueTokenUseCase = mock()
        useCase = LoginWithKakaoUseCaseImpl(kakaoApiClient, memberPort, issueTokenUseCase, fixedClock)
    }

    @Test
    fun `should login existing member with Kakao and return tokens`() {
        val kakaoAccessToken = "valid_kakao_access_token"
        val kakaoId = 3456789012345L
        val command = LoginWithKakaoCommand(kakaoAccessToken)

        val tokenInfo = KakaoTokenInfo(id = kakaoId, expiresIn = 3600, appId = 123456)
        val userInfo = KakaoUserInfo(id = kakaoId, nickname = "기존유저")

        val existingMember = Member.reconstruct(
            id = MemberId.new(),
            socialProvider = SocialProvider.KAKAO,
            socialId = SocialId.fromKakaoId(kakaoId),
            memberNickname = MemberNickname.of("기존유저"),
            createdAt = Instant.parse("2024-01-01T00:00:00Z"),
            updatedAt = Instant.parse("2024-01-01T00:00:00Z")
        )

        val expectedTokenPair = TokenPair(accessToken = "access_token", refreshToken = "refresh_token")

        whenever(kakaoApiClient.verifyAccessToken(kakaoAccessToken)).thenReturn(tokenInfo)
        whenever(kakaoApiClient.getUserInfo(kakaoAccessToken)).thenReturn(userInfo)
        whenever(memberPort.findBySocial(SocialProvider.KAKAO, SocialId.fromKakaoId(kakaoId))).thenReturn(existingMember)
        whenever(issueTokenUseCase.execute(IssueTokenCommand(identifier = kakaoId.toString(), userType = "MEMBER")))
            .thenReturn(expectedTokenPair)

        val result = useCase.execute(command)

        assertThat(result.accessToken).isEqualTo("access_token")
        assertThat(result.refreshToken).isEqualTo("refresh_token")
        verify(memberPort, never()).save(any())
    }

    @Test
    fun `should register new member with Kakao and return tokens`() {
        val kakaoAccessToken = "valid_kakao_access_token"
        val kakaoId = 9876543210123L
        val command = LoginWithKakaoCommand(kakaoAccessToken)

        val tokenInfo = KakaoTokenInfo(id = kakaoId, expiresIn = 3600, appId = 123456)
        val userInfo = KakaoUserInfo(id = kakaoId, nickname = "신규유저")

        val expectedTokenPair = TokenPair(accessToken = "new_access_token", refreshToken = "new_refresh_token")

        whenever(kakaoApiClient.verifyAccessToken(kakaoAccessToken)).thenReturn(tokenInfo)
        whenever(kakaoApiClient.getUserInfo(kakaoAccessToken)).thenReturn(userInfo)
        whenever(memberPort.findBySocial(SocialProvider.KAKAO, SocialId.fromKakaoId(kakaoId))).thenReturn(null)
        whenever(memberPort.save(any())).thenAnswer { invocation -> invocation.arguments[0] as Member }
        whenever(issueTokenUseCase.execute(IssueTokenCommand(identifier = kakaoId.toString(), userType = "MEMBER")))
            .thenReturn(expectedTokenPair)

        val result = useCase.execute(command)

        assertThat(result.accessToken).isEqualTo("new_access_token")
        assertThat(result.refreshToken).isEqualTo("new_refresh_token")
        verify(memberPort).save(any())
    }

    @Test
    fun `should throw InvalidKakaoTokenException when token verification fails`() {
        val kakaoAccessToken = "invalid_token"
        val command = LoginWithKakaoCommand(kakaoAccessToken)

        whenever(kakaoApiClient.verifyAccessToken(kakaoAccessToken))
            .thenThrow(InvalidKakaoTokenException())

        assertThatThrownBy { useCase.execute(command) }
            .isInstanceOf(InvalidKakaoTokenException::class.java)

        verify(kakaoApiClient, never()).getUserInfo(any())
    }

    @Test
    fun `should throw KakaoApiException when getting user info fails`() {
        val kakaoAccessToken = "valid_token"
        val kakaoId = 123456789L
        val command = LoginWithKakaoCommand(kakaoAccessToken)

        val tokenInfo = KakaoTokenInfo(id = kakaoId, expiresIn = 3600, appId = 123456)

        whenever(kakaoApiClient.verifyAccessToken(kakaoAccessToken)).thenReturn(tokenInfo)
        whenever(kakaoApiClient.getUserInfo(kakaoAccessToken))
            .thenThrow(KakaoApiException("Failed to get user info"))

        assertThatThrownBy { useCase.execute(command) }
            .isInstanceOf(KakaoApiException::class.java)
            .hasMessageContaining("Failed to get user info")
    }

    @Test
    fun `should use socialId as identifier for JWT token`() {
        val kakaoAccessToken = "valid_kakao_access_token"
        val kakaoId = 1234567890123L
        val command = LoginWithKakaoCommand(kakaoAccessToken)

        val tokenInfo = KakaoTokenInfo(id = kakaoId, expiresIn = 3600, appId = 123456)
        val userInfo = KakaoUserInfo(id = kakaoId, nickname = "테스트유저")
        val expectedTokenPair = TokenPair(accessToken = "access", refreshToken = "refresh")

        whenever(kakaoApiClient.verifyAccessToken(kakaoAccessToken)).thenReturn(tokenInfo)
        whenever(kakaoApiClient.getUserInfo(kakaoAccessToken)).thenReturn(userInfo)
        whenever(memberPort.findBySocial(SocialProvider.KAKAO, SocialId.fromKakaoId(kakaoId))).thenReturn(null)
        whenever(memberPort.save(any())).thenAnswer { invocation -> invocation.arguments[0] as Member }
        whenever(issueTokenUseCase.execute(IssueTokenCommand(identifier = "1234567890123", userType = "MEMBER")))
            .thenReturn(expectedTokenPair)

        val result = useCase.execute(command)

        assertThat(result).isEqualTo(expectedTokenPair)
        verify(issueTokenUseCase).execute(IssueTokenCommand(identifier = "1234567890123", userType = "MEMBER"))
    }
}
