package com.mad.jellomarkserver.beautishop.core.domain.exception

class UnauthorizedBeautishopAccessException(val shopId: String) : RuntimeException(
    "Unauthorized access to beautishop: $shopId"
)
