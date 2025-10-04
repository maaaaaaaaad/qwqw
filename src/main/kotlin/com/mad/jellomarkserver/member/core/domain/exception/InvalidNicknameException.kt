package com.mad.jellomarkserver.member.core.domain.exception

class InvalidNicknameException(nickname: String) : RuntimeException("Invalid nickname: $nickname")