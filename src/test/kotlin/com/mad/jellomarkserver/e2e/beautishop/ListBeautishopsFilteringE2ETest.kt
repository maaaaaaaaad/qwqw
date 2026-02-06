package com.mad.jellomarkserver.e2e.beautishop

import com.mad.jellomarkserver.apigateway.adapter.driving.web.request.CreateBeautishopRequest
import com.mad.jellomarkserver.apigateway.adapter.driving.web.request.SetShopCategoriesRequest
import com.mad.jellomarkserver.apigateway.adapter.driving.web.request.SignUpOwnerRequest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
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
        "classpath:sql/truncate-refresh-tokens.sql",
        "classpath:sql/truncate-categories.sql",
        "classpath:sql/truncate-shop-category-mappings.sql",
        "classpath:sql/truncate-shop-reviews.sql",
        "classpath:sql/seed-categories.sql"
    ],
    executionPhase = ExecutionPhase.BEFORE_TEST_METHOD
)
class ListBeautishopsFilteringE2ETest {

    @LocalServerPort
    private var port: Int = 0

    @Autowired
    lateinit var rest: TestRestTemplate

    private lateinit var accessToken: String

    private val nailCategoryId = "11111111-1111-1111-1111-111111111111"

    private fun url(path: String) = "http://localhost:$port$path"

    @BeforeEach
    fun setup() {
        accessToken = signUpOwnerAndGetAccessToken()
    }

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

    private fun createBeautishop(
        shopName: String,
        regNum: String,
        latitude: Double = 37.4979,
        longitude: Double = 127.0276
    ): String {
        val request = CreateBeautishopRequest(
            shopName = shopName,
            shopRegNum = regNum,
            shopPhoneNumber = "02-1234-5678",
            shopAddress = "Seoul Gangnam-gu",
            latitude = latitude,
            longitude = longitude,
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

        return response.body?.get("id") as String
    }

    private fun setShopCategories(shopId: String, categoryIds: List<String>) {
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
    }

    @Test
    fun `should filter beautishops by category`() {
        val shopId1 = createBeautishop("Shop A", "111-11-11111")
        val shopId2 = createBeautishop("Shop B", "222-22-22222")
        createBeautishop("Shop C", "333-33-33333")

        setShopCategories(shopId1, listOf(nailCategoryId))
        setShopCategories(shopId2, listOf(nailCategoryId))

        val response = rest.exchange(
            url("/api/beautishops?categoryId=$nailCategoryId"),
            HttpMethod.GET,
            null,
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val json = requireNotNull(response.body)

        @Suppress("UNCHECKED_CAST")
        val items = json["items"] as List<Map<String, Any?>>
        assertThat(items).hasSize(2)
        assertThat(items.map { it["name"] }).containsExactlyInAnyOrder("Shop A", "Shop B")
    }

    @Test
    fun `should filter beautishops by minimum rating`() {
        createBeautishop("Shop A", "111-11-11111")
        createBeautishop("Shop B", "222-22-22222")

        val response = rest.exchange(
            url("/api/beautishops?minRating=4.0"),
            HttpMethod.GET,
            null,
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val json = requireNotNull(response.body)

        @Suppress("UNCHECKED_CAST")
        val items = json["items"] as List<Map<String, Any?>>
        assertThat(items).isEmpty()
    }

    @Test
    fun `should sort beautishops by rating descending`() {
        createBeautishop("Shop A", "111-11-11111")
        createBeautishop("Shop B", "222-22-22222")

        val response = rest.exchange(
            url("/api/beautishops?sortBy=RATING&sortOrder=DESC"),
            HttpMethod.GET,
            null,
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val json = requireNotNull(response.body)

        @Suppress("UNCHECKED_CAST")
        val items = json["items"] as List<Map<String, Any?>>
        assertThat(items).hasSize(2)
    }

    @Test
    fun `should sort beautishops by createdAt ascending`() {
        createBeautishop("Shop A", "111-11-11111")
        Thread.sleep(10)
        createBeautishop("Shop B", "222-22-22222")

        val response = rest.exchange(
            url("/api/beautishops?sortBy=CREATED_AT&sortOrder=ASC"),
            HttpMethod.GET,
            null,
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val json = requireNotNull(response.body)

        @Suppress("UNCHECKED_CAST")
        val items = json["items"] as List<Map<String, Any?>>
        assertThat(items).hasSize(2)
        assertThat(items[0]["name"]).isEqualTo("Shop A")
        assertThat(items[1]["name"]).isEqualTo("Shop B")
    }

    @Test
    fun `should include distance when latitude and longitude provided`() {
        createBeautishop("Shop A", "111-11-11111", 37.5665, 126.9780)

        val response = rest.exchange(
            url("/api/beautishops?latitude=37.5000&longitude=127.0000"),
            HttpMethod.GET,
            null,
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val json = requireNotNull(response.body)

        @Suppress("UNCHECKED_CAST")
        val items = json["items"] as List<Map<String, Any?>>
        assertThat(items).hasSize(1)
        assertThat(items[0]["distance"]).isNotNull()
    }

    @Test
    fun `should sort by distance when sortBy is DISTANCE`() {
        createBeautishop("Shop Far", "111-11-11111", 37.5665, 126.9780)
        createBeautishop("Shop Near", "222-22-22222", 37.5000, 127.0000)

        val response = rest.exchange(
            url("/api/beautishops?sortBy=DISTANCE&sortOrder=ASC&latitude=37.5000&longitude=127.0000"),
            HttpMethod.GET,
            null,
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val json = requireNotNull(response.body)

        @Suppress("UNCHECKED_CAST")
        val items = json["items"] as List<Map<String, Any?>>
        assertThat(items).hasSize(2)
        assertThat(items[0]["name"]).isEqualTo("Shop Near")
        assertThat(items[1]["name"]).isEqualTo("Shop Far")
    }

    @Test
    fun `should not include distance when latitude and longitude not provided`() {
        createBeautishop("Shop A", "111-11-11111")

        val response = rest.exchange(
            url("/api/beautishops"),
            HttpMethod.GET,
            null,
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val json = requireNotNull(response.body)

        @Suppress("UNCHECKED_CAST")
        val items = json["items"] as List<Map<String, Any?>>
        assertThat(items).hasSize(1)
        assertThat(items[0]["distance"]).isNull()
    }

    @Test
    fun `should filter beautishops by keyword in name`() {
        createBeautishop("예쁜네일 강남점", "111-11-11111")
        createBeautishop("러블리네일 홍대점", "222-22-22222")
        createBeautishop("강남 헤어샵", "333-33-33333")

        val response = rest.exchange(
            url("/api/beautishops?keyword=강남"),
            HttpMethod.GET,
            null,
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val json = requireNotNull(response.body)

        @Suppress("UNCHECKED_CAST")
        val items = json["items"] as List<Map<String, Any?>>
        assertThat(items).hasSize(2)
        assertThat(items.map { it["name"] }).containsExactlyInAnyOrder("예쁜네일 강남점", "강남 헤어샵")
    }

    @Test
    fun `should filter beautishops by keyword case insensitively`() {
        createBeautishop("Beautiful Nail", "111-11-11111")
        createBeautishop("Lovely Hair", "222-22-22222")

        val response = rest.exchange(
            url("/api/beautishops?keyword=beautiful"),
            HttpMethod.GET,
            null,
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val json = requireNotNull(response.body)

        @Suppress("UNCHECKED_CAST")
        val items = json["items"] as List<Map<String, Any?>>
        assertThat(items).hasSize(1)
        assertThat(items[0]["name"]).isEqualTo("Beautiful Nail")
    }

    @Test
    fun `should return empty list when no shops match the keyword`() {
        createBeautishop("예쁜네일 강남점", "111-11-11111")

        val response = rest.exchange(
            url("/api/beautishops?keyword=홍대"),
            HttpMethod.GET,
            null,
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val json = requireNotNull(response.body)

        @Suppress("UNCHECKED_CAST")
        val items = json["items"] as List<Map<String, Any?>>
        assertThat(items).isEmpty()
    }

    @Test
    fun `should combine keyword filter with category filter`() {
        val shopId1 = createBeautishop("예쁜네일 강남점", "111-11-11111")
        val shopId2 = createBeautishop("강남 헤어샵", "222-22-22222")
        createBeautishop("예쁜네일 홍대점", "333-33-33333")

        setShopCategories(shopId1, listOf(nailCategoryId))

        val response = rest.exchange(
            url("/api/beautishops?keyword=강남&categoryId=$nailCategoryId"),
            HttpMethod.GET,
            null,
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val json = requireNotNull(response.body)

        @Suppress("UNCHECKED_CAST")
        val items = json["items"] as List<Map<String, Any?>>
        assertThat(items).hasSize(1)
        assertThat(items[0]["name"]).isEqualTo("예쁜네일 강남점")
    }

    @Test
    fun `should filter beautishops by address containing keyword`() {
        createBeautishopWithAddress("예쁜네일샵", "111-11-11111", "서울특별시 강남구 테헤란로 123")
        createBeautishopWithAddress("러블리헤어", "222-22-22222", "서울특별시 홍대입구 234")
        createBeautishopWithAddress("스타일샵", "333-33-33333", "서울특별시 강남구 역삼동 456")

        val response = rest.exchange(
            url("/api/beautishops?keyword=강남"),
            HttpMethod.GET,
            null,
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val json = requireNotNull(response.body)

        @Suppress("UNCHECKED_CAST")
        val items = json["items"] as List<Map<String, Any?>>
        assertThat(items).hasSize(2)
        assertThat(items.map { it["name"] }).containsExactlyInAnyOrder("예쁜네일샵", "스타일샵")
    }

    @Test
    fun `should filter beautishops by name OR address containing keyword`() {
        createBeautishopWithAddress("강남네일", "111-11-11111", "서울특별시 홍대입구 123")
        createBeautishopWithAddress("러블리헤어", "222-22-22222", "서울특별시 강남구 역삼동 234")
        createBeautishopWithAddress("스타일샵", "333-33-33333", "서울특별시 마포구 456")

        val response = rest.exchange(
            url("/api/beautishops?keyword=강남"),
            HttpMethod.GET,
            null,
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val json = requireNotNull(response.body)

        @Suppress("UNCHECKED_CAST")
        val items = json["items"] as List<Map<String, Any?>>
        assertThat(items).hasSize(2)
        assertThat(items.map { it["name"] }).containsExactlyInAnyOrder("강남네일", "러블리헤어")
    }

    private fun createBeautishopWithAddress(
        shopName: String,
        regNum: String,
        address: String,
        latitude: Double = 37.4979,
        longitude: Double = 127.0276
    ): String {
        val request = CreateBeautishopRequest(
            shopName = shopName,
            shopRegNum = regNum,
            shopPhoneNumber = "02-1234-5678",
            shopAddress = address,
            latitude = latitude,
            longitude = longitude,
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

        return response.body?.get("id") as String
    }
}
