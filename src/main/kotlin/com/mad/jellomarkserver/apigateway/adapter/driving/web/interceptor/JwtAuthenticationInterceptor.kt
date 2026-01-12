package com.mad.jellomarkserver.apigateway.adapter.driving.web.interceptor

import com.mad.jellomarkserver.auth.adapter.driven.jwt.JwtTokenProvider
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor

@Component
class JwtAuthenticationInterceptor(
    private val jwtTokenProvider: JwtTokenProvider
) : HandlerInterceptor {

    private val publicGetPatterns = listOf(
        "/api/beautishops",
        "/api/beautishops/[^/]+",
        "/api/beautishops/[^/]+/reviews"
    )

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        if (isPublicGetEndpoint(request)) {
            return true
        }

        val authHeader = request.getHeader("Authorization")

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.status = HttpServletResponse.SC_UNAUTHORIZED
            return false
        }

        val token = authHeader.substring(7)

        if (!jwtTokenProvider.validateToken(token)) {
            response.status = HttpServletResponse.SC_UNAUTHORIZED
            return false
        }

        try {
            val email = jwtTokenProvider.getEmailFromToken(token)
            val userType = jwtTokenProvider.getUserTypeFromToken(token)
            val socialProvider = jwtTokenProvider.getSocialProviderFromToken(token)
            val socialId = jwtTokenProvider.getSocialIdFromToken(token)

            request.setAttribute("email", email)
            request.setAttribute("userType", userType)

            if (socialProvider != null) {
                request.setAttribute("socialProvider", socialProvider)
                request.setAttribute("socialId", socialId)
            }

            return true
        } catch (e: Exception) {
            response.status = HttpServletResponse.SC_UNAUTHORIZED
            return false
        }
    }

    private fun isPublicGetEndpoint(request: HttpServletRequest): Boolean {
        if (request.method != "GET") {
            return false
        }
        val path = request.requestURI
        return publicGetPatterns.any { pattern -> path.matches(Regex(pattern)) }
    }
}
