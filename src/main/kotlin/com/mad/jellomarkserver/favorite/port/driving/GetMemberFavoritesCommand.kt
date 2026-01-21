package com.mad.jellomarkserver.favorite.port.driving

data class GetMemberFavoritesCommand(
    val memberId: String,
    val page: Int,
    val size: Int
)
