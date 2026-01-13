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
class ListTreatmentsE2ETest {

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

    private fun createTreatment(accessToken: String, shopId: String, name: String, price: Int): String {
        val request = CreateTreatmentRequest(
            treatmentName = name,
            price = price,
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

        val json = requireNotNull(response.body)
        return json["id"] as String
    }

    @Test
    fun `should list treatments for shop`() {
        val accessToken = signUpOwnerAndGetAccessToken()
        val shopId = createBeautishop(accessToken)
        createTreatment(accessToken, shopId, "젤네일", 50000)
        createTreatment(accessToken, shopId, "속눈썹", 30000)

        val response = rest.exchange(
            url("/api/beautishops/$shopId/treatments"),
            HttpMethod.GET,
            HttpEntity<Void>(HttpHeaders()),
            object : ParameterizedTypeReference<List<Map<String, Any?>>>() {}
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val treatments = requireNotNull(response.body)
        assertThat(treatments).hasSize(2)
        assertThat(treatments.map { it["name"] }).containsExactlyInAnyOrder("젤네일", "속눈썹")
    }

    @Test
    fun `should return empty list when shop has no treatments`() {
        val accessToken = signUpOwnerAndGetAccessToken()
        val shopId = createBeautishop(accessToken)

        val response = rest.exchange(
            url("/api/beautishops/$shopId/treatments"),
            HttpMethod.GET,
            HttpEntity<Void>(HttpHeaders()),
            object : ParameterizedTypeReference<List<Map<String, Any?>>>() {}
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val treatments = requireNotNull(response.body)
        assertThat(treatments).isEmpty()
    }

    @Test
    fun `should return empty list for non-existent shop`() {
        val nonExistentShopId = "00000000-0000-0000-0000-000000000000"

        val response = rest.exchange(
            url("/api/beautishops/$nonExistentShopId/treatments"),
            HttpMethod.GET,
            HttpEntity<Void>(HttpHeaders()),
            object : ParameterizedTypeReference<List<Map<String, Any?>>>() {}
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val treatments = requireNotNull(response.body)
        assertThat(treatments).isEmpty()
    }
}
