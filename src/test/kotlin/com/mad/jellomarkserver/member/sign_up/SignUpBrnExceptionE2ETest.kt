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
class SignUpBrnExceptionE2ETest {

    @LocalServerPort
    private var port: Int = 0

    @Autowired
    lateinit var rest: TestRestTemplate
    private fun url(path: String) = "http://localhost:$port$path"

    private final val headers = HttpHeaders().apply { contentType = MediaType.APPLICATION_JSON }

    @Test
    fun `422 when blank business registration number`() {
        val body = MemberSignUpRequest(
            nickname = "owner",
            email = "owner@example.com",
            memberType = MemberType.OWNER,
            businessRegistrationNumber = ""
        )
        val response = rest.exchange(
            url("/api/members/sign-up"),
            HttpMethod.POST,
            HttpEntity(body, headers),
            Map::class.java
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
        val err = response.body!!
        assertThat(err["code"]).isEqualTo("INVALID_ARGUMENT")
    }

    @Test
    fun `422 when owner without business registration number`() {
        val body = MemberSignUpRequest(
            nickname = "owner2",
            email = "owner2@example.com",
            memberType = MemberType.OWNER,
            businessRegistrationNumber = null
        )
        val response = rest.exchange(
            url("/api/members/sign-up"),
            HttpMethod.POST,
            HttpEntity(body, headers),
            Map::class.java
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
        val err = response.body!!
        assertThat(err["code"]).isEqualTo("BUSINESS_NUMBER_INVALID")
    }

    @Test
    fun `409 when owner duplicate business registration number`() {
        val first = MemberSignUpRequest(
            nickname = "owner2",
            email = "owner2@example.com",
            memberType = MemberType.OWNER,
            businessRegistrationNumber = "123-45-67890"
        )

        val second = MemberSignUpRequest(
            nickname = "owner3",
            email = "owner3@example.com",
            memberType = MemberType.OWNER,
            businessRegistrationNumber = "123-45-67890"
        )

        val r1 = rest.exchange(
            url("/api/members/sign-up"),
            HttpMethod.POST,
            HttpEntity(first, headers),
            Map::class.java
        )

        val r2 = rest.exchange(
            url("/api/members/sign-up"),
            HttpMethod.POST,
            HttpEntity(second, headers),
            Map::class.java
        )

        assertThat(r1.statusCode).isEqualTo(HttpStatus.CREATED)
        assertThat(r2.statusCode).isEqualTo(HttpStatus.CONFLICT)
    }
}