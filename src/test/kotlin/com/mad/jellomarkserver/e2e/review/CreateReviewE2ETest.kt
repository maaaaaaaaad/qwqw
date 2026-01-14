package com.mad.jellomarkserver.e2e.review

import com.mad.jellomarkserver.apigateway.adapter.driving.web.request.CreateBeautishopRequest
import com.mad.jellomarkserver.apigateway.adapter.driving.web.request.CreateReviewRequest
import com.mad.jellomarkserver.apigateway.adapter.driving.web.request.LoginWithKakaoRequest
import com.mad.jellomarkserver.apigateway.adapter.driving.web.request.SignUpOwnerRequest
import com.mad.jellomarkserver.auth.adapter.driven.kakao.KakaoTokenInfo
import com.mad.jellomarkserver.auth.adapter.driven.kakao.KakaoUserInfo
import com.mad.jellomarkserver.auth.port.driven.KakaoApiClient
import com.mad.jellomarkserver.review.adapter.driven.persistence.repository.ShopReviewJpaRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.*
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.jdbc.Sql.ExecutionPhase

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(
    scripts = [
        "classpath:sql/truncate-shop-reviews.sql",
        "classpath:sql/truncate-beautishops.sql",
        "classpath:sql/truncate-members.sql",
        "classpath:sql/truncate-owners.sql",
        "classpath:sql/truncate-auths.sql",
        "classpath:sql/truncate-refresh-tokens.sql"
    ],
    executionPhase = ExecutionPhase.BEFORE_TEST_METHOD
)
class CreateReviewE2ETest {

    @LocalServerPort
    private var port: Int = 0

    @Autowired
    lateinit var rest: TestRestTemplate

    @Autowired
    lateinit var shopReviewJpaRepository: ShopReviewJpaRepository

    @MockitoBean
    lateinit var kakaoApiClient: KakaoApiClient

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

    private fun createBeautishop(ownerAccessToken: String): String {
        val request = CreateBeautishopRequest(
            shopName = "Test Beauty Salon",
            shopRegNum = "123-45-67890",
            shopPhoneNumber = "02-1234-5678",
            shopAddress = "Seoul Gangnam-gu",
            latitude = 37.4979,
            longitude = 127.0276,
            operatingTime = mapOf("Monday" to "09:00-18:00"),
            shopDescription = "Test beauty salon",
            shopImage = null
        )

        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            set("Authorization", "Bearer $ownerAccessToken")
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

    private fun loginWithKakaoAndGetAccessToken(): String {
        val kakaoAccessToken = "valid_kakao_access_token"
        val kakaoId = 1234567890123L
        val nickname = "테스트멤버"

        val tokenInfo = KakaoTokenInfo(id = kakaoId, expiresIn = 3600, appId = 123456)
        val userInfo = KakaoUserInfo(id = kakaoId, nickname = nickname)

        whenever(kakaoApiClient.verifyAccessToken(kakaoAccessToken)).thenReturn(tokenInfo)
        whenever(kakaoApiClient.getUserInfo(kakaoAccessToken)).thenReturn(userInfo)

        val request = LoginWithKakaoRequest(kakaoAccessToken = kakaoAccessToken)
        val headers = HttpHeaders().apply { contentType = MediaType.APPLICATION_JSON }

        val response = rest.exchange(
            url("/api/auth/kakao"),
            HttpMethod.POST,
            HttpEntity(request, headers),
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )

        val json = requireNotNull(response.body)
        return json["accessToken"] as String
    }

    @Test
    fun `should create review when member provides valid data`() {
        val ownerAccessToken = signUpOwnerAndGetAccessToken()
        val shopId = createBeautishop(ownerAccessToken)
        val memberAccessToken = loginWithKakaoAndGetAccessToken()

        val request = CreateReviewRequest(
            rating = 5,
            content = "정말 훌륭한 서비스였습니다! 다음에 또 방문하겠습니다.",
            images = listOf("https://example.com/img1.jpg", "https://example.com/img2.jpg")
        )

        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            set("Authorization", "Bearer $memberAccessToken")
        }

        val response = rest.exchange(
            url("/api/beautishops/$shopId/reviews"),
            HttpMethod.POST,
            HttpEntity(request, headers),
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.CREATED)
        val json = requireNotNull(response.body)
        assertThat(json["id"]).isNotNull()
        assertThat(json["shopId"]).isEqualTo(shopId)
        assertThat(json["rating"]).isEqualTo(5)
        assertThat(json["content"]).isEqualTo("정말 훌륭한 서비스였습니다! 다음에 또 방문하겠습니다.")
        assertThat(json["images"]).isEqualTo(listOf("https://example.com/img1.jpg", "https://example.com/img2.jpg"))
        assertThat(json["createdAt"]).isNotNull()
        assertThat(json["updatedAt"]).isNotNull()

        val saved = shopReviewJpaRepository.findAll()
        assertThat(saved).hasSize(1)
        assertThat(saved[0].rating).isEqualTo(5)
    }

    @Test
    fun `should create review without images`() {
        val ownerAccessToken = signUpOwnerAndGetAccessToken()
        val shopId = createBeautishop(ownerAccessToken)
        val memberAccessToken = loginWithKakaoAndGetAccessToken()

        val request = CreateReviewRequest(
            rating = 4,
            content = "좋은 경험이었습니다. 친절하게 응대해주셨어요.",
            images = null
        )

        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            set("Authorization", "Bearer $memberAccessToken")
        }

        val response = rest.exchange(
            url("/api/beautishops/$shopId/reviews"),
            HttpMethod.POST,
            HttpEntity(request, headers),
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.CREATED)
        val json = requireNotNull(response.body)
        assertThat(json["images"]).isNull()
    }

    @Test
    fun `should return 401 when authorization header is missing`() {
        val ownerAccessToken = signUpOwnerAndGetAccessToken()
        val shopId = createBeautishop(ownerAccessToken)

        val request = CreateReviewRequest(
            rating = 5,
            content = "정말 훌륭한 서비스였습니다! 다음에 또 방문하겠습니다.",
            images = null
        )

        val headers = HttpHeaders().apply { contentType = MediaType.APPLICATION_JSON }

        val response = rest.exchange(
            url("/api/beautishops/$shopId/reviews"),
            HttpMethod.POST,
            HttpEntity(request, headers),
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)
    }

    @Test
    fun `should return 409 when member already reviewed the shop`() {
        val ownerAccessToken = signUpOwnerAndGetAccessToken()
        val shopId = createBeautishop(ownerAccessToken)
        val memberAccessToken = loginWithKakaoAndGetAccessToken()

        val request = CreateReviewRequest(
            rating = 5,
            content = "정말 훌륭한 서비스였습니다! 다음에 또 방문하겠습니다.",
            images = null
        )

        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            set("Authorization", "Bearer $memberAccessToken")
        }

        val firstResponse = rest.exchange(
            url("/api/beautishops/$shopId/reviews"),
            HttpMethod.POST,
            HttpEntity(request, headers),
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )
        assertThat(firstResponse.statusCode).isEqualTo(HttpStatus.CREATED)

        val secondResponse = rest.exchange(
            url("/api/beautishops/$shopId/reviews"),
            HttpMethod.POST,
            HttpEntity(request, headers),
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )
        assertThat(secondResponse.statusCode).isEqualTo(HttpStatus.CONFLICT)
    }

    @Test
    fun `should return 422 when rating is invalid`() {
        val ownerAccessToken = signUpOwnerAndGetAccessToken()
        val shopId = createBeautishop(ownerAccessToken)
        val memberAccessToken = loginWithKakaoAndGetAccessToken()

        val request = CreateReviewRequest(
            rating = 6,
            content = "정말 훌륭한 서비스였습니다! 다음에 또 방문하겠습니다.",
            images = null
        )

        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            set("Authorization", "Bearer $memberAccessToken")
        }

        val response = rest.exchange(
            url("/api/beautishops/$shopId/reviews"),
            HttpMethod.POST,
            HttpEntity(request, headers),
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
    }

    @Test
    fun `should return 422 when content is too short`() {
        val ownerAccessToken = signUpOwnerAndGetAccessToken()
        val shopId = createBeautishop(ownerAccessToken)
        val memberAccessToken = loginWithKakaoAndGetAccessToken()

        val request = CreateReviewRequest(
            rating = 5,
            content = "좋아요",
            images = null
        )

        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            set("Authorization", "Bearer $memberAccessToken")
        }

        val response = rest.exchange(
            url("/api/beautishops/$shopId/reviews"),
            HttpMethod.POST,
            HttpEntity(request, headers),
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
    }

    @Test
    fun `should create review with rating only when content is null`() {
        val ownerAccessToken = signUpOwnerAndGetAccessToken()
        val shopId = createBeautishop(ownerAccessToken)
        val memberAccessToken = loginWithKakaoAndGetAccessToken()

        val requestBody = mapOf(
            "rating" to 5,
            "content" to null,
            "images" to null
        )

        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            set("Authorization", "Bearer $memberAccessToken")
        }

        val response = rest.exchange(
            url("/api/beautishops/$shopId/reviews"),
            HttpMethod.POST,
            HttpEntity(requestBody, headers),
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.CREATED)
        val json = requireNotNull(response.body)
        assertThat(json["rating"]).isEqualTo(5)
        assertThat(json["content"]).isNull()

        val saved = shopReviewJpaRepository.findAll()
        assertThat(saved).hasSize(1)
        assertThat(saved[0].rating).isEqualTo(5)
        assertThat(saved[0].content).isNull()
    }

    @Test
    fun `should create review with content only when rating is null`() {
        val ownerAccessToken = signUpOwnerAndGetAccessToken()
        val shopId = createBeautishop(ownerAccessToken)
        val memberAccessToken = loginWithKakaoAndGetAccessToken()

        val requestBody = mapOf(
            "rating" to null,
            "content" to "리뷰 내용입니다. 서비스가 좋았습니다.",
            "images" to null
        )

        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            set("Authorization", "Bearer $memberAccessToken")
        }

        val response = rest.exchange(
            url("/api/beautishops/$shopId/reviews"),
            HttpMethod.POST,
            HttpEntity(requestBody, headers),
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.CREATED)
        val json = requireNotNull(response.body)
        assertThat(json["rating"]).isNull()
        assertThat(json["content"]).isEqualTo("리뷰 내용입니다. 서비스가 좋았습니다.")

        val saved = shopReviewJpaRepository.findAll()
        assertThat(saved).hasSize(1)
        assertThat(saved[0].rating).isNull()
        assertThat(saved[0].content).isEqualTo("리뷰 내용입니다. 서비스가 좋았습니다.")
    }

    @Test
    fun `should return 422 when both rating and content are null`() {
        val ownerAccessToken = signUpOwnerAndGetAccessToken()
        val shopId = createBeautishop(ownerAccessToken)
        val memberAccessToken = loginWithKakaoAndGetAccessToken()

        val requestBody = mapOf(
            "rating" to null,
            "content" to null,
            "images" to null
        )

        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            set("Authorization", "Bearer $memberAccessToken")
        }

        val response = rest.exchange(
            url("/api/beautishops/$shopId/reviews"),
            HttpMethod.POST,
            HttpEntity(requestBody, headers),
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
    }
}
