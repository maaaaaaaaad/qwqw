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
import org.springframework.test.context.bean.override.mockito.MockitoBean
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
        "classpath:sql/truncate-shop-reviews.sql",
        "classpath:sql/truncate-beautishops.sql",
        "classpath:sql/truncate-members.sql",
        "classpath:sql/truncate-owners.sql",
        "classpath:sql/truncate-auths.sql",
        "classpath:sql/truncate-refresh-tokens.sql"
    ],
    executionPhase = ExecutionPhase.BEFORE_TEST_METHOD
)
class DeleteReviewE2ETest {

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
            shopImages = null
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

    private fun loginWithKakaoAndGetAccessToken(kakaoId: Long = 1234567890123L, nickname: String = "테스트멤버"): String {
        val kakaoAccessToken = "valid_kakao_access_token_$kakaoId"

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

    private fun createReview(shopId: String, memberAccessToken: String): String {
        val request = CreateReviewRequest(
            rating = 5,
            content = "정말 훌륭한 서비스였습니다! 다음에 또 방문하겠습니다.",
            images = listOf("https://example.com/img1.jpg")
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

        val json = requireNotNull(response.body)
        return json["id"] as String
    }

    @Test
    fun `should delete review when owner requests`() {
        val ownerAccessToken = signUpOwnerAndGetAccessToken()
        val shopId = createBeautishop(ownerAccessToken)
        val memberAccessToken = loginWithKakaoAndGetAccessToken()
        val reviewId = createReview(shopId, memberAccessToken)

        assertThat(shopReviewJpaRepository.existsById(UUID.fromString(reviewId))).isTrue()

        val headers = HttpHeaders().apply {
            set("Authorization", "Bearer $memberAccessToken")
        }

        val response = rest.exchange(
            url("/api/beautishops/$shopId/reviews/$reviewId"),
            HttpMethod.DELETE,
            HttpEntity<Unit>(headers),
            object : ParameterizedTypeReference<Unit>() {}
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.NO_CONTENT)
        assertThat(shopReviewJpaRepository.existsById(UUID.fromString(reviewId))).isFalse()
    }

    @Test
    fun `should return 401 when authorization header is missing`() {
        val ownerAccessToken = signUpOwnerAndGetAccessToken()
        val shopId = createBeautishop(ownerAccessToken)
        val memberAccessToken = loginWithKakaoAndGetAccessToken()
        val reviewId = createReview(shopId, memberAccessToken)

        val response = rest.exchange(
            url("/api/beautishops/$shopId/reviews/$reviewId"),
            HttpMethod.DELETE,
            HttpEntity<Unit>(HttpHeaders()),
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)
        assertThat(shopReviewJpaRepository.existsById(UUID.fromString(reviewId))).isTrue()
    }

    @Test
    fun `should return 403 when non-owner tries to delete`() {
        val ownerAccessToken = signUpOwnerAndGetAccessToken()
        val shopId = createBeautishop(ownerAccessToken)
        val memberAccessToken = loginWithKakaoAndGetAccessToken(kakaoId = 1234567890123L, nickname = "원작성자")
        val reviewId = createReview(shopId, memberAccessToken)

        val otherMemberAccessToken = loginWithKakaoAndGetAccessToken(kakaoId = 9876543210987L, nickname = "다른사용자")

        val headers = HttpHeaders().apply {
            set("Authorization", "Bearer $otherMemberAccessToken")
        }

        val response = rest.exchange(
            url("/api/beautishops/$shopId/reviews/$reviewId"),
            HttpMethod.DELETE,
            HttpEntity<Unit>(headers),
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.FORBIDDEN)
        assertThat(shopReviewJpaRepository.existsById(UUID.fromString(reviewId))).isTrue()
    }

    @Test
    fun `should return 404 when review does not exist`() {
        val ownerAccessToken = signUpOwnerAndGetAccessToken()
        val shopId = createBeautishop(ownerAccessToken)
        val memberAccessToken = loginWithKakaoAndGetAccessToken()
        val nonExistentReviewId = UUID.randomUUID().toString()

        val headers = HttpHeaders().apply {
            set("Authorization", "Bearer $memberAccessToken")
        }

        val response = rest.exchange(
            url("/api/beautishops/$shopId/reviews/$nonExistentReviewId"),
            HttpMethod.DELETE,
            HttpEntity<Unit>(headers),
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
    }
}
