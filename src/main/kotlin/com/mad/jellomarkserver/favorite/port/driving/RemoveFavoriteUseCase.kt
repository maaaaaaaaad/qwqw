package com.mad.jellomarkserver.favorite.port.driving

fun interface RemoveFavoriteUseCase {
    fun execute(command: RemoveFavoriteCommand)
}
