package com.mad.jellomarkserver.e2e.owner.sign_up

import com.mad.jellomarkserver.apigateway.adapter.driving.web.request.SignUpOwnerRequest
import com.mad.jellomarkserver.auth.adapter.driven.persistence.repository.AuthJpaRepository
import com.mad.jellomarkserver.auth.adapter.driven.persistence.repository.RefreshTokenJpaRepository
import com.mad.jellomarkserver.owner.adapter.driven.persistence.repository.OwnerJpaRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mindrot.jbcrypt.BCrypt
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
class SignUpOwnerWithAuthE2ETest {

    @LocalServerPort
    private var port: Int = 0

    @Autowired
    lateinit var rest: TestRestTemplate

    @Autowired
    lateinit var ownerJpaRepository: OwnerJpaRepository

    @Autowired
    lateinit var authJpaRepository: AuthJpaRepository

    @Autowired
    lateinit var refreshTokenJpaRepository: RefreshTokenJpaRepository

    private fun url(path: String) = "http://localhost:$port$path"

    private val headers = HttpHeaders().apply { contentType = MediaType.APPLICATION_JSON }

    @Test
    fun `should create both Owner and Auth when signing up`() {
        val request = SignUpOwnerRequest(
            businessNumber = "123456789",
            phoneNumber = "010-1234-5678",
            nickname = "testshop",
            email = "owner@example.com",
            password = "Password123!",
        )

        val response = rest.exchange(
            url("/api/sign-up/owner"),
            HttpMethod.POST,
            HttpEntity(request, headers),
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.CREATED)
        val json = requireNotNull(response.body)
        assertThat(json["id"]).isNotNull()
        assertThat(json["businessNumber"]).isEqualTo("123456789")
        assertThat(json["phoneNumber"]).isEqualTo("010-1234-5678")
        assertThat(json["nickname"]).isEqualTo("testshop")
        assertThat(json["accessToken"]).isNotNull()
        assertThat(json["refreshToken"]).isNotNull()

        val owners = ownerJpaRepository.findAll()
        assertThat(owners).hasSize(1)
        val ownerEntity = owners[0]
        assertThat(ownerEntity.businessNumber).isEqualTo("123456789")
        assertThat(ownerEntity.phoneNumber).isEqualTo("010-1234-5678")
        assertThat(ownerEntity.nickname).isEqualTo("testshop")

        val auths = authJpaRepository.findAll()
        assertThat(auths).hasSize(1)
        val authEntity = auths[0]
        assertThat(authEntity.email).isEqualTo("owner@example.com")
        assertThat(authEntity.userType).isEqualTo("OWNER")

        assertThat(authEntity.hashedPassword).isNotEqualTo("Password123!")
        assertThat(BCrypt.checkpw("Password123!", authEntity.hashedPassword)).isTrue()

        val refreshTokens = refreshTokenJpaRepository.findAll()
        assertThat(refreshTokens).hasSize(1)
        val refreshTokenEntity = refreshTokens[0]
        assertThat(refreshTokenEntity.identifier).isEqualTo("owner@example.com")
        assertThat(refreshTokenEntity.token).isEqualTo(json["refreshToken"])
    }

    @Test
    fun `should create Auth with different passwords for different owners`() {
        val request1 = SignUpOwnerRequest(
            businessNumber = "111111111",
            phoneNumber = "010-1111-1111",
            nickname = "shop1",
            email = "owner1@example.com",
            password = "Password111!",
        )

        val request2 = SignUpOwnerRequest(
            businessNumber = "222222222",
            phoneNumber = "010-2222-2222",
            nickname = "shop2",
            email = "owner2@example.com",
            password = "Password222!",
        )

        rest.exchange(
            url("/api/sign-up/owner"),
            HttpMethod.POST,
            HttpEntity(request1, headers),
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )

        rest.exchange(
            url("/api/sign-up/owner"),
            HttpMethod.POST,
            HttpEntity(request2, headers),
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )

        val auths = authJpaRepository.findAll()
        assertThat(auths).hasSize(2)

        val auth1 = auths.find { it.email == "owner1@example.com" }
        val auth2 = auths.find { it.email == "owner2@example.com" }

        assertThat(auth1).isNotNull
        assertThat(auth2).isNotNull
        assertThat(auth1!!.hashedPassword).isNotEqualTo(auth2!!.hashedPassword)
        assertThat(BCrypt.checkpw("Password111!", auth1.hashedPassword)).isTrue()
        assertThat(BCrypt.checkpw("Password222!", auth2.hashedPassword)).isTrue()
    }

    @Test
    fun `should create Auth with OWNER user type`() {
        val request = SignUpOwnerRequest(
            businessNumber = "333333333",
            phoneNumber = "010-3333-3333",
            nickname = "shop3",
            email = "shop3@example.com",
            password = "Password123!",
        )

        val response = rest.exchange(
            url("/api/sign-up/owner"),
            HttpMethod.POST,
            HttpEntity(request, headers),
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.CREATED)

        val auths = authJpaRepository.findAll()
        assertThat(auths).hasSize(1)
        assertThat(auths[0].userType).isEqualTo("OWNER")
    }
}
