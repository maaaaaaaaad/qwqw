package com.mad.jellomarkserver.e2e.beautishop

import com.mad.jellomarkserver.apigateway.adapter.driving.web.request.CreateBeautishopRequest
import com.mad.jellomarkserver.apigateway.adapter.driving.web.request.SignUpOwnerRequest
import com.mad.jellomarkserver.beautishop.adapter.driven.persistence.repository.BeautishopJpaRepository
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
        "classpath:sql/truncate-beautishops.sql",
        "classpath:sql/truncate-owners.sql",
        "classpath:sql/truncate-auths.sql",
        "classpath:sql/truncate-refresh-tokens.sql"
    ],
    executionPhase = ExecutionPhase.BEFORE_TEST_METHOD
)
class CreateBeautishopE2ETest {

    @LocalServerPort
    private var port: Int = 0

    @Autowired
    lateinit var rest: TestRestTemplate

    @Autowired
    lateinit var beautishopJpaRepository: BeautishopJpaRepository

    private fun url(path: String) = "http://localhost:$port$path"

    private fun signUpOwnerAndGetAccessToken(): String {
        val request = SignUpOwnerRequest(
            businessNumber = "123456789",
            phoneNumber = "010-1234-5678",
            nickname = "testshop",
            email = "owner@example.com",
            password = "Password123!",
        )

        val headers = HttpHeaders().apply { contentType = MediaType.APPLICATION_JSON }

        val response = rest.exchange(
            url("/api/sign-up/owner"),
            HttpMethod.POST,
            HttpEntity(request, headers),
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )

        val json = requireNotNull(response.body)
        return json["accessToken"] as String
    }

    @Test
    fun `should create beautishop when owner provides valid data`() {
        val accessToken = signUpOwnerAndGetAccessToken()

        val request = CreateBeautishopRequest(
            shopName = "Beauty Salon",
            shopRegNum = "123-45-67890",
            shopPhoneNumber = "02-1234-5678",
            shopAddress = "Seoul Gangnam-gu",
            latitude = 37.4979,
            longitude = 127.0276,
            operatingTime = mapOf(
                "Monday" to "09:00-18:00",
                "Tuesday" to "09:00-18:00"
            ),
            shopDescription = "Premium beauty salon",
            shopImages = listOf("https://example.com/image.jpg")
        )

        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            set("Authorization", "Bearer $accessToken")
        }

        val response = rest.exchange(
            url("/api/beautishops"),
            HttpMethod.POST,
            HttpEntity(request, headers),
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.CREATED)
        val json = requireNotNull(response.body)
        assertThat(json["id"]).isNotNull()
        assertThat(json["name"]).isEqualTo("Beauty Salon")
        assertThat(json["regNum"]).isEqualTo("123-45-67890")
        assertThat(json["phoneNumber"]).isEqualTo("02-1234-5678")
        assertThat(json["address"]).isEqualTo("Seoul Gangnam-gu")
        assertThat(json["latitude"]).isEqualTo(37.4979)
        assertThat(json["longitude"]).isEqualTo(127.0276)
        assertThat(json["operatingTime"]).isNotNull()
        assertThat(json["description"]).isEqualTo("Premium beauty salon")
        assertThat(json["image"]).isEqualTo("https://example.com/image.jpg")
        assertThat(json["createdAt"]).isNotNull()
        assertThat(json["updatedAt"]).isNotNull()

        val saved = beautishopJpaRepository.findAll()
        assertThat(saved).hasSize(1)
        assertThat(saved[0].name).isEqualTo("Beauty Salon")
    }

    @Test
    fun `should return 401 when authorization header is missing`() {
        val request = CreateBeautishopRequest(
            shopName = "Beauty Salon",
            shopRegNum = "123-45-67890",
            shopPhoneNumber = "02-1234-5678",
            shopAddress = "Seoul Gangnam-gu",
            latitude = 37.4979,
            longitude = 127.0276,
            operatingTime = mapOf("Monday" to "09:00-18:00"),
            shopDescription = null,
            shopImages = null
        )

        val headers = HttpHeaders().apply { contentType = MediaType.APPLICATION_JSON }

        val response = rest.exchange(
            url("/api/beautishops"),
            HttpMethod.POST,
            HttpEntity(request, headers),
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)
    }

    @Test
    fun `should return 401 when invalid access token is provided`() {
        signUpOwnerAndGetAccessToken()

        val request = CreateBeautishopRequest(
            shopName = "Beauty Salon",
            shopRegNum = "123-45-67890",
            shopPhoneNumber = "02-1234-5678",
            shopAddress = "Seoul Gangnam-gu",
            latitude = 37.4979,
            longitude = 127.0276,
            operatingTime = mapOf("Monday" to "09:00-18:00"),
            shopDescription = null,
            shopImages = null
        )

        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            set("Authorization", "Bearer invalid-token")
        }

        val response = rest.exchange(
            url("/api/beautishops"),
            HttpMethod.POST,
            HttpEntity(request, headers),
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)
    }
}
