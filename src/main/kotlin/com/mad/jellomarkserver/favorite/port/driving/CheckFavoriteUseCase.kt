package com.mad.jellomarkserver.favorite.port.driving

fun interface CheckFavoriteUseCase {
    fun execute(command: CheckFavoriteCommand): Boolean
}
