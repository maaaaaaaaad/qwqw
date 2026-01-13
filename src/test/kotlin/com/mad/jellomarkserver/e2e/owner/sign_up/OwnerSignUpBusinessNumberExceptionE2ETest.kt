package com.mad.jellomarkserver.e2e.owner.sign_up

import com.mad.jellomarkserver.apigateway.adapter.driving.web.request.SignUpOwnerRequest
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
        "classpath:sql/truncate-auths.sql",
        "classpath:sql/truncate-refresh-tokens.sql"
    ],
    executionPhase = ExecutionPhase.BEFORE_TEST_METHOD
)
class OwnerSignUpBusinessNumberExceptionE2ETest {

    @LocalServerPort
    private var port: Int = 0

    @Autowired
    lateinit var rest: TestRestTemplate
    private fun url(path: String) = "http://localhost:$port$path"

    private val headers = HttpHeaders().apply { contentType = MediaType.APPLICATION_JSON }

    @Test
    fun `409 duplicate business number`() {
        val first = SignUpOwnerRequest(
            businessNumber = "123456789",
            phoneNumber = "010-1111-1111",
            nickname = "first",
            email = "first@example.com",
            password = "Password123!",
        )
        val second = SignUpOwnerRequest(
            businessNumber = "123456789",
            phoneNumber = "010-2222-2222",
            nickname = "second",
            email = "second@example.com",
            password = "Password456!",
        )

        val r1 = rest.exchange(
            url("/api/sign-up/owner"),
            HttpMethod.POST,
            HttpEntity(first, headers),
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )
        assertThat(r1.statusCode).isEqualTo(HttpStatus.CREATED)

        val r2 = rest.exchange(
            url("/api/sign-up/owner"),
            HttpMethod.POST,
            HttpEntity(second, headers),
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )
        assertThat(r2.statusCode).isEqualTo(HttpStatus.CONFLICT)
        val err = requireNotNull(r2.body)
        assertThat(err["title"]).isEqualTo("Conflict")
        assertThat(err["status"]).isEqualTo(HttpStatus.CONFLICT.value())
    }

    @Test
    fun `422 when business number format is invalid`() {
        val body = SignUpOwnerRequest(
            businessNumber = "12345",
            phoneNumber = "010-1234-5678",
            nickname = "test",
            email = "test@example.com",
            password = "Password123!",
        )
        val response = rest.exchange(
            url("/api/sign-up/owner"),
            HttpMethod.POST,
            HttpEntity(body, headers),
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
        val err = requireNotNull(response.body)
        assertThat(err["title"]).isEqualTo("Unprocessable Entity")
    }

    @Test
    fun `422 when business number is null`() {
        val body = SignUpOwnerRequest(
            businessNumber = "",
            phoneNumber = "010-1234-5678",
            nickname = "test",
            email = "test@example.com",
            password = "Password123!",
        )
        val response = rest.exchange(
            url("/api/sign-up/owner"),
            HttpMethod.POST,
            HttpEntity(body, headers),
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
        val err = requireNotNull(response.body)
        assertThat(err["title"]).isEqualTo("Unprocessable Entity")
    }
}
