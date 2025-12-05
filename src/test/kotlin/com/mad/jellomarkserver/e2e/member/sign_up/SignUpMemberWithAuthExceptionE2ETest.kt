package com.mad.jellomarkserver.e2e.member.sign_up

import com.mad.jellomarkserver.apigateway.adapter.driving.web.request.SignUpMemberRequest
import com.mad.jellomarkserver.auth.adapter.driven.persistence.repository.AuthJpaRepository
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
        "classpath:sql/truncate-auths.sql"
    ],
    executionPhase = ExecutionPhase.BEFORE_TEST_METHOD
)
class SignUpMemberWithAuthExceptionE2ETest {

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
    fun `should return 409 CONFLICT when email is duplicated and not create second Member or Auth`() {
        val first = SignUpMemberRequest(
            nickname = "first",
            email = "dup@example.com",
            password = "Password123!",
        )

        val second = SignUpMemberRequest(
            nickname = "second",
            email = "dup@example.com",
            password = "Password456!",
        )

        val r1 = rest.exchange(
            url("/api/sign-up/member"),
            HttpMethod.POST,
            HttpEntity(first, headers),
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )
        assertThat(r1.statusCode).isEqualTo(HttpStatus.CREATED)

        val membersAfterFirst = memberJpaRepository.findAll()
        val authsAfterFirst = authJpaRepository.findAll()
        assertThat(membersAfterFirst).hasSize(1)
        assertThat(authsAfterFirst).hasSize(1)
        assertThat(membersAfterFirst[0].email).isEqualTo("dup@example.com")
        assertThat(authsAfterFirst[0].email).isEqualTo("dup@example.com")

        val r2 = rest.exchange(
            url("/api/sign-up/member"),
            HttpMethod.POST,
            HttpEntity(second, headers),
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )
        assertThat(r2.statusCode).isEqualTo(HttpStatus.CONFLICT)
        val err = requireNotNull(r2.body)
        assertThat(err["title"]).isEqualTo("Conflict")

        val membersAfterSecond = memberJpaRepository.findAll()
        val authsAfterSecond = authJpaRepository.findAll()
        assertThat(membersAfterSecond).hasSize(1)
        assertThat(authsAfterSecond).hasSize(1)
        assertThat(membersAfterSecond[0].nickname).isEqualTo("first")
        assertThat(authsAfterSecond[0].email).isEqualTo("dup@example.com")
    }

    @Test
    fun `should rollback Auth creation when Member creation fails due to duplicate nickname`() {
        val first = SignUpMemberRequest(
            nickname = "samename",
            email = "email1@example.com",
            password = "Password123!",
        )

        val second = SignUpMemberRequest(
            nickname = "samename",
            email = "email2@example.com",
            password = "Password456!",
        )

        val r1 = rest.exchange(
            url("/api/sign-up/member"),
            HttpMethod.POST,
            HttpEntity(first, headers),
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )
        assertThat(r1.statusCode).isEqualTo(HttpStatus.CREATED)

        val membersAfterFirst = memberJpaRepository.findAll()
        val authsAfterFirst = authJpaRepository.findAll()
        assertThat(membersAfterFirst).hasSize(1)
        assertThat(authsAfterFirst).hasSize(1)

        val r2 = rest.exchange(
            url("/api/sign-up/member"),
            HttpMethod.POST,
            HttpEntity(second, headers),
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )
        assertThat(r2.statusCode).isEqualTo(HttpStatus.CONFLICT)

        val membersAfterSecond = memberJpaRepository.findAll()
        val authsAfterSecond = authJpaRepository.findAll()
        assertThat(membersAfterSecond).hasSize(1)
        assertThat(authsAfterSecond).hasSize(1)
        assertThat(authsAfterSecond[0].email).isEqualTo("email1@example.com")
    }
}
