package com.mad.jellomarkserver.category.core.domain.exception

class UnauthorizedShopAccessException(val shopId: String) : RuntimeException(
    "Unauthorized access to shop: $shopId"
)
