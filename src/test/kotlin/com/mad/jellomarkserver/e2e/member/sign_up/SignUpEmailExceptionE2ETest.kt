package com.mad.jellomarkserver.e2e.member.sign_up

import com.mad.jellomarkserver.member.adapter.driving.web.request.MemberSignUpRequest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.jdbc.Sql.ExecutionPhase

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = ["classpath:sql/truncate-members.sql"], executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
class SignUpEmailExceptionE2ETest {

    @LocalServerPort
    private var port: Int = 0

    @Autowired
    lateinit var rest: TestRestTemplate
    private fun url(path: String) = "http://localhost:$port$path"

    private final val headers = HttpHeaders().apply { contentType = MediaType.APPLICATION_JSON }

    @Test
    fun `409 email`() {
        val first = MemberSignUpRequest(
            nickname = "first",
            email = "dup@example.com",
        )
        val second = MemberSignUpRequest(
            nickname = "second",
            email = "dup@example.com",
        )

        val r1 =
            rest.exchange(
                url("/api/members/sign-up"),
                HttpMethod.POST,
                HttpEntity(first, headers),
                object : ParameterizedTypeReference<Map<String, Any?>>() {})
        assertThat(r1.statusCode).isEqualTo(HttpStatus.CREATED)

        val r2 =
            rest.exchange(
                url("/api/members/sign-up"),
                HttpMethod.POST,
                HttpEntity(second, headers),
                object : ParameterizedTypeReference<Map<String, Any?>>() {})
        assertThat(r2.statusCode).isEqualTo(HttpStatus.CONFLICT)
        val err = requireNotNull(r2.body)
        assertThat(err["title"]).isEqualTo("Conflict")
        assertThat(err["status"]).isEqualTo(HttpStatus.CONFLICT.value())
    }

    @Test
    fun `422 when email format is invalid`() {
        val body = MemberSignUpRequest(
            nickname = "user1",
            email = "not-an-email",
        )
        val response = rest.exchange(
            url("/api/members/sign-up"),
            HttpMethod.POST,
            HttpEntity(body, headers),
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
        val err = requireNotNull(response.body)
        assertThat(err["title"]).isEqualTo("Unprocessable Entity")
    }

    @Test
    fun `422 when email is null`() {
        val body = MemberSignUpRequest(
            nickname = "user2",
            email = ""
        )
        val response = rest.exchange(
            url("/api/members/sign-up"),
            HttpMethod.POST,
            HttpEntity(body, headers),
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )
        assertThat(response.statusCode).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
        val err = requireNotNull(response.body)
        assertThat(err["title"]).isEqualTo("Unprocessable Entity")
    }
}
