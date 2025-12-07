package com.mad.jellomarkserver.e2e.owner.sign_up

import com.mad.jellomarkserver.owner.adapter.driving.web.request.OwnerSignUpRequest
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
        "classpath:sql/truncate-owners.sql",
        "classpath:sql/truncate-auths.sql"
    ],
    executionPhase = ExecutionPhase.BEFORE_TEST_METHOD
)
class OwnerSignUpSuccessE2ETest {

    @LocalServerPort
    private var port: Int = 0

    @Autowired
    lateinit var rest: TestRestTemplate
    private fun url(path: String) = "http://localhost:$port$path"

    private val headers = HttpHeaders().apply { contentType = MediaType.APPLICATION_JSON }

    @Test
    fun `success signup for owner`() {
        val body = OwnerSignUpRequest(
            businessNumber = "123456789",
            phoneNumber = "010-1234-5678",
            nickname = "shop",
            email = "owner@example.com",
            password = "Password123!",
        )

        val response = rest.exchange(
            url("/api/owners/sign-up"),
            HttpMethod.POST,
            HttpEntity(body, headers),
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.CREATED)
        val json = requireNotNull(response.body)
        assertThat(json["id"]).isNotNull()
        assertThat(json["businessNumber"]).isEqualTo("123456789")
        assertThat(json["phoneNumber"]).isEqualTo("010-1234-5678")
        assertThat(json["nickname"]).isEqualTo("shop")
        assertThat(json.keys).containsAll(listOf("createdAt", "updatedAt"))
    }
}
