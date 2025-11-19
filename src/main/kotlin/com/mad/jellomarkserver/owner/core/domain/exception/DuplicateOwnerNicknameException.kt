package com.mad.jellomarkserver.owner.core.domain.exception

class DuplicateOwnerNicknameException(nickname: String) :
    RuntimeException(
        "Nickname already in use: $nickname"
    )
