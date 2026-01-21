package com.mad.jellomarkserver.favorite.port.driving

import com.mad.jellomarkserver.favorite.core.domain.model.Favorite
import org.springframework.data.domain.Page

fun interface GetMemberFavoritesUseCase {
    fun execute(command: GetMemberFavoritesCommand): Page<Favorite>
}
