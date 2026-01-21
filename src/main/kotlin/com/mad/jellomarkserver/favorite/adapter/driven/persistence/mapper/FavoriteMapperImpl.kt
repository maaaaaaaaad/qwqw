package com.mad.jellomarkserver.favorite.adapter.driven.persistence.mapper

import com.mad.jellomarkserver.beautishop.core.domain.model.ShopId
import com.mad.jellomarkserver.favorite.adapter.driven.persistence.entity.FavoriteJpaEntity
import com.mad.jellomarkserver.favorite.core.domain.model.Favorite
import com.mad.jellomarkserver.favorite.core.domain.model.FavoriteId
import com.mad.jellomarkserver.member.core.domain.model.MemberId
import org.springframework.stereotype.Component

@Component
class FavoriteMapperImpl : FavoriteMapper {

    override fun toEntity(domain: Favorite): FavoriteJpaEntity {
        return FavoriteJpaEntity(
            id = domain.id.value,
            memberId = domain.memberId.value,
            shopId = domain.shopId.value,
            createdAt = domain.createdAt
        )
    }

    override fun toDomain(entity: FavoriteJpaEntity): Favorite {
        return Favorite.reconstruct(
            id = FavoriteId.from(entity.id),
            memberId = MemberId.from(entity.memberId),
            shopId = ShopId.from(entity.shopId),
            createdAt = entity.createdAt
        )
    }
}
