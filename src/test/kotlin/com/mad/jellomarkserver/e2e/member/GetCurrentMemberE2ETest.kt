package com.mad.jellomarkserver.e2e.member

import com.mad.jellomarkserver.apigateway.adapter.driving.web.request.SignUpMemberRequest
import com.mad.jellomarkserver.member.adapter.driven.persistence.repository.MemberJpaRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
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
        "classpath:sql/truncate-auths.sql",
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

    private fun url(path: String) = "http://localhost:$port$path"

    private fun signUpAndGetAccessToken(): String {
        val request = SignUpMemberRequest(
            nickname = "testuser",
            email = "test@example.com",
            password = "Password123!",
        )

        val headers = HttpHeaders().apply { contentType = MediaType.APPLICATION_JSON }

        val response = rest.exchange(
            url("/api/sign-up/member"),
            HttpMethod.POST,
            HttpEntity(request, headers),
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )

        val json = requireNotNull(response.body)
        return json["accessToken"] as String
    }

    @Test
    fun `should return current member when valid access token is provided`() {
        val accessToken = signUpAndGetAccessToken()

        val headers = HttpHeaders().apply {
            set("Authorization", "Bearer $accessToken")
        }

        val response = rest.exchange(
            url("/api/members/me"),
            HttpMethod.GET,
            HttpEntity<Void>(headers),
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val json = requireNotNull(response.body)
        assertThat(json["id"]).isNotNull()
        assertThat(json["nickname"]).isEqualTo("testuser")
        assertThat(json["email"]).isEqualTo("test@example.com")
    }

    @Test
    fun `should return 401 when authorization header is missing`() {
        signUpAndGetAccessToken()

        val response = rest.exchange(
            url("/api/members/me"),
            HttpMethod.GET,
            null,
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)
    }

    @Test
    fun `should return 401 when invalid access token is provided`() {
        signUpAndGetAccessToken()

        val headers = HttpHeaders().apply {
            set("Authorization", "Bearer invalid-token")
        }

        val response = rest.exchange(
            url("/api/members/me"),
            HttpMethod.GET,
            HttpEntity<Void>(headers),
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)
    }

    @Test
    fun `should return 401 when bearer token format is incorrect`() {
        val accessToken = signUpAndGetAccessToken()

        val headers = HttpHeaders().apply {
            set("Authorization", accessToken)
        }

        val response = rest.exchange(
            url("/api/members/me"),
            HttpMethod.GET,
            HttpEntity<Void>(headers),
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)
    }
}
