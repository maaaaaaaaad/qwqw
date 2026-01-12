package com.mad.jellomarkserver.e2e.auth

import com.mad.jellomarkserver.apigateway.adapter.driving.web.request.LoginWithKakaoRequest
import com.mad.jellomarkserver.auth.adapter.driven.kakao.KakaoTokenInfo
import com.mad.jellomarkserver.auth.adapter.driven.kakao.KakaoUserInfo
import com.mad.jellomarkserver.auth.adapter.driven.persistence.repository.RefreshTokenJpaRepository
import com.mad.jellomarkserver.auth.core.domain.exception.InvalidKakaoTokenException
import com.mad.jellomarkserver.auth.port.driven.KakaoApiClient
import com.mad.jellomarkserver.member.adapter.driven.persistence.repository.MemberJpaRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.*
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.jdbc.Sql.ExecutionPhase

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(
    scripts = [
        "classpath:sql/truncate-members.sql",
        "classpath:sql/truncate-refresh-tokens.sql"
    ],
    executionPhase = ExecutionPhase.BEFORE_TEST_METHOD
)
class LoginWithKakaoE2ETest {

    @LocalServerPort
    private var port: Int = 0

    @Autowired
    lateinit var rest: TestRestTemplate

    @Autowired
    lateinit var memberJpaRepository: MemberJpaRepository

    @Autowired
    lateinit var refreshTokenJpaRepository: RefreshTokenJpaRepository

    @MockitoBean
    lateinit var kakaoApiClient: KakaoApiClient

    private fun url(path: String) = "http://localhost:$port$path"

    private val headers = HttpHeaders().apply { contentType = MediaType.APPLICATION_JSON }

    @Test
    fun `should login new member with Kakao and return tokens`() {
        val kakaoAccessToken = "valid_kakao_access_token"
        val kakaoId = 1234567890123L
        val nickname = "카카오유저"

        val tokenInfo = KakaoTokenInfo(id = kakaoId, expiresIn = 3600, appId = 123456)
        val userInfo = KakaoUserInfo(id = kakaoId, nickname = nickname)

        whenever(kakaoApiClient.verifyAccessToken(kakaoAccessToken)).thenReturn(tokenInfo)
        whenever(kakaoApiClient.getUserInfo(kakaoAccessToken)).thenReturn(userInfo)

        val request = LoginWithKakaoRequest(kakaoAccessToken = kakaoAccessToken)

        val response = rest.exchange(
            url("/api/auth/kakao"),
            HttpMethod.POST,
            HttpEntity(request, headers),
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val json = requireNotNull(response.body)
        assertThat(json["accessToken"]).isNotNull()
        assertThat(json["refreshToken"]).isNotNull()

        val members = memberJpaRepository.findAll()
        assertThat(members).hasSize(1)
        val memberEntity = members[0]
        assertThat(memberEntity.socialProvider).isEqualTo("KAKAO")
        assertThat(memberEntity.socialId).isEqualTo(kakaoId.toString())
        assertThat(memberEntity.nickname).isEqualTo(nickname)

        val refreshTokens = refreshTokenJpaRepository.findAll()
        assertThat(refreshTokens).hasSize(1)
        val refreshTokenEntity = refreshTokens[0]
        assertThat(refreshTokenEntity.identifier).isEqualTo(kakaoId.toString())
        assertThat(refreshTokenEntity.userType).isEqualTo("MEMBER")
        assertThat(refreshTokenEntity.token).isEqualTo(json["refreshToken"])
    }

    @Test
    fun `should login existing member with Kakao and return tokens`() {
        val kakaoAccessToken = "valid_kakao_access_token"
        val kakaoId = 9876543210123L
        val nickname = "기존카카오유저"

        val tokenInfo = KakaoTokenInfo(id = kakaoId, expiresIn = 3600, appId = 123456)
        val userInfo = KakaoUserInfo(id = kakaoId, nickname = nickname)

        whenever(kakaoApiClient.verifyAccessToken(kakaoAccessToken)).thenReturn(tokenInfo)
        whenever(kakaoApiClient.getUserInfo(kakaoAccessToken)).thenReturn(userInfo)

        val request = LoginWithKakaoRequest(kakaoAccessToken = kakaoAccessToken)

        val firstResponse = rest.exchange(
            url("/api/auth/kakao"),
            HttpMethod.POST,
            HttpEntity(request, headers),
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )

        assertThat(firstResponse.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(memberJpaRepository.findAll()).hasSize(1)

        val secondResponse = rest.exchange(
            url("/api/auth/kakao"),
            HttpMethod.POST,
            HttpEntity(request, headers),
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )

        assertThat(secondResponse.statusCode).isEqualTo(HttpStatus.OK)
        val json = requireNotNull(secondResponse.body)
        assertThat(json["accessToken"]).isNotNull()
        assertThat(json["refreshToken"]).isNotNull()

        assertThat(memberJpaRepository.findAll()).hasSize(1)

        val refreshTokens = refreshTokenJpaRepository.findAll()
        assertThat(refreshTokens).hasSize(1)
        assertThat(refreshTokens[0].token).isEqualTo(json["refreshToken"])
    }

    @Test
    fun `should return 401 when Kakao token is invalid`() {
        val invalidToken = "invalid_kakao_token"

        whenever(kakaoApiClient.verifyAccessToken(invalidToken))
            .thenThrow(InvalidKakaoTokenException())

        val request = LoginWithKakaoRequest(kakaoAccessToken = invalidToken)

        val response = rest.exchange(
            url("/api/auth/kakao"),
            HttpMethod.POST,
            HttpEntity(request, headers),
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)

        assertThat(memberJpaRepository.findAll()).isEmpty()
        assertThat(refreshTokenJpaRepository.findAll()).isEmpty()
    }
}
