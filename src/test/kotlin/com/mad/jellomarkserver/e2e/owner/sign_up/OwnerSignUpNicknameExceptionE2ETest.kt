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
@Sql(scripts = ["classpath:sql/truncate-owners.sql"], executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
class OwnerSignUpNicknameExceptionE2ETest {

    @LocalServerPort
    private var port: Int = 0

    @Autowired
    lateinit var rest: TestRestTemplate
    private fun url(path: String) = "http://localhost:$port$path"

    private val headers = HttpHeaders().apply { contentType = MediaType.APPLICATION_JSON }

    @Test
    fun `409 duplicate nickname`() {
        val first = OwnerSignUpRequest(
            businessNumber = "111111111",
            phoneNumber = "010-1111-1111",
            nickname = "myshop"
        )
        val second = OwnerSignUpRequest(
            businessNumber = "222222222",
            phoneNumber = "010-2222-2222",
            nickname = "myshop"
        )

        val r1 = rest.exchange(
            url("/api/owners/sign-up"),
            HttpMethod.POST,
            HttpEntity(first, headers),
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )
        assertThat(r1.statusCode).isEqualTo(HttpStatus.CREATED)

        val r2 = rest.exchange(
            url("/api/owners/sign-up"),
            HttpMethod.POST,
            HttpEntity(second, headers),
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )
        assertThat(r2.statusCode).isEqualTo(HttpStatus.CONFLICT)
        val err = requireNotNull(r2.body)
        assertThat(err["title"]).isEqualTo("Conflict")
        assertThat(err["status"]).isEqualTo(HttpStatus.CONFLICT.value())
    }
}
