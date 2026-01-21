package com.mad.jellomarkserver.favorite.port.driving

data class AddFavoriteCommand(
    val memberId: String,
    val shopId: String
)
