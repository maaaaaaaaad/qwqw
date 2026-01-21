package com.mad.jellomarkserver.favorite.port.driving

data class CheckFavoriteCommand(
    val memberId: String,
    val shopId: String
)
