package com.mad.jellomarkserver.member.core.domain.exception

class InvalidMemberEmailException(email: String) : RuntimeException("Invalid email $email")