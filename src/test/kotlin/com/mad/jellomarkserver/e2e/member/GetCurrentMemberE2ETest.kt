package com.mad.jellomarkserver.e2e.member

import com.mad.jellomarkserver.apigateway.adapter.driving.web.request.LoginWithKakaoRequest
import com.mad.jellomarkserver.auth.adapter.driven.kakao.KakaoTokenInfo
import com.mad.jellomarkserver.auth.adapter.driven.kakao.KakaoUserInfo
import com.mad.jellomarkserver.auth.port.driven.KakaoApiClient
import com.mad.jellomarkserver.member.adapter.driven.persistence.repository.MemberJpaRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
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
class GetCurrentMemberE2ETest {

    @LocalServerPort
    private var port: Int = 0

    @Autowired
    lateinit var rest: TestRestTemplate

    @Autowired
    lateinit var memberJpaRepository: MemberJpaRepository

    @MockBean
    lateinit var kakaoApiClient: KakaoApiClient

    private fun url(path: String) = "http://localhost:$port$path"

    private val headers = HttpHeaders().apply { contentType = MediaType.APPLICATION_JSON }

    private fun loginWithKakaoAndGetAccessToken(kakaoId: Long, nickname: String): String {
        val kakaoAccessToken = "valid_kakao_access_token_$kakaoId"

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

        val json = requireNotNull(response.body)
        return json["accessToken"] as String
    }

    @Test
    fun `should return current member info after Kakao login`() {
        val kakaoId = 1234567890123L
        val nickname = "테스트유저"
        val accessToken = loginWithKakaoAndGetAccessToken(kakaoId, nickname)

        val requestHeaders = HttpHeaders().apply {
            set("Authorization", "Bearer $accessToken")
        }

        val response = rest.exchange(
            url("/api/members/me"),
            HttpMethod.GET,
            HttpEntity(null, requestHeaders),
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val json = requireNotNull(response.body)
        assertThat(json["id"]).isNotNull()
        assertThat(json["socialProvider"]).isEqualTo("KAKAO")
        assertThat(json["socialId"]).isEqualTo(kakaoId.toString())
        assertThat(json["nickname"]).isEqualTo(nickname)
        assertThat(json["createdAt"]).isNotNull()
        assertThat(json["updatedAt"]).isNotNull()
    }

    @Test
    fun `should return 401 when getting current member without authorization`() {
        val response = rest.exchange(
            url("/api/members/me"),
            HttpMethod.GET,
            HttpEntity(null, headers),
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)
    }

    @Test
    fun `should return 401 when getting current member with invalid token`() {
        val invalidHeaders = HttpHeaders().apply {
            set("Authorization", "Bearer invalid-token")
        }

        val response = rest.exchange(
            url("/api/members/me"),
            HttpMethod.GET,
            HttpEntity(null, invalidHeaders),
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)
    }
}
