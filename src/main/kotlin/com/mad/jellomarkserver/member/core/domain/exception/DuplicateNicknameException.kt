package com.mad.jellomarkserver.member.core.domain.exception

class DuplicateNicknameException(nickname: String) :
    RuntimeException(
        "Nickname already in use: $nickname"
    ) {
}