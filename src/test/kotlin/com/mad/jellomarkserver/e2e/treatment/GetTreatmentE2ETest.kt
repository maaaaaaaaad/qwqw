package com.mad.jellomarkserver.e2e.treatment

import com.mad.jellomarkserver.apigateway.adapter.driving.web.request.CreateBeautishopRequest
import com.mad.jellomarkserver.apigateway.adapter.driving.web.request.CreateTreatmentRequest
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
        "classpath:sql/truncate-treatments.sql",
        "classpath:sql/truncate-beautishops.sql",
        "classpath:sql/truncate-owners.sql",
        "classpath:sql/truncate-auths.sql",
        "classpath:sql/truncate-refresh-tokens.sql"
    ],
    executionPhase = ExecutionPhase.BEFORE_TEST_METHOD
)
class GetTreatmentE2ETest {

    @LocalServerPort
    private var port: Int = 0

    @Autowired
    lateinit var rest: TestRestTemplate

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

    private fun createTreatment(accessToken: String, shopId: String): String {
        val request = CreateTreatmentRequest(
            treatmentName = "젤네일",
            price = 50000,
            duration = 60,
            description = "기본 젤네일 시술"
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
    fun `should get treatment successfully`() {
        val accessToken = signUpOwnerAndGetAccessToken()
        val shopId = createBeautishop(accessToken)
        val treatmentId = createTreatment(accessToken, shopId)

        val response = rest.exchange(
            url("/api/treatments/$treatmentId"),
            HttpMethod.GET,
            HttpEntity<Void>(HttpHeaders()),
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val json = requireNotNull(response.body)
        assertThat(json["id"]).isEqualTo(treatmentId)
        assertThat(json["shopId"]).isEqualTo(shopId)
        assertThat(json["name"]).isEqualTo("젤네일")
        assertThat(json["price"]).isEqualTo(50000)
        assertThat(json["duration"]).isEqualTo(60)
        assertThat(json["description"]).isEqualTo("기본 젤네일 시술")
    }

    @Test
    fun `should return 404 when treatment does not exist`() {
        val nonExistentId = "00000000-0000-0000-0000-000000000000"

        val response = rest.exchange(
            url("/api/treatments/$nonExistentId"),
            HttpMethod.GET,
            HttpEntity<Void>(HttpHeaders()),
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
    }
}
