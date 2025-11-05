package com.mad.jellomarkserver.member.core.domain.exception

class DuplicateMemberEmailException(email: String) : RuntimeException("Email already in use: $email")
