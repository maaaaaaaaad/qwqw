package com.mad.jellomarkserver.e2e.member.sign_up

import com.mad.jellomarkserver.member.adapter.driving.web.request.MemberSignUpRequest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
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
        val first = MemberSignUpRequest(
            nickname = "mad",
            email = "email1@example.com",
        )
        val second = MemberSignUpRequest(
            nickname = "mad",
            email = "email2@example.com",
        )

        val r1 =
            rest.exchange(url("/api/members/sign-up"), HttpMethod.POST, HttpEntity(first, headers), Map::class.java)
        assertThat(r1.statusCode).isEqualTo(HttpStatus.CREATED)

        val r2 =
            rest.exchange(url("/api/members/sign-up"), HttpMethod.POST, HttpEntity(second, headers), Map::class.java)
        assertThat(r2.statusCode).isEqualTo(HttpStatus.CONFLICT)

        val err = r2.body!!
        assertThat(err["code"]).isEqualTo("MEMBER_DUPLICATE_NICKNAME")
    }

    @Test
    fun `422 when invalid nickname by null`() {
        val member = MemberSignUpRequest(
            nickname = "",
            email = "email1@example.com",
        )

        val response =
            rest.exchange(
                url("/api/members/sign-up"),
                HttpMethod.POST,
                HttpEntity(member, headers),
                Map::class.java
            )

        val err = response.body!!
        assertThat(err["code"]).isEqualTo("MEMBER_NICKNAME_INVALID")
        assertThat(response.statusCode).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
    }
}