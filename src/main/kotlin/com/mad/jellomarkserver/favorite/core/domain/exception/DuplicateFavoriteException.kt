package com.mad.jellomarkserver.favorite.core.domain.exception

class DuplicateFavoriteException(shopId: String, memberId: String) : RuntimeException(
    "Member $memberId has already favorited shop $shopId"
)
