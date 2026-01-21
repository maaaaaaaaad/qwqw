package com.mad.jellomarkserver.favorite.core.domain.exception

class FavoriteNotFoundException(shopId: String, memberId: String) : RuntimeException(
    "Favorite not found for shop $shopId and member $memberId"
)
