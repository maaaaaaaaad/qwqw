package com.mad.jellomarkserver.auth.core.domain.exception

class InvalidAuthEmailException(email: String) : RuntimeException("Invalid email $email")
