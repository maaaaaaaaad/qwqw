package com.mad.jellomarkserver.designer.core.domain.exception

class InvalidDesignerNicknameException(val nickname: String) : RuntimeException(
    "Invalid designer nickname: $nickname"
)
