package com.mad.jellomarkserver.e2e.beautishop

import com.mad.jellomarkserver.apigateway.adapter.driving.web.request.CreateBeautishopRequest
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
        "classpath:sql/truncate-beautishops.sql",
        "classpath:sql/truncate-owners.sql",
        "classpath:sql/truncate-auths.sql",
        "classpath:sql/truncate-refresh-tokens.sql"
    ],
    executionPhase = ExecutionPhase.BEFORE_TEST_METHOD
)
class ListBeautishopsE2ETest {

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

    private fun createBeautishop(accessToken: String, shopName: String, regNum: String) {
        val request = CreateBeautishopRequest(
            shopName = shopName,
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

        rest.exchange(
            url("/api/beautishops"),
            HttpMethod.POST,
            HttpEntity(request, headers),
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )
    }

    @Test
    fun `should return paginated list of beautishops`() {
        val accessToken = signUpOwnerAndGetAccessToken()
        createBeautishop(accessToken, "Shop A", "111-11-11111")
        createBeautishop(accessToken, "Shop B", "222-22-22222")
        createBeautishop(accessToken, "Shop C", "333-33-33333")

        val response = rest.exchange(
            url("/api/beautishops?page=0&size=2"),
            HttpMethod.GET,
            null,
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val json = requireNotNull(response.body)

        @Suppress("UNCHECKED_CAST")
        val items = json["items"] as List<Map<String, Any?>>
        assertThat(items).hasSize(2)
        assertThat(json["hasNext"]).isEqualTo(true)
        assertThat(json["totalElements"]).isEqualTo(3)
    }

    @Test
    fun `should return empty list when no beautishops exist`() {
        val response = rest.exchange(
            url("/api/beautishops?page=0&size=20"),
            HttpMethod.GET,
            null,
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val json = requireNotNull(response.body)

        @Suppress("UNCHECKED_CAST")
        val items = json["items"] as List<Map<String, Any?>>
        assertThat(items).isEmpty()
        assertThat(json["hasNext"]).isEqualTo(false)
        assertThat(json["totalElements"]).isEqualTo(0)
    }

    @Test
    fun `should return hasNext false on last page`() {
        val accessToken = signUpOwnerAndGetAccessToken()
        createBeautishop(accessToken, "Shop A", "111-11-11111")
        createBeautishop(accessToken, "Shop B", "222-22-22222")

        val response = rest.exchange(
            url("/api/beautishops?page=0&size=20"),
            HttpMethod.GET,
            null,
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val json = requireNotNull(response.body)

        @Suppress("UNCHECKED_CAST")
        val items = json["items"] as List<Map<String, Any?>>
        assertThat(items).hasSize(2)
        assertThat(json["hasNext"]).isEqualTo(false)
        assertThat(json["totalElements"]).isEqualTo(2)
    }
}
