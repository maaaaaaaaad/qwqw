package com.mad.jellomarkserver.member.core.domain.exception

class DuplicateMemberNicknameException(nickname: String) :
    RuntimeException(
        "Nickname already in use: $nickname"
    ) {
}