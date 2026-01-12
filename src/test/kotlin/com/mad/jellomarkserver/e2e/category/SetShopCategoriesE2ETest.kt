package com.mad.jellomarkserver.e2e.category

import com.mad.jellomarkserver.apigateway.adapter.driving.web.request.CreateBeautishopRequest
import com.mad.jellomarkserver.apigateway.adapter.driving.web.request.SetShopCategoriesRequest
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
import java.util.*

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(
    scripts = [
        "classpath:sql/truncate-shop-category-mappings.sql",
        "classpath:sql/truncate-categories.sql",
        "classpath:sql/truncate-beautishops.sql",
        "classpath:sql/truncate-owners.sql",
        "classpath:sql/truncate-auths.sql",
        "classpath:sql/truncate-refresh-tokens.sql",
        "classpath:sql/seed-categories.sql"
    ],
    executionPhase = ExecutionPhase.BEFORE_TEST_METHOD
)
class SetShopCategoriesE2ETest {

    @LocalServerPort
    private var port: Int = 0

    @Autowired
    lateinit var rest: TestRestTemplate

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

        assertThat(response.statusCode).isEqualTo(HttpStatus.CREATED)
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
    fun `should set categories for beautishop successfully`() {
        val accessToken = signUpOwnerAndGetAccessToken()
        val shopId = createBeautishop(accessToken)

        val categoryIds = listOf(
            "11111111-1111-1111-1111-111111111111",
            "22222222-2222-2222-2222-222222222222"
        )

        val request = SetShopCategoriesRequest(categoryIds = categoryIds)
        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            set("Authorization", "Bearer $accessToken")
        }

        val response = rest.exchange(
            url("/api/beautishops/$shopId/categories"),
            HttpMethod.PUT,
            HttpEntity(request, headers),
            object : ParameterizedTypeReference<List<Map<String, Any?>>>() {}
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val categories = requireNotNull(response.body)
        assertThat(categories).hasSize(2)
        assertThat(categories.map { it["id"] }).containsExactlyInAnyOrder(
            "11111111-1111-1111-1111-111111111111",
            "22222222-2222-2222-2222-222222222222"
        )
    }

    @Test
    fun `should return categories in getBeautishop after setting categories`() {
        val accessToken = signUpOwnerAndGetAccessToken()
        val shopId = createBeautishop(accessToken)

        val categoryIds = listOf(
            "11111111-1111-1111-1111-111111111111",
            "33333333-3333-3333-3333-333333333333"
        )

        val request = SetShopCategoriesRequest(categoryIds = categoryIds)
        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            set("Authorization", "Bearer $accessToken")
        }

        rest.exchange(
            url("/api/beautishops/$shopId/categories"),
            HttpMethod.PUT,
            HttpEntity(request, headers),
            object : ParameterizedTypeReference<List<Map<String, Any?>>>() {}
        )

        val getResponse = rest.exchange(
            url("/api/beautishops/$shopId"),
            HttpMethod.GET,
            null,
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )

        assertThat(getResponse.statusCode).isEqualTo(HttpStatus.OK)
        val json = requireNotNull(getResponse.body)

        @Suppress("UNCHECKED_CAST")
        val categories = json["categories"] as List<Map<String, Any?>>
        assertThat(categories).hasSize(2)
        assertThat(categories.map { it["name"] }).containsExactlyInAnyOrder("네일", "왁싱")
    }

    @Test
    fun `should replace existing categories when setting new ones`() {
        val accessToken = signUpOwnerAndGetAccessToken()
        val shopId = createBeautishop(accessToken)

        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            set("Authorization", "Bearer $accessToken")
        }

        val firstRequest = SetShopCategoriesRequest(
            categoryIds = listOf("11111111-1111-1111-1111-111111111111")
        )
        rest.exchange(
            url("/api/beautishops/$shopId/categories"),
            HttpMethod.PUT,
            HttpEntity(firstRequest, headers),
            object : ParameterizedTypeReference<List<Map<String, Any?>>>() {}
        )

        val secondRequest = SetShopCategoriesRequest(
            categoryIds = listOf(
                "22222222-2222-2222-2222-222222222222",
                "33333333-3333-3333-3333-333333333333"
            )
        )
        val response = rest.exchange(
            url("/api/beautishops/$shopId/categories"),
            HttpMethod.PUT,
            HttpEntity(secondRequest, headers),
            object : ParameterizedTypeReference<List<Map<String, Any?>>>() {}
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val categories = requireNotNull(response.body)
        assertThat(categories).hasSize(2)
        assertThat(categories.map { it["name"] }).containsExactlyInAnyOrder("속눈썹", "왁싱")
    }

    @Test
    fun `should clear all categories when empty list is provided`() {
        val accessToken = signUpOwnerAndGetAccessToken()
        val shopId = createBeautishop(accessToken)

        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            set("Authorization", "Bearer $accessToken")
        }

        val firstRequest = SetShopCategoriesRequest(
            categoryIds = listOf("11111111-1111-1111-1111-111111111111")
        )
        rest.exchange(
            url("/api/beautishops/$shopId/categories"),
            HttpMethod.PUT,
            HttpEntity(firstRequest, headers),
            object : ParameterizedTypeReference<List<Map<String, Any?>>>() {}
        )

        val clearRequest = SetShopCategoriesRequest(categoryIds = emptyList())
        val response = rest.exchange(
            url("/api/beautishops/$shopId/categories"),
            HttpMethod.PUT,
            HttpEntity(clearRequest, headers),
            object : ParameterizedTypeReference<List<Map<String, Any?>>>() {}
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val categories = requireNotNull(response.body)
        assertThat(categories).isEmpty()
    }

    @Test
    fun `should return 404 when category does not exist`() {
        val accessToken = signUpOwnerAndGetAccessToken()
        val shopId = createBeautishop(accessToken)

        val nonExistentCategoryId = UUID.randomUUID().toString()
        val request = SetShopCategoriesRequest(categoryIds = listOf(nonExistentCategoryId))
        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            set("Authorization", "Bearer $accessToken")
        }

        val response = rest.exchange(
            url("/api/beautishops/$shopId/categories"),
            HttpMethod.PUT,
            HttpEntity(request, headers),
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
    }

    @Test
    fun `should return 404 when beautishop does not exist`() {
        val accessToken = signUpOwnerAndGetAccessToken()
        val nonExistentShopId = UUID.randomUUID().toString()

        val request = SetShopCategoriesRequest(
            categoryIds = listOf("11111111-1111-1111-1111-111111111111")
        )
        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            set("Authorization", "Bearer $accessToken")
        }

        val response = rest.exchange(
            url("/api/beautishops/$nonExistentShopId/categories"),
            HttpMethod.PUT,
            HttpEntity(request, headers),
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
    }

    @Test
    fun `should return 403 when owner tries to set categories for another owner's shop`() {
        val owner1Token = signUpOwnerAndGetAccessToken(
            email = "owner1@example.com",
            businessNumber = "123456789",
            phoneNumber = "010-1234-5678",
            nickname = "shop1"
        )
        val shopId = createBeautishop(owner1Token)

        val owner2Token = signUpOwnerAndGetAccessToken(
            email = "owner2@example.com",
            businessNumber = "987654321",
            phoneNumber = "010-9876-5432",
            nickname = "shop2"
        )

        val request = SetShopCategoriesRequest(
            categoryIds = listOf("11111111-1111-1111-1111-111111111111")
        )
        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            set("Authorization", "Bearer $owner2Token")
        }

        val response = rest.exchange(
            url("/api/beautishops/$shopId/categories"),
            HttpMethod.PUT,
            HttpEntity(request, headers),
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.FORBIDDEN)
    }

    @Test
    fun `should return 401 when no token is provided`() {
        val accessToken = signUpOwnerAndGetAccessToken()
        val shopId = createBeautishop(accessToken)

        val request = SetShopCategoriesRequest(
            categoryIds = listOf("11111111-1111-1111-1111-111111111111")
        )
        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
        }

        val response = rest.exchange(
            url("/api/beautishops/$shopId/categories"),
            HttpMethod.PUT,
            HttpEntity(request, headers),
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)
    }
}
