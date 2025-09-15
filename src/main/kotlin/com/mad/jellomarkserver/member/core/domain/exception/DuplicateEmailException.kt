package com.mad.jellomarkserver.member.core.domain.exception

class DuplicateEmailException(email: String) : RuntimeException("Email already in use: $email")
