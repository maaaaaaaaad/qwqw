package com.mad.jellomarkserver.verification.core.domain.exception

class VerificationRateLimitException : RuntimeException("Too many verification code requests. Please try again later")
