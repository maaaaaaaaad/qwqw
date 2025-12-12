package com.mad.jellomarkserver.auth.adapter.driven.jwt

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey

@Component
class JwtTokenProvider(
    private val jwtProperties: JwtProperties
) {
    private val secretKey: SecretKey = Keys.hmacShaKeyFor(jwtProperties.secretKey.toByteArray())

    fun generateAccessToken(email: String, userType: String): String {
        val now = Date()
        val expiryDate = Date(now.time + jwtProperties.accessTokenExpiration)

        return Jwts.builder()
            .subject(email)
            .claim("userType", userType)
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(secretKey)
            .compact()
    }

    fun generateRefreshToken(email: String): String {
        val now = Date()
        val expiryDate = Date(now.time + jwtProperties.refreshTokenExpiration)

        return Jwts.builder()
            .subject(email)
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(secretKey)
            .compact()
    }

    fun validateToken(token: String): Boolean {
        return try {
            Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun getEmailFromToken(token: String): String {
        val claims = getClaims(token)
        return claims.subject
    }

    fun getUserTypeFromToken(token: String): String {
        val claims = getClaims(token)
        return claims.get("userType", String::class.java)
    }

    private fun getClaims(token: String): Claims {
        return Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .payload
    }
}
