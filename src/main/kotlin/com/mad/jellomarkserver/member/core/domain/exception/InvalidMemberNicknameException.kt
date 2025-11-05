package com.mad.jellomarkserver.member.core.domain.exception

class InvalidMemberNicknameException(nickname: String) : RuntimeException("Invalid nickname: $nickname")