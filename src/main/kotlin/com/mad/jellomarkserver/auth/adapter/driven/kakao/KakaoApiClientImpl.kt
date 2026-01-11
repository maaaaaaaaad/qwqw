package com.mad.jellomarkserver.auth.adapter.driven.kakao

import com.mad.jellomarkserver.auth.core.domain.exception.InvalidKakaoTokenException
import com.mad.jellomarkserver.auth.core.domain.exception.KakaoApiException
import com.mad.jellomarkserver.auth.port.driven.KakaoApiClient
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.HttpServerErrorException
import org.springframework.web.client.RestTemplate

@Component
class KakaoApiClientImpl(
    private val restTemplate: RestTemplate
) : KakaoApiClient {

    companion object {
        private const val TOKEN_INFO_URL = "https://kapi.kakao.com/v1/user/access_token_info"
        private const val USER_INFO_URL = "https://kapi.kakao.com/v2/user/me"
    }

    override fun verifyAccessToken(accessToken: String): KakaoTokenInfo {
        val headers = createAuthorizationHeaders(accessToken)
        val entity = HttpEntity<Any>(headers)

        try {
            val response = restTemplate.exchange(
                TOKEN_INFO_URL,
                HttpMethod.GET,
                entity,
                KakaoTokenInfoResponse::class.java
            )
            return response.body?.toKakaoTokenInfo()
                ?: throw KakaoApiException("Empty response from Kakao API")
        } catch (e: HttpClientErrorException.Unauthorized) {
            throw InvalidKakaoTokenException()
        } catch (e: HttpClientErrorException) {
            throw InvalidKakaoTokenException(e.message ?: "Invalid Kakao access token")
        } catch (e: HttpServerErrorException) {
            throw KakaoApiException("Kakao API server error: ${e.statusCode}")
        }
    }

    override fun getUserInfo(accessToken: String): KakaoUserInfo {
        val headers = createAuthorizationHeaders(accessToken)
        val entity = HttpEntity<Any>(headers)

        try {
            val response = restTemplate.exchange(
                USER_INFO_URL,
                HttpMethod.GET,
                entity,
                KakaoUserInfoResponse::class.java
            )
            return response.body?.toKakaoUserInfo()
                ?: throw KakaoApiException("Empty response from Kakao API")
        } catch (e: HttpClientErrorException.Unauthorized) {
            throw InvalidKakaoTokenException()
        } catch (e: HttpClientErrorException) {
            throw InvalidKakaoTokenException(e.message ?: "Invalid Kakao access token")
        } catch (e: HttpServerErrorException) {
            throw KakaoApiException("Kakao API server error: ${e.statusCode}")
        }
    }

    private fun createAuthorizationHeaders(accessToken: String): HttpHeaders {
        return HttpHeaders().apply {
            set("Authorization", "Bearer $accessToken")
        }
    }
}

data class KakaoTokenInfoResponse(
    val id: Long,
    val expires_in: Int,
    val app_id: Int
) {
    fun toKakaoTokenInfo() = KakaoTokenInfo(
        id = id,
        expiresIn = expires_in,
        appId = app_id
    )
}

data class KakaoUserInfoResponse(
    val id: Long,
    val properties: KakaoProperties?
) {
    fun toKakaoUserInfo() = KakaoUserInfo(
        id = id,
        nickname = properties?.nickname ?: ""
    )
}

data class KakaoProperties(
    val nickname: String?
)
