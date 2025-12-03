package com.mad.jellomarkserver.auth.core.domain.exception

class DuplicateAuthEmailException(email: String) : RuntimeException("Email already in use: $email")
