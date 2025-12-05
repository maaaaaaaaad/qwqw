package com.mad.jellomarkserver.e2e.member.sign_up

import com.mad.jellomarkserver.apigateway.adapter.driving.web.request.SignUpMemberRequest
import com.mad.jellomarkserver.auth.adapter.driven.persistence.repository.AuthJpaRepository
import com.mad.jellomarkserver.member.adapter.driven.persistence.repository.MemberJpaRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mindrot.jbcrypt.BCrypt
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
        "classpath:sql/truncate-auths.sql"
    ],
    executionPhase = ExecutionPhase.BEFORE_TEST_METHOD
)
class SignUpMemberWithAuthE2ETest {

    @LocalServerPort
    private var port: Int = 0

    @Autowired
    lateinit var rest: TestRestTemplate

    @Autowired
    lateinit var memberJpaRepository: MemberJpaRepository

    @Autowired
    lateinit var authJpaRepository: AuthJpaRepository

    private fun url(path: String) = "http://localhost:$port$path"

    private val headers = HttpHeaders().apply { contentType = MediaType.APPLICATION_JSON }

    @Test
    fun `should create both Member and Auth when signing up`() {
        val request = SignUpMemberRequest(
            nickname = "testuser",
            email = "test@example.com",
            password = "Password123!",
        )

        val response = rest.exchange(
            url("/api/sign-up/member"),
            HttpMethod.POST,
            HttpEntity(request, headers),
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.CREATED)
        val json = requireNotNull(response.body)
        assertThat(json["id"]).isNotNull()
        assertThat(json["nickname"]).isEqualTo("testuser")
        assertThat(json["email"]).isEqualTo("test@example.com")

        val members = memberJpaRepository.findAll()
        assertThat(members).hasSize(1)
        val memberEntity = members[0]
        assertThat(memberEntity.email).isEqualTo("test@example.com")
        assertThat(memberEntity.nickname).isEqualTo("testuser")

        val auths = authJpaRepository.findAll()
        assertThat(auths).hasSize(1)
        val authEntity = auths[0]
        assertThat(authEntity.email).isEqualTo("test@example.com")
        assertThat(authEntity.userType).isEqualTo("MEMBER")

        assertThat(authEntity.hashedPassword).isNotEqualTo("Password123!")
        assertThat(BCrypt.checkpw("Password123!", authEntity.hashedPassword)).isTrue()
    }

    @Test
    fun `should create Auth with different passwords for different members`() {
        val request1 = SignUpMemberRequest(
            nickname = "user1",
            email = "user1@example.com",
            password = "Password111!",
        )

        val request2 = SignUpMemberRequest(
            nickname = "user2",
            email = "user2@example.com",
            password = "Password222!",
        )

        rest.exchange(
            url("/api/sign-up/member"),
            HttpMethod.POST,
            HttpEntity(request1, headers),
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )

        rest.exchange(
            url("/api/sign-up/member"),
            HttpMethod.POST,
            HttpEntity(request2, headers),
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )

        val auths = authJpaRepository.findAll()
        assertThat(auths).hasSize(2)

        val auth1 = auths.find { it.email == "user1@example.com" }
        val auth2 = auths.find { it.email == "user2@example.com" }

        assertThat(auth1).isNotNull
        assertThat(auth2).isNotNull
        assertThat(auth1!!.hashedPassword).isNotEqualTo(auth2!!.hashedPassword)
        assertThat(BCrypt.checkpw("Password111!", auth1.hashedPassword)).isTrue()
        assertThat(BCrypt.checkpw("Password222!", auth2.hashedPassword)).isTrue()
    }

    @Test
    fun `should create Auth with MEMBER user type`() {
        val request = SignUpMemberRequest(
            nickname = "memuser",
            email = "member@example.com",
            password = "Password123!",
        )

        val response = rest.exchange(
            url("/api/sign-up/member"),
            HttpMethod.POST,
            HttpEntity(request, headers),
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.CREATED)

        val auths = authJpaRepository.findAll()
        assertThat(auths).hasSize(1)
        assertThat(auths[0].userType).isEqualTo("MEMBER")
    }
}
