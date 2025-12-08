package com.mad.jellomarkserver.auth.core.domain.exception

class AuthenticationFailedException(email: String) : RuntimeException("Authentication failed for email $email")
