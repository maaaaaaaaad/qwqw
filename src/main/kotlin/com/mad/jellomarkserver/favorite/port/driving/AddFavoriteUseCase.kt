package com.mad.jellomarkserver.favorite.port.driving

import com.mad.jellomarkserver.favorite.core.domain.model.Favorite

fun interface AddFavoriteUseCase {
    fun execute(command: AddFavoriteCommand): Favorite
}
