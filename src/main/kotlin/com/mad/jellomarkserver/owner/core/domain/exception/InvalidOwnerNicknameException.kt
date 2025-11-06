package com.mad.jellomarkserver.owner.core.domain.exception

class InvalidOwnerNicknameException(nickname: String) : RuntimeException("Invalid nickname: $nickname")