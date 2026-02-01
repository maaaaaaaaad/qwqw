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
class ListReviewsE2ETest {

    @LocalServerPort
    private var port: Int = 0

    @Autowired
    lateinit var rest: TestRestTemplate

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
    fun `should list reviews for a shop without authentication`() {
        val ownerAccessToken = signUpOwnerAndGetAccessToken()
        val shopId = createBeautishop(ownerAccessToken)

        val member1Token = loginWithKakaoAndGetAccessToken(1234567890123L, "멤버1")
        val member2Token = loginWithKakaoAndGetAccessToken(9876543210123L, "멤버2")

        createReview(shopId, member1Token, 5, "정말 훌륭한 서비스였습니다! 다음에 또 방문하겠습니다.")
        createReview(shopId, member2Token, 4, "좋은 경험이었습니다. 친절하게 응대해주셨어요.")

        val headers = HttpHeaders().apply { contentType = MediaType.APPLICATION_JSON }

        val response = rest.exchange(
            url("/api/beautishops/$shopId/reviews"),
            HttpMethod.GET,
            HttpEntity<Any>(headers),
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

    @Test
    fun `should return empty list when no reviews exist`() {
        val ownerAccessToken = signUpOwnerAndGetAccessToken()
        val shopId = createBeautishop(ownerAccessToken)

        val headers = HttpHeaders().apply { contentType = MediaType.APPLICATION_JSON }

        val response = rest.exchange(
            url("/api/beautishops/$shopId/reviews"),
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

    @Test
    fun `should paginate reviews correctly`() {
        val ownerAccessToken = signUpOwnerAndGetAccessToken()
        val shopId = createBeautishop(ownerAccessToken)

        val member1Token = loginWithKakaoAndGetAccessToken(1111111111111L, "멤버1")
        val member2Token = loginWithKakaoAndGetAccessToken(2222222222222L, "멤버2")
        val member3Token = loginWithKakaoAndGetAccessToken(3333333333333L, "멤버3")

        createReview(shopId, member1Token, 5, "정말 훌륭한 서비스였습니다! 다음에 또 방문하겠습니다.")
        createReview(shopId, member2Token, 4, "좋은 경험이었습니다. 친절하게 응대해주셨어요.")
        createReview(shopId, member3Token, 3, "보통이었습니다. 조금 더 개선이 필요해요.")

        val headers = HttpHeaders().apply { contentType = MediaType.APPLICATION_JSON }

        val response = rest.exchange(
            url("/api/beautishops/$shopId/reviews?page=0&size=2"),
            HttpMethod.GET,
            HttpEntity<Any>(headers),
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
    fun `should sort reviews by rating descending`() {
        val ownerAccessToken = signUpOwnerAndGetAccessToken()
        val shopId = createBeautishop(ownerAccessToken)

        val member1Token = loginWithKakaoAndGetAccessToken(4444444444444L, "멤버1")
        val member2Token = loginWithKakaoAndGetAccessToken(5555555555555L, "멤버2")
        val member3Token = loginWithKakaoAndGetAccessToken(6666666666666L, "멤버3")

        createReview(shopId, member1Token, 3, "보통이었습니다. 조금 더 개선이 필요해요.")
        createReview(shopId, member2Token, 5, "정말 훌륭한 서비스였습니다! 다음에 또 방문하겠습니다.")
        createReview(shopId, member3Token, 4, "좋은 경험이었습니다. 친절하게 응대해주셨어요.")

        val headers = HttpHeaders().apply { contentType = MediaType.APPLICATION_JSON }

        val response = rest.exchange(
            url("/api/beautishops/$shopId/reviews?sort=rating,desc"),
            HttpMethod.GET,
            HttpEntity<Any>(headers),
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val json = requireNotNull(response.body)

        @Suppress("UNCHECKED_CAST")
        val items = json["items"] as List<Map<String, Any?>>
        assertThat(items).hasSize(3)

        val ratings = items.map { it["rating"] as Int }
        assertThat(ratings).containsExactly(5, 4, 3)
    }

    @Test
    fun `should sort reviews by rating ascending`() {
        val ownerAccessToken = signUpOwnerAndGetAccessToken()
        val shopId = createBeautishop(ownerAccessToken)

        val member1Token = loginWithKakaoAndGetAccessToken(7777777777777L, "멤버1")
        val member2Token = loginWithKakaoAndGetAccessToken(8888888888888L, "멤버2")
        val member3Token = loginWithKakaoAndGetAccessToken(9999999999999L, "멤버3")

        createReview(shopId, member1Token, 3, "보통이었습니다. 조금 더 개선이 필요해요.")
        createReview(shopId, member2Token, 5, "정말 훌륭한 서비스였습니다! 다음에 또 방문하겠습니다.")
        createReview(shopId, member3Token, 4, "좋은 경험이었습니다. 친절하게 응대해주셨어요.")

        val headers = HttpHeaders().apply { contentType = MediaType.APPLICATION_JSON }

        val response = rest.exchange(
            url("/api/beautishops/$shopId/reviews?sort=rating,asc"),
            HttpMethod.GET,
            HttpEntity<Any>(headers),
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val json = requireNotNull(response.body)

        @Suppress("UNCHECKED_CAST")
        val items = json["items"] as List<Map<String, Any?>>
        assertThat(items).hasSize(3)

        val ratings = items.map { it["rating"] as Int }
        assertThat(ratings).containsExactly(3, 4, 5)
    }

    @Test
    fun `should sort reviews by createdAt descending as default`() {
        val ownerAccessToken = signUpOwnerAndGetAccessToken()
        val shopId = createBeautishop(ownerAccessToken)

        val member1Token = loginWithKakaoAndGetAccessToken(1010101010101L, "멤버1")
        val member2Token = loginWithKakaoAndGetAccessToken(2020202020202L, "멤버2")
        val member3Token = loginWithKakaoAndGetAccessToken(3030303030303L, "멤버3")

        createReview(shopId, member1Token, 3, "첫 번째 리뷰입니다. 보통이었습니다.")
        Thread.sleep(50)
        createReview(shopId, member2Token, 5, "두 번째 리뷰입니다. 훌륭합니다!")
        Thread.sleep(50)
        createReview(shopId, member3Token, 4, "세 번째 리뷰입니다. 좋았습니다.")

        val headers = HttpHeaders().apply { contentType = MediaType.APPLICATION_JSON }

        val response = rest.exchange(
            url("/api/beautishops/$shopId/reviews?sort=createdAt,desc"),
            HttpMethod.GET,
            HttpEntity<Any>(headers),
            object : ParameterizedTypeReference<Map<String, Any?>>() {}
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val json = requireNotNull(response.body)

        @Suppress("UNCHECKED_CAST")
        val items = json["items"] as List<Map<String, Any?>>
        assertThat(items).hasSize(3)

        val ratings = items.map { it["rating"] as Int }
        assertThat(ratings).containsExactly(4, 5, 3)
    }
}
