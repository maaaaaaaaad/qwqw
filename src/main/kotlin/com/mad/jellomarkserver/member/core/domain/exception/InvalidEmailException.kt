package com.mad.jellomarkserver.member.core.domain.exception

class InvalidEmailException(email: String) : RuntimeException("Invalid email $email")