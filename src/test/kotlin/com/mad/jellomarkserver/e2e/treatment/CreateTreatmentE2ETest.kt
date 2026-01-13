package com.mad.jellomarkserver.e2e.treatment

import com.mad.jellomarkserver.apigateway.adapter.driving.web.request.CreateBeautishopRequest
import com.mad.jellomarkserver.apigateway.adapter.driving.web.request.CreateTreatmentRequest
import com.mad.jellomarkserver.apigateway.adapter.driving.web.request.SignUpOwnerRequest
import com.mad.jellomarkserver.treatment.adapter.driven.persistence.repository.TreatmentJpaRepository
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
        "classpath:sql/truncate-treatments.sql",
        "classpath:sql/truncate-beautishops.sql",
        "classpath:sql/truncate-owners.sql",
        "classpath:sql/truncate-auths.sql",
        "classpath:sql/truncate-refresh-tokens.sql"
    ],
    executionPhase = ExecutionPhase.BEFORE_TEST_METHOD
)
class CreateTreatmentE2ETest {

    @LocalServerPort
    private var port: Int = 0

    @Autowired
    lateinit var rest: TestRestTemplate

    @Autowired
    lateinit var treatmentJpaRepository: TreatmentJpaRepository

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

    private fun createBeautishop(accessToken: String, regNum: String = "123-45-67890"): String {
        val request = CreateBeautishopRequest(
            shopName = "Beauty Salon",
            shopRegNum = regNum,
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
    fun `should create treatment successfully when owner is authorized`() {
        val accessToken = signUpOwnerAndGetAccessToken()
        val shopId = createBeautishop(accessToken)

        val request = CreateTreatmentRequest(
            treatmentName = "젤네일",
            price = 50000,
            duration = 60,
            description = "기본 젤네일 시술입니다"
        )

        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            set("Authorization", "Bearer $accessToken")
        }

        val response = rest.exchange(
            url("/api/beautishops/$shopId/treatments"),
            HttpMethod.POST,
            HttpEntity(request, headers),
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.CREATED)
        val json = requireNotNull(response.body)
        assertThat(json["id"]).isNotNull()
        assertThat(json["shopId"]).isEqualTo(shopId)
        assertThat(json["name"]).isEqualTo("젤네일")
        assertThat(json["price"]).isEqualTo(50000)
        assertThat(json["duration"]).isEqualTo(60)
        assertThat(json["description"]).isEqualTo("기본 젤네일 시술입니다")
        assertThat(json["createdAt"]).isNotNull()
        assertThat(json["updatedAt"]).isNotNull()

        val saved = treatmentJpaRepository.findAll()
        assertThat(saved).hasSize(1)
        assertThat(saved[0].name).isEqualTo("젤네일")
    }

    @Test
    fun `should create treatment successfully without description`() {
        val accessToken = signUpOwnerAndGetAccessToken()
        val shopId = createBeautishop(accessToken)

        val request = CreateTreatmentRequest(
            treatmentName = "젤네일",
            price = 50000,
            duration = 60,
            description = null
        )

        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            set("Authorization", "Bearer $accessToken")
        }

        val response = rest.exchange(
            url("/api/beautishops/$shopId/treatments"),
            HttpMethod.POST,
            HttpEntity(request, headers),
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.CREATED)
        val json = requireNotNull(response.body)
        assertThat(json["description"]).isNull()
    }

    @Test
    fun `should return 403 when owner tries to add treatment to another owner's beautishop`() {
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

        val request = CreateTreatmentRequest(
            treatmentName = "젤네일",
            price = 50000,
            duration = 60,
            description = null
        )

        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            set("Authorization", "Bearer $ownerToken2")
        }

        val response = rest.exchange(
            url("/api/beautishops/$shopId/treatments"),
            HttpMethod.POST,
            HttpEntity(request, headers),
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.FORBIDDEN)
    }

    @Test
    fun `should return 404 when beautishop does not exist`() {
        val accessToken = signUpOwnerAndGetAccessToken()
        val nonExistentShopId = "00000000-0000-0000-0000-000000000000"

        val request = CreateTreatmentRequest(
            treatmentName = "젤네일",
            price = 50000,
            duration = 60,
            description = null
        )

        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            set("Authorization", "Bearer $accessToken")
        }

        val response = rest.exchange(
            url("/api/beautishops/$nonExistentShopId/treatments"),
            HttpMethod.POST,
            HttpEntity(request, headers),
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
    }

    @Test
    fun `should return 401 when authorization header is missing`() {
        val accessToken = signUpOwnerAndGetAccessToken()
        val shopId = createBeautishop(accessToken)

        val request = CreateTreatmentRequest(
            treatmentName = "젤네일",
            price = 50000,
            duration = 60,
            description = null
        )

        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
        }

        val response = rest.exchange(
            url("/api/beautishops/$shopId/treatments"),
            HttpMethod.POST,
            HttpEntity(request, headers),
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)
    }

    @Test
    fun `should return 422 when treatment name is invalid`() {
        val accessToken = signUpOwnerAndGetAccessToken()
        val shopId = createBeautishop(accessToken)

        val request = CreateTreatmentRequest(
            treatmentName = "A",
            price = 50000,
            duration = 60,
            description = null
        )

        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            set("Authorization", "Bearer $accessToken")
        }

        val response = rest.exchange(
            url("/api/beautishops/$shopId/treatments"),
            HttpMethod.POST,
            HttpEntity(request, headers),
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
    }

    @Test
    fun `should return 422 when price is negative`() {
        val accessToken = signUpOwnerAndGetAccessToken()
        val shopId = createBeautishop(accessToken)

        val request = CreateTreatmentRequest(
            treatmentName = "젤네일",
            price = -1,
            duration = 60,
            description = null
        )

        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            set("Authorization", "Bearer $accessToken")
        }

        val response = rest.exchange(
            url("/api/beautishops/$shopId/treatments"),
            HttpMethod.POST,
            HttpEntity(request, headers),
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
    }

    @Test
    fun `should return 422 when duration is out of range`() {
        val accessToken = signUpOwnerAndGetAccessToken()
        val shopId = createBeautishop(accessToken)

        val request = CreateTreatmentRequest(
            treatmentName = "젤네일",
            price = 50000,
            duration = 5,
            description = null
        )

        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            set("Authorization", "Bearer $accessToken")
        }

        val response = rest.exchange(
            url("/api/beautishops/$shopId/treatments"),
            HttpMethod.POST,
            HttpEntity(request, headers),
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
    }
}
