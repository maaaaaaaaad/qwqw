package com.mad.jellomarkserver.e2e.review

import com.mad.jellomarkserver.apigateway.adapter.driving.web.request.CreateBeautishopRequest
import com.mad.jellomarkserver.apigateway.adapter.driving.web.request.CreateReviewRequest
import com.mad.jellomarkserver.apigateway.adapter.driving.web.request.LoginWithKakaoRequest
import com.mad.jellomarkserver.apigateway.adapter.driving.web.request.SignUpOwnerRequest
import com.mad.jellomarkserver.auth.adapter.driven.kakao.KakaoTokenInfo
import com.mad.jellomarkserver.auth.adapter.driven.kakao.KakaoUserInfo
import com.mad.jellomarkserver.auth.port.driven.KakaoApiClient
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
class GetMyReviewsE2ETest {

    @LocalServerPort
    private var port: Int = 0

    @Autowired
    lateinit var rest: TestRestTemplate

    @MockitoBean
    lateinit var kakaoApiClient: KakaoApiClient

    private fun url(path: String) = "http://localhost:$port$path"

    private fun signUpOwnerAndGetAccessToken(
        businessNumber: String = "123456789",
        email: String = "owner@example.com"
    ): String {
        val request = SignUpOwnerRequest(
            businessNumber = businessNumber,
            phoneNumber = "010-1234-5678",
            nickname = "testshop",
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

    private fun createBeautishop(ownerAccessToken: String, shopName: String): String {
        val request = CreateBeautishopRequest(
            shopName = shopName,
            shopRegNum = "123-45-${System.nanoTime() % 100000}",
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

    private fun loginWithKakaoAndGetAccessToken(kakaoId: Long, nickname: String): String {
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

    private fun createReview(shopId: String, memberAccessToken: String, rating: Int, content: String) {
        val request = CreateReviewRequest(
            rating = rating,
            content = content,
            images = null
        )

        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            set("Authorization", "Bearer $memberAccessToken")
        }

        rest.exchange(
            url("/api/beautishops/$shopId/reviews"),
            HttpMethod.POST,
            HttpEntity(request, headers),
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )
    }

    @Test
    fun `should return my reviews with shopName included`() {
        val ownerAccessToken = signUpOwnerAndGetAccessToken()
        val shopName = "래쉬바 강남점"
        val shopId = createBeautishop(ownerAccessToken, shopName)

        val memberAccessToken = loginWithKakaoAndGetAccessToken(1234567890123L, "테스트멤버")
        createReview(shopId, memberAccessToken, 5, "정말 훌륭한 서비스였습니다! 다음에 또 방문하겠습니다.")

        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            set("Authorization", "Bearer $memberAccessToken")
        }

        val response = rest.exchange(
            url("/api/reviews/me"),
            HttpMethod.GET,
            HttpEntity<Any>(headers),
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val json = requireNotNull(response.body)

        @Suppress("UNCHECKED_CAST")
        val items = json["items"] as List<Map<String, Any?>>
        assertThat(items).hasSize(1)

        val review = items[0]
        assertThat(review["shopId"]).isEqualTo(shopId)
        assertThat(review["shopName"]).isEqualTo(shopName)
        assertThat(review["rating"]).isEqualTo(5)
    }

    @Test
    fun `should return multiple reviews with correct shopNames`() {
        val owner1Token = signUpOwnerAndGetAccessToken("111111111", "owner1@example.com")
        val owner2Token = signUpOwnerAndGetAccessToken("222222222", "owner2@example.com")

        val shop1Name = "네일아트 홍대점"
        val shop2Name = "뷰티살롱 강남본점"

        val shop1Id = createBeautishop(owner1Token, shop1Name)
        val shop2Id = createBeautishop(owner2Token, shop2Name)

        val memberAccessToken = loginWithKakaoAndGetAccessToken(9876543210123L, "리뷰어")

        createReview(shop1Id, memberAccessToken, 4, "좋은 경험이었습니다. 친절하게 응대해주셨어요.")
        createReview(shop2Id, memberAccessToken, 5, "최고의 서비스였습니다! 강력 추천합니다.")

        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            set("Authorization", "Bearer $memberAccessToken")
        }

        val response = rest.exchange(
            url("/api/reviews/me"),
            HttpMethod.GET,
            HttpEntity<Any>(headers),
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val json = requireNotNull(response.body)

        @Suppress("UNCHECKED_CAST")
        val items = json["items"] as List<Map<String, Any?>>
        assertThat(items).hasSize(2)

        val shopNames = items.map { it["shopName"] as String }.toSet()
        assertThat(shopNames).containsExactlyInAnyOrder(shop1Name, shop2Name)
    }

    @Test
    fun `should return empty list when member has no reviews`() {
        val memberAccessToken = loginWithKakaoAndGetAccessToken(5555555555555L, "신규멤버")

        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            set("Authorization", "Bearer $memberAccessToken")
        }

        val response = rest.exchange(
            url("/api/reviews/me"),
            HttpMethod.GET,
            HttpEntity<Any>(headers),
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
}
