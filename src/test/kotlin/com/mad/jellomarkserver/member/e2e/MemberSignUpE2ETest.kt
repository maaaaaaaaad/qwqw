package com.mad.jellomarkserver.member.e2e

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
import org.springframework.test.context.jdbc.Sql.ExecutionPhase

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = ["classpath:sql/truncate-members.sql"], executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
class MemberSignUpE2ETest {

    @LocalServerPort
    private var port: Int = 0

    @Autowired
    lateinit var rest: TestRestTemplate
    private fun url(path: String) = "http://localhost:$port$path"

    private final val headers = HttpHeaders().apply { contentType = MediaType.APPLICATION_JSON }

    @Test
    fun `success signup for general member`() {
        val body = MemberSignUpRequest(
            nickname = "maduser",
            email = "mad@example.com",
            memberType = MemberType.CONSUMER,
            businessRegistrationNumber = null
        )

        val response = rest.exchange(
            url("/api/members/sign-up"),
            HttpMethod.POST,
            HttpEntity(body, headers),
            Map::class.java
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.CREATED)
        val json = response.body!!
        assertThat(json["id"]).isNotNull()
        assertThat(json["nickname"]).isEqualTo("maduser")
        assertThat(json["email"]).isEqualTo("mad@example.com")
        assertThat(json.keys).containsAll(listOf("createdAt", "updatedAt"))
    }

    @Test
    fun `409 email`() {
        val first = MemberSignUpRequest(
            nickname = "first",
            email = "dup@example.com",
            memberType = MemberType.CONSUMER,
            businessRegistrationNumber = null
        )
        val second = MemberSignUpRequest(
            nickname = "second",
            email = "dup@example.com",
            memberType = MemberType.CONSUMER,
            businessRegistrationNumber = null
        )

        val r1 =
            rest.exchange(url("/api/members/sign-up"), HttpMethod.POST, HttpEntity(first, headers), Map::class.java)
        assertThat(r1.statusCode).isEqualTo(HttpStatus.CREATED)

        val r2 =
            rest.exchange(url("/api/members/sign-up"), HttpMethod.POST, HttpEntity(second, headers), Map::class.java)
        assertThat(r2.statusCode).isEqualTo(HttpStatus.CONFLICT)
        val err = r2.body
        if (err != null) {
            assertThat(err["code"]).isIn("MEMBER_DUPLICATE_EMAIL", "error")
        }
    }

    @Test
    fun `422 when email format is invalid`() {
        val body = MemberSignUpRequest(
            nickname = "user1",
            email = "not-an-email",
            memberType = MemberType.CONSUMER,
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
        assertThat(err["code"]).isEqualTo("INVALID_ARGUMENT")
    }

    @Test
    fun `422 when email is blank`() {
        val body = MemberSignUpRequest(
            nickname = "user2",
            email = "   ",
            memberType = MemberType.CONSUMER,
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
        assertThat(err["code"]).isEqualTo("INVALID_ARGUMENT")
    }

    @Test
    fun `success signup for owner with business registration number`() {
        val body = MemberSignUpRequest(
            nickname = "owner1",
            email = "owner1@example.com",
            memberType = MemberType.OWNER,
            businessRegistrationNumber = "123-45-67890"
        )

        val response = rest.exchange(
            url("/api/members/sign-up"),
            HttpMethod.POST,
            HttpEntity(body, headers),
            Map::class.java
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.CREATED)
        val json = response.body!!
        assertThat(json["memberType"]).isEqualTo("OWNER")
        assertThat(json["businessRegistrationNumber"]).isEqualTo("1234567890")
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
