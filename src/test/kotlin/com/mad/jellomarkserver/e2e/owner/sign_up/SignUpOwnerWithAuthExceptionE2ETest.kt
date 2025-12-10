package com.mad.jellomarkserver.e2e.owner.sign_up

import com.mad.jellomarkserver.apigateway.adapter.driving.web.request.SignUpOwnerRequest
import com.mad.jellomarkserver.auth.adapter.driven.persistence.repository.AuthJpaRepository
import com.mad.jellomarkserver.owner.adapter.driven.persistence.repository.OwnerJpaRepository
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
class SignUpOwnerWithAuthExceptionE2ETest {

    @LocalServerPort
    private var port: Int = 0

    @Autowired
    lateinit var rest: TestRestTemplate

    @Autowired
    lateinit var ownerJpaRepository: OwnerJpaRepository

    @Autowired
    lateinit var authJpaRepository: AuthJpaRepository

    private fun url(path: String) = "http://localhost:$port$path"

    private val headers = HttpHeaders().apply { contentType = MediaType.APPLICATION_JSON }

    @Test
    fun `should return 409 CONFLICT when email is duplicated and not create second Owner or Auth`() {
        val first = SignUpOwnerRequest(
            businessNumber = "111111111",
            phoneNumber = "010-1111-1111",
            nickname = "first",
            email = "dup@example.com",
            password = "Password123!",
        )

        val second = SignUpOwnerRequest(
            businessNumber = "222222222",
            phoneNumber = "010-2222-2222",
            nickname = "second",
            email = "dup@example.com",
            password = "Password456!",
        )

        val r1 = rest.exchange(
            url("/api/sign-up/owner"),
            HttpMethod.POST,
            HttpEntity(first, headers),
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )
        assertThat(r1.statusCode).isEqualTo(HttpStatus.CREATED)

        val ownersAfterFirst = ownerJpaRepository.findAll()
        val authsAfterFirst = authJpaRepository.findAll()
        assertThat(ownersAfterFirst).hasSize(1)
        assertThat(authsAfterFirst).hasSize(1)
        assertThat(authsAfterFirst[0].email).isEqualTo("dup@example.com")

        val r2 = rest.exchange(
            url("/api/sign-up/owner"),
            HttpMethod.POST,
            HttpEntity(second, headers),
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )
        assertThat(r2.statusCode).isEqualTo(HttpStatus.CONFLICT)
        val err = requireNotNull(r2.body)
        assertThat(err["title"]).isEqualTo("Conflict")

        val ownersAfterSecond = ownerJpaRepository.findAll()
        val authsAfterSecond = authJpaRepository.findAll()
        assertThat(ownersAfterSecond).hasSize(1)
        assertThat(authsAfterSecond).hasSize(1)
        assertThat(ownersAfterSecond[0].nickname).isEqualTo("first")
        assertThat(authsAfterSecond[0].email).isEqualTo("dup@example.com")
    }

    @Test
    fun `should rollback Auth creation when Owner creation fails due to duplicate nickname`() {
        val first = SignUpOwnerRequest(
            businessNumber = "111111111",
            phoneNumber = "010-1111-1111",
            nickname = "samename",
            email = "email1@example.com",
            password = "Password123!",
        )

        val second = SignUpOwnerRequest(
            businessNumber = "222222222",
            phoneNumber = "010-2222-2222",
            nickname = "samename",
            email = "email2@example.com",
            password = "Password456!",
        )

        val r1 = rest.exchange(
            url("/api/sign-up/owner"),
            HttpMethod.POST,
            HttpEntity(first, headers),
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )
        assertThat(r1.statusCode).isEqualTo(HttpStatus.CREATED)

        val ownersAfterFirst = ownerJpaRepository.findAll()
        val authsAfterFirst = authJpaRepository.findAll()
        assertThat(ownersAfterFirst).hasSize(1)
        assertThat(authsAfterFirst).hasSize(1)

        val r2 = rest.exchange(
            url("/api/sign-up/owner"),
            HttpMethod.POST,
            HttpEntity(second, headers),
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )
        assertThat(r2.statusCode).isEqualTo(HttpStatus.CONFLICT)

        val ownersAfterSecond = ownerJpaRepository.findAll()
        val authsAfterSecond = authJpaRepository.findAll()
        assertThat(ownersAfterSecond).hasSize(1)
        assertThat(authsAfterSecond).hasSize(1)
        assertThat(authsAfterSecond[0].email).isEqualTo("email1@example.com")
    }

    @Test
    fun `should return 422 UNPROCESSABLE_ENTITY when password is invalid and not create Owner or Auth`() {
        val request = SignUpOwnerRequest(
            businessNumber = "123456789",
            phoneNumber = "010-1234-5678",
            nickname = "testowner",
            email = "test@example.com",
            password = "invalid",
        )

        val response = rest.exchange(
            url("/api/sign-up/owner"),
            HttpMethod.POST,
            HttpEntity(request, headers),
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
        val err = requireNotNull(response.body)
        assertThat(err["title"]).isEqualTo("Unprocessable Entity")

        val owners = ownerJpaRepository.findAll()
        val auths = authJpaRepository.findAll()
        assertThat(owners).isEmpty()
        assertThat(auths).isEmpty()
    }

    @Test
    fun `should return 422 UNPROCESSABLE_ENTITY when email is invalid and not create Owner or Auth`() {
        val request = SignUpOwnerRequest(
            businessNumber = "123456789",
            phoneNumber = "010-1234-5678",
            nickname = "testowner",
            email = "invalid-email",
            password = "Password123!",
        )

        val response = rest.exchange(
            url("/api/sign-up/owner"),
            HttpMethod.POST,
            HttpEntity(request, headers),
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
        val err = requireNotNull(response.body)
        assertThat(err["title"]).isEqualTo("Unprocessable Entity")

        val owners = ownerJpaRepository.findAll()
        val auths = authJpaRepository.findAll()
        assertThat(owners).isEmpty()
        assertThat(auths).isEmpty()
    }
}
