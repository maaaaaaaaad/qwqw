package com.mad.jellomarkserver.favorite.adapter.driven.persistence.mapper

import com.mad.jellomarkserver.favorite.adapter.driven.persistence.entity.FavoriteJpaEntity
import com.mad.jellomarkserver.favorite.core.domain.model.Favorite

interface FavoriteMapper {
    fun toEntity(domain: Favorite): FavoriteJpaEntity
    fun toDomain(entity: FavoriteJpaEntity): Favorite
}
