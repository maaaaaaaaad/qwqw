package com.mad.jellomarkserver.e2e.treatment

import com.mad.jellomarkserver.apigateway.adapter.driving.web.request.CreateBeautishopRequest
import com.mad.jellomarkserver.apigateway.adapter.driving.web.request.CreateTreatmentRequest
import com.mad.jellomarkserver.apigateway.adapter.driving.web.request.SignUpOwnerRequest
import com.mad.jellomarkserver.apigateway.adapter.driving.web.request.UpdateTreatmentRequest
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
class UpdateTreatmentE2ETest {

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

        val json = requireNotNull(response.body)
        return json["id"] as String
    }

    private fun createTreatment(accessToken: String, shopId: String): String {
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

        val json = requireNotNull(response.body)
        return json["id"] as String
    }

    @Test
    fun `should update treatment successfully when owner provides valid data`() {
        val accessToken = signUpOwnerAndGetAccessToken()
        val shopId = createBeautishop(accessToken)
        val treatmentId = createTreatment(accessToken, shopId)

        val updateRequest = UpdateTreatmentRequest(
            treatmentName = "속눈썹 연장",
            price = 80000,
            duration = 90,
            description = "프리미엄 속눈썹 연장 시술"
        )

        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            set("Authorization", "Bearer $accessToken")
        }

        val response = rest.exchange(
            url("/api/treatments/$treatmentId"),
            HttpMethod.PUT,
            HttpEntity(updateRequest, headers),
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val json = requireNotNull(response.body)
        assertThat(json["id"]).isEqualTo(treatmentId)
        assertThat(json["shopId"]).isEqualTo(shopId)
        assertThat(json["name"]).isEqualTo("속눈썹 연장")
        assertThat(json["price"]).isEqualTo(80000)
        assertThat(json["duration"]).isEqualTo(90)
        assertThat(json["description"]).isEqualTo("프리미엄 속눈썹 연장 시술")

        val saved = treatmentJpaRepository.findAll().first()
        assertThat(saved.name).isEqualTo("속눈썹 연장")
        assertThat(saved.price).isEqualTo(80000)
    }

    @Test
    fun `should update treatment successfully without description`() {
        val accessToken = signUpOwnerAndGetAccessToken()
        val shopId = createBeautishop(accessToken)
        val treatmentId = createTreatment(accessToken, shopId)

        val updateRequest = UpdateTreatmentRequest(
            treatmentName = "속눈썹 연장",
            price = 80000,
            duration = 90,
            description = null
        )

        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            set("Authorization", "Bearer $accessToken")
        }

        val response = rest.exchange(
            url("/api/treatments/$treatmentId"),
            HttpMethod.PUT,
            HttpEntity(updateRequest, headers),
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val json = requireNotNull(response.body)
        assertThat(json["description"]).isNull()
    }

    @Test
    fun `should return 403 when owner tries to update another owner's treatment`() {
        val ownerToken1 = signUpOwnerAndGetAccessToken(
            email = "owner1@example.com",
            businessNumber = "123456789",
            phoneNumber = "010-1111-1111",
            nickname = "owner1"
        )
        val shopId = createBeautishop(ownerToken1)
        val treatmentId = createTreatment(ownerToken1, shopId)

        val ownerToken2 = signUpOwnerAndGetAccessToken(
            email = "owner2@example.com",
            businessNumber = "987654321",
            phoneNumber = "010-2222-2222",
            nickname = "owner2"
        )

        val updateRequest = UpdateTreatmentRequest(
            treatmentName = "속눈썹 연장",
            price = 80000,
            duration = 90,
            description = null
        )

        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            set("Authorization", "Bearer $ownerToken2")
        }

        val response = rest.exchange(
            url("/api/treatments/$treatmentId"),
            HttpMethod.PUT,
            HttpEntity(updateRequest, headers),
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.FORBIDDEN)
    }

    @Test
    fun `should return 404 when treatment does not exist`() {
        val accessToken = signUpOwnerAndGetAccessToken()
        val nonExistentTreatmentId = "00000000-0000-0000-0000-000000000000"

        val updateRequest = UpdateTreatmentRequest(
            treatmentName = "속눈썹 연장",
            price = 80000,
            duration = 90,
            description = null
        )

        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            set("Authorization", "Bearer $accessToken")
        }

        val response = rest.exchange(
            url("/api/treatments/$nonExistentTreatmentId"),
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
        val treatmentId = createTreatment(accessToken, shopId)

        val updateRequest = UpdateTreatmentRequest(
            treatmentName = "속눈썹 연장",
            price = 80000,
            duration = 90,
            description = null
        )

        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
        }

        val response = rest.exchange(
            url("/api/treatments/$treatmentId"),
            HttpMethod.PUT,
            HttpEntity(updateRequest, headers),
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)
    }

    @Test
    fun `should return 422 when treatment name is invalid`() {
        val accessToken = signUpOwnerAndGetAccessToken()
        val shopId = createBeautishop(accessToken)
        val treatmentId = createTreatment(accessToken, shopId)

        val updateRequest = UpdateTreatmentRequest(
            treatmentName = "A",
            price = 80000,
            duration = 90,
            description = null
        )

        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            set("Authorization", "Bearer $accessToken")
        }

        val response = rest.exchange(
            url("/api/treatments/$treatmentId"),
            HttpMethod.PUT,
            HttpEntity(updateRequest, headers),
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
    }

    @Test
    fun `should return 422 when price is negative`() {
        val accessToken = signUpOwnerAndGetAccessToken()
        val shopId = createBeautishop(accessToken)
        val treatmentId = createTreatment(accessToken, shopId)

        val updateRequest = UpdateTreatmentRequest(
            treatmentName = "속눈썹 연장",
            price = -1,
            duration = 90,
            description = null
        )

        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            set("Authorization", "Bearer $accessToken")
        }

        val response = rest.exchange(
            url("/api/treatments/$treatmentId"),
            HttpMethod.PUT,
            HttpEntity(updateRequest, headers),
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
    }

    @Test
    fun `should return 422 when duration is out of range`() {
        val accessToken = signUpOwnerAndGetAccessToken()
        val shopId = createBeautishop(accessToken)
        val treatmentId = createTreatment(accessToken, shopId)

        val updateRequest = UpdateTreatmentRequest(
            treatmentName = "속눈썹 연장",
            price = 80000,
            duration = 5,
            description = null
        )

        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            set("Authorization", "Bearer $accessToken")
        }

        val response = rest.exchange(
            url("/api/treatments/$treatmentId"),
            HttpMethod.PUT,
            HttpEntity(updateRequest, headers),
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
    }
}
