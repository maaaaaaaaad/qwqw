package com.mad.jellomarkserver.member.sign_up

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
import org.springframework.test.context.jdbc.Sql.ExecutionPhase

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = ["classpath:sql/truncate-members.sql"], executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
class SignUpSuccessE2ETest {

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
}
