package com.mad.jellomarkserver.member.core.domain.exception

class MemberNotFoundException(email: String) : RuntimeException("Member not found: $email")
