package com.mad.jellomarkserver.member.sign_up

import com.mad.jellomarkserver.member.adapter.`in`.web.request.MemberSignUpRequest
import com.mad.jellomarkserver.member.core.domain.model.MemberType
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
    fun `409 nickname`() {
        val first = MemberSignUpRequest(
            nickname = "mad",
            email = "email1@example.com",
            memberType = MemberType.CONSUMER,
            businessRegistrationNumber = null
        )
        val second = MemberSignUpRequest(
            nickname = "mad",
            email = "email2@example.com",
            memberType = MemberType.CONSUMER,
            businessRegistrationNumber = null
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
}