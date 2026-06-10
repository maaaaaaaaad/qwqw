package com.mad.jellomarkserver.auth.adapter.driven.apple

import com.mad.jellomarkserver.auth.core.domain.exception.AppleApiException
import com.mad.jellomarkserver.auth.core.domain.exception.InvalidAppleTokenException
import com.mad.jellomarkserver.auth.port.driven.AppleApiClient
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.jwk.source.JWKSourceBuilder
import com.nimbusds.jose.proc.JWSKeySelector
import com.nimbusds.jose.proc.JWSVerificationKeySelector
import com.nimbusds.jose.proc.SecurityContext
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.proc.DefaultJWTClaimsVerifier
import com.nimbusds.jwt.proc.DefaultJWTProcessor
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.net.URL

@Component
class AppleApiClientImpl(
    @Value("\${apple.client-ids}") private val clientIdsRaw: String
) : AppleApiClient {

    private val acceptedAudiences: Set<String> = clientIdsRaw
        .split(',')
        .map { it.trim() }
        .filter { it.isNotEmpty() }
        .toSet()

    private val jwtProcessor: DefaultJWTProcessor<SecurityContext> by lazy {
        DefaultJWTProcessor<SecurityContext>().apply {
            val jwkSource = JWKSourceBuilder.create<SecurityContext>(URL(JWKS_URL)).build()
            val keySelector: JWSKeySelector<SecurityContext> =
                JWSVerificationKeySelector(JWSAlgorithm.RS256, jwkSource)
            jwsKeySelector = keySelector
            jwtClaimsSetVerifier = DefaultJWTClaimsVerifier(
                acceptedAudiences,
                JWTClaimsSet.Builder().issuer(ISSUER).build(),
                setOf("sub", "iss", "aud", "exp", "iat"),
                emptySet()
            )
        }
    }

    override fun verifyIdentityToken(identityToken: String): AppleUserInfo {
        val claimsSet = try {
            jwtProcessor.process(identityToken, null)
        } catch (e: com.nimbusds.jose.proc.BadJOSEException) {
            throw InvalidAppleTokenException(e.message ?: "Invalid Apple identity token")
        } catch (e: com.nimbusds.jose.JOSEException) {
            throw AppleApiException("Failed to verify Apple identity token: ${e.message}")
        } catch (e: java.text.ParseException) {
            throw InvalidAppleTokenException("Malformed Apple identity token")
        }

        val sub = claimsSet.subject
            ?: throw InvalidAppleTokenException("Apple identity token missing sub claim")
        val email = runCatching { claimsSet.getStringClaim("email") }.getOrNull()

        return AppleUserInfo(sub = sub, email = email)
    }

    companion object {
        private const val ISSUER = "https://appleid.apple.com"
        private const val JWKS_URL = "https://appleid.apple.com/auth/keys"
    }
}
