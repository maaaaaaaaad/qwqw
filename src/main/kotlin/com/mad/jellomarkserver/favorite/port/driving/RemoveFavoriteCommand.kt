package com.mad.jellomarkserver.favorite.port.driving

data class RemoveFavoriteCommand(
    val memberId: String,
    val shopId: String
)
