package com.mad.jellomarkserver.e2e.member.sign_up

import com.mad.jellomarkserver.apigateway.adapter.driving.web.request.SignUpMemberRequest
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

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = ["classpath:sql/truncate-members.sql"], executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class SignUpNicknameExceptionE2ETest {
    @LocalServerPort
    private var port: Int = 0

    @Autowired
    lateinit var rest: TestRestTemplate
    private fun url(path: String) = "http://localhost:$port$path"

    private final val headers = HttpHeaders().apply { contentType = MediaType.APPLICATION_JSON }

    @Test
    fun `409 when owner duplicate nickname`() {
        val first = SignUpMemberRequest(
            nickname = "mad",
            email = "email1@example.com",
        )
        val second = SignUpMemberRequest(
            nickname = "mad",
            email = "email2@example.com",
        )

        val r1 =
            rest.exchange(
                url("/api/sign-up/member"),
                HttpMethod.POST,
                HttpEntity(first, headers),
                object : ParameterizedTypeReference<Map<String, Any?>>() {})
        assertThat(r1.statusCode).isEqualTo(HttpStatus.CREATED)

        val r2 =
            rest.exchange(
                url("/api/sign-up/member"),
                HttpMethod.POST,
                HttpEntity(second, headers),
                object : ParameterizedTypeReference<Map<String, Any?>>() {})
        assertThat(r2.statusCode).isEqualTo(HttpStatus.CONFLICT)

        val err = requireNotNull(r2.body)
        assertThat(err["title"]).isEqualTo("Conflict")
    }

    @Test
    fun `422 when invalid nickname by null`() {
        val member = SignUpMemberRequest(
            nickname = "",
            email = "email1@example.com",
        )

        val response =
            rest.exchange(
                url("/api/sign-up/member"),
                HttpMethod.POST,
                HttpEntity(member, headers),
                object : ParameterizedTypeReference<Map<String, Any?>>() {}
            )

        val err = requireNotNull(response.body)
        assertThat(response.statusCode).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
        assertThat(err["title"]).isEqualTo("Unprocessable Entity")
    }
}