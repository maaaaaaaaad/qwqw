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

    private val headers = HttpHeaders().apply { contentType = MediaType.APPLICATION_JSON }

    @Test
    fun `success signup for general member`() {
        val body = SignUpMemberRequest(
            nickname = "maduser",
            email = "mad@example.com",
        )

        val response = rest.exchange(
            url("/api/sign-up/member"),
            HttpMethod.POST,
            HttpEntity(body, headers),
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.CREATED)
        val json = requireNotNull(response.body)
        assertThat(json["id"]).isNotNull()
        assertThat(json["nickname"]).isEqualTo("maduser")
        assertThat(json["email"]).isEqualTo("mad@example.com")
        assertThat(json.keys).containsAll(listOf("createdAt", "updatedAt"))
    }
}
