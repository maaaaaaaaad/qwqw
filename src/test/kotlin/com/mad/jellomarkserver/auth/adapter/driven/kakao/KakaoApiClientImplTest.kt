package com.mad.jellomarkserver.auth.adapter.driven.kakao

import com.mad.jellomarkserver.auth.core.domain.exception.InvalidKakaoTokenException
import com.mad.jellomarkserver.auth.core.domain.exception.KakaoApiException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers.*
import org.springframework.test.web.client.response.MockRestResponseCreators.withStatus
import org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess
import org.springframework.web.client.RestTemplate

class KakaoApiClientImplTest {

    private lateinit var restTemplate: RestTemplate
    private lateinit var mockServer: MockRestServiceServer
    private lateinit var kakaoApiClient: KakaoApiClientImpl

    @BeforeEach
    fun setUp() {
        restTemplate = RestTemplate()
        mockServer = MockRestServiceServer.createServer(restTemplate)
        kakaoApiClient = KakaoApiClientImpl(restTemplate)
    }

    @Test
    fun `should verify access token successfully`() {
        val accessToken = "valid_access_token"
        val responseJson = """
            {
                "id": 123456789,
                "expires_in": 3600,
                "app_id": 987654
            }
        """.trimIndent()

        mockServer.expect(requestTo("https://kapi.kakao.com/v1/user/access_token_info"))
            .andExpect(method(HttpMethod.GET))
            .andExpect(header("Authorization", "Bearer $accessToken"))
            .andRespond(withSuccess(responseJson, MediaType.APPLICATION_JSON))

        val result = kakaoApiClient.verifyAccessToken(accessToken)

        assertThat(result.id).isEqualTo(123456789L)
        assertThat(result.expiresIn).isEqualTo(3600)
        assertThat(result.appId).isEqualTo(987654)
        mockServer.verify()
    }

    @Test
    fun `should throw InvalidKakaoTokenException when access token is invalid`() {
        val accessToken = "invalid_token"
        val errorJson = """
            {
                "code": -401,
                "msg": "this access token does not exist"
            }
        """.trimIndent()

        mockServer.expect(requestTo("https://kapi.kakao.com/v1/user/access_token_info"))
            .andExpect(method(HttpMethod.GET))
            .andExpect(header("Authorization", "Bearer $accessToken"))
            .andRespond(withStatus(HttpStatus.UNAUTHORIZED).body(errorJson).contentType(MediaType.APPLICATION_JSON))

        assertThatThrownBy { kakaoApiClient.verifyAccessToken(accessToken) }
            .isInstanceOf(InvalidKakaoTokenException::class.java)

        mockServer.verify()
    }

    @Test
    fun `should throw KakaoApiException when Kakao API returns server error`() {
        val accessToken = "valid_token"

        mockServer.expect(requestTo("https://kapi.kakao.com/v1/user/access_token_info"))
            .andExpect(method(HttpMethod.GET))
            .andExpect(header("Authorization", "Bearer $accessToken"))
            .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR))

        assertThatThrownBy { kakaoApiClient.verifyAccessToken(accessToken) }
            .isInstanceOf(KakaoApiException::class.java)

        mockServer.verify()
    }

    @Test
    fun `should get user info successfully`() {
        val accessToken = "valid_access_token"
        val responseJson = """
            {
                "id": 3456789012345,
                "properties": {
                    "nickname": "테스트유저"
                }
            }
        """.trimIndent()

        mockServer.expect(requestTo("https://kapi.kakao.com/v2/user/me"))
            .andExpect(method(HttpMethod.GET))
            .andExpect(header("Authorization", "Bearer $accessToken"))
            .andRespond(withSuccess(responseJson, MediaType.APPLICATION_JSON))

        val result = kakaoApiClient.getUserInfo(accessToken)

        assertThat(result.id).isEqualTo(3456789012345L)
        assertThat(result.nickname).isEqualTo("테스트유저")
        mockServer.verify()
    }

    @Test
    fun `should throw InvalidKakaoTokenException when getting user info with invalid token`() {
        val accessToken = "invalid_token"
        val errorJson = """
            {
                "code": -401,
                "msg": "this access token does not exist"
            }
        """.trimIndent()

        mockServer.expect(requestTo("https://kapi.kakao.com/v2/user/me"))
            .andExpect(method(HttpMethod.GET))
            .andExpect(header("Authorization", "Bearer $accessToken"))
            .andRespond(withStatus(HttpStatus.UNAUTHORIZED).body(errorJson).contentType(MediaType.APPLICATION_JSON))

        assertThatThrownBy { kakaoApiClient.getUserInfo(accessToken) }
            .isInstanceOf(InvalidKakaoTokenException::class.java)

        mockServer.verify()
    }

    @Test
    fun `should throw KakaoApiException when getting user info fails with server error`() {
        val accessToken = "valid_token"

        mockServer.expect(requestTo("https://kapi.kakao.com/v2/user/me"))
            .andExpect(method(HttpMethod.GET))
            .andExpect(header("Authorization", "Bearer $accessToken"))
            .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR))

        assertThatThrownBy { kakaoApiClient.getUserInfo(accessToken) }
            .isInstanceOf(KakaoApiException::class.java)

        mockServer.verify()
    }
}
