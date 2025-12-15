package com.mad.jellomarkserver.e2e.auth.authenticate

import com.mad.jellomarkserver.apigateway.adapter.driving.web.request.SignUpMemberRequest
import com.mad.jellomarkserver.auth.adapter.driven.persistence.repository.AuthJpaRepository
import com.mad.jellomarkserver.auth.adapter.driven.persistence.repository.RefreshTokenJpaRepository
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
class AuthenticateE2ETest {

    @LocalServerPort
    private var port: Int = 0

    @Autowired
    lateinit var rest: TestRestTemplate

    @Autowired
    lateinit var authJpaRepository: AuthJpaRepository

    @Autowired
    lateinit var refreshTokenJpaRepository: RefreshTokenJpaRepository

    private fun url(path: String) = "http://localhost:$port$path"

    private val headers = HttpHeaders().apply { contentType = MediaType.APPLICATION_JSON }

    @Test
    fun `should authenticate successfully with correct email and password`() {
        val signUpRequest = SignUpMemberRequest(
            nickname = "testuser",
            email = "test@example.com",
            password = "Password123!",
        )

        rest.exchange(
            url("/api/sign-up/member"),
            HttpMethod.POST,
            HttpEntity(signUpRequest, headers),
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )

        val authenticateRequest = mapOf(
            "email" to "test@example.com",
            "password" to "Password123!"
        )

        val response = rest.exchange(
            url("/api/auth/authenticate"),
            HttpMethod.POST,
            HttpEntity(authenticateRequest, headers),
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val json = requireNotNull(response.body)
        assertThat(json["authenticated"]).isEqualTo(true)
        assertThat(json["email"]).isEqualTo("test@example.com")
        assertThat(json["userType"]).isEqualTo("MEMBER")
        assertThat(json["accessToken"]).isNotNull()
        assertThat(json["refreshToken"]).isNotNull()

        val savedRefreshToken = refreshTokenJpaRepository.findByEmail("test@example.com")
        assertThat(savedRefreshToken).isNotNull()
        assertThat(savedRefreshToken?.token).isEqualTo(json["refreshToken"])
    }

    @Test
    fun `should fail authentication with invalid email`() {
        val signUpRequest = SignUpMemberRequest(
            nickname = "testuser",
            email = "test@example.com",
            password = "Password123!",
        )

        rest.exchange(
            url("/api/sign-up/member"),
            HttpMethod.POST,
            HttpEntity(signUpRequest, headers),
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )

        val authenticateRequest = mapOf(
            "email" to "wrong@example.com",
            "password" to "Password123!"
        )

        val response = rest.exchange(
            url("/api/auth/authenticate"),
            HttpMethod.POST,
            HttpEntity(authenticateRequest, headers),
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)
    }

    @Test
    fun `should fail authentication with invalid password`() {
        val signUpRequest = SignUpMemberRequest(
            nickname = "testuser",
            email = "test@example.com",
            password = "Password123!",
        )

        rest.exchange(
            url("/api/sign-up/member"),
            HttpMethod.POST,
            HttpEntity(signUpRequest, headers),
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )

        val authenticateRequest = mapOf(
            "email" to "test@example.com",
            "password" to "WrongPassword123!"
        )

        val response = rest.exchange(
            url("/api/auth/authenticate"),
            HttpMethod.POST,
            HttpEntity(authenticateRequest, headers),
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)
    }
}
