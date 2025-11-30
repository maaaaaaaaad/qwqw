package com.mad.jellomarkserver.auth.core.domain.exception

class DuplicateAuthEmailException(email: String) : RuntimeException("Email $email already exists")
