package com.mad.jellomarkserver.e2e.beautishop

import com.mad.jellomarkserver.apigateway.adapter.driving.web.request.CreateBeautishopRequest
import com.mad.jellomarkserver.apigateway.adapter.driving.web.request.SignUpOwnerRequest
import com.mad.jellomarkserver.apigateway.adapter.driving.web.request.UpdateBeautishopRequest
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
class UpdateBeautishopE2ETest {

    @LocalServerPort
    private var port: Int = 0

    @Autowired
    lateinit var rest: TestRestTemplate

    @Autowired
    lateinit var beautishopJpaRepository: BeautishopJpaRepository

    private fun url(path: String) = "http://localhost:$port$path"

    private fun signUpOwnerAndGetAccessToken(
        email: String = "owner@example.com",
        businessNumber: String = "123456789",
        phoneNumber: String = "010-1234-5678",
        nickname: String = "testshop"
    ): String {
        val request = SignUpOwnerRequest(
            businessNumber = businessNumber,
            phoneNumber = phoneNumber,
            nickname = nickname,
            email = email,
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

    private fun createBeautishop(accessToken: String): String {
        val request = CreateBeautishopRequest(
            shopName = "Beauty Salon",
            shopRegNum = "123-45-67890",
            shopPhoneNumber = "02-1234-5678",
            shopAddress = "Seoul Gangnam-gu",
            latitude = 37.4979,
            longitude = 127.0276,
            operatingTime = mapOf("Monday" to "09:00-18:00"),
            shopDescription = "Premium beauty salon",
            shopImage = "https://example.com/image.jpg"
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

        val json = requireNotNull(response.body)
        return json["id"] as String
    }

    @Test
    fun `should update beautishop successfully when owner provides valid data`() {
        val accessToken = signUpOwnerAndGetAccessToken()
        val shopId = createBeautishop(accessToken)

        val updateRequest = UpdateBeautishopRequest(
            operatingTime = mapOf("Tuesday" to "10:00-20:00", "Wednesday" to "10:00-20:00"),
            shopDescription = "Updated premium beauty salon",
            shopImage = "https://example.com/new-image.jpg"
        )

        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            set("Authorization", "Bearer $accessToken")
        }

        val response = rest.exchange(
            url("/api/beautishops/$shopId"),
            HttpMethod.PUT,
            HttpEntity(updateRequest, headers),
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val json = requireNotNull(response.body)
        assertThat(json["id"]).isEqualTo(shopId)
        assertThat(json["name"]).isEqualTo("Beauty Salon")
        assertThat(json["description"]).isEqualTo("Updated premium beauty salon")
        assertThat(json["image"]).isEqualTo("https://example.com/new-image.jpg")
        @Suppress("UNCHECKED_CAST")
        val operatingTime = json["operatingTime"] as Map<String, String>
        assertThat(operatingTime).containsEntry("Tuesday", "10:00-20:00")
        assertThat(operatingTime).containsEntry("Wednesday", "10:00-20:00")

        val saved = beautishopJpaRepository.findAll().first()
        assertThat(saved.description).isEqualTo("Updated premium beauty salon")
    }

    @Test
    fun `should keep existing operatingTime when operatingTime is null`() {
        val accessToken = signUpOwnerAndGetAccessToken()
        val shopId = createBeautishop(accessToken)

        val updateRequest = UpdateBeautishopRequest(
            operatingTime = null,
            shopDescription = "Only description updated",
            shopImage = null
        )

        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            set("Authorization", "Bearer $accessToken")
        }

        val response = rest.exchange(
            url("/api/beautishops/$shopId"),
            HttpMethod.PUT,
            HttpEntity(updateRequest, headers),
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val json = requireNotNull(response.body)

        @Suppress("UNCHECKED_CAST")
        val operatingTime = json["operatingTime"] as Map<String, String>
        assertThat(operatingTime).containsEntry("Monday", "09:00-18:00")
        assertThat(json["description"]).isEqualTo("Only description updated")
        assertThat(json["image"]).isNull()
    }

    @Test
    fun `should return 403 when owner tries to update another owner's beautishop`() {
        val ownerToken1 = signUpOwnerAndGetAccessToken(
            email = "owner1@example.com",
            businessNumber = "123456789",
            phoneNumber = "010-1111-1111",
            nickname = "owner1"
        )
        val shopId = createBeautishop(ownerToken1)

        val ownerToken2 = signUpOwnerAndGetAccessToken(
            email = "owner2@example.com",
            businessNumber = "987654321",
            phoneNumber = "010-2222-2222",
            nickname = "owner2"
        )

        val updateRequest = UpdateBeautishopRequest(
            operatingTime = mapOf("Tuesday" to "10:00-20:00"),
            shopDescription = "Trying to update",
            shopImage = null
        )

        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            set("Authorization", "Bearer $ownerToken2")
        }

        val response = rest.exchange(
            url("/api/beautishops/$shopId"),
            HttpMethod.PUT,
            HttpEntity(updateRequest, headers),
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.FORBIDDEN)
    }

    @Test
    fun `should return 404 when beautishop does not exist`() {
        val accessToken = signUpOwnerAndGetAccessToken()
        val nonExistentShopId = "00000000-0000-0000-0000-000000000000"

        val updateRequest = UpdateBeautishopRequest(
            operatingTime = mapOf("Tuesday" to "10:00-20:00"),
            shopDescription = "Trying to update non-existent shop",
            shopImage = null
        )

        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            set("Authorization", "Bearer $accessToken")
        }

        val response = rest.exchange(
            url("/api/beautishops/$nonExistentShopId"),
            HttpMethod.PUT,
            HttpEntity(updateRequest, headers),
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
    }

    @Test
    fun `should return 401 when authorization header is missing`() {
        val accessToken = signUpOwnerAndGetAccessToken()
        val shopId = createBeautishop(accessToken)

        val updateRequest = UpdateBeautishopRequest(
            operatingTime = mapOf("Tuesday" to "10:00-20:00"),
            shopDescription = "Updated",
            shopImage = null
        )

        val headers = HttpHeaders().apply { contentType = MediaType.APPLICATION_JSON }

        val response = rest.exchange(
            url("/api/beautishops/$shopId"),
            HttpMethod.PUT,
            HttpEntity(updateRequest, headers),
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)
    }

    @Test
    fun `should return 422 when operatingTime is empty map`() {
        val accessToken = signUpOwnerAndGetAccessToken()
        val shopId = createBeautishop(accessToken)

        val updateRequest = UpdateBeautishopRequest(
            operatingTime = emptyMap(),
            shopDescription = "Updated",
            shopImage = null
        )

        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            set("Authorization", "Bearer $accessToken")
        }

        val response = rest.exchange(
            url("/api/beautishops/$shopId"),
            HttpMethod.PUT,
            HttpEntity(updateRequest, headers),
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
    }
}
