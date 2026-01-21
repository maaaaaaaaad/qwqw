package com.mad.jellomarkserver.favorite.port.driven

import com.mad.jellomarkserver.beautishop.core.domain.model.ShopId
import com.mad.jellomarkserver.favorite.core.domain.model.Favorite
import com.mad.jellomarkserver.favorite.core.domain.model.FavoriteId
import com.mad.jellomarkserver.member.core.domain.model.MemberId
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface FavoritePort {
    fun save(favorite: Favorite): Favorite
    fun findById(id: FavoriteId): Favorite?
    fun findByMemberIdAndShopId(memberId: MemberId, shopId: ShopId): Favorite?
    fun findByMemberId(memberId: MemberId, pageable: Pageable): Page<Favorite>
    fun existsByMemberIdAndShopId(memberId: MemberId, shopId: ShopId): Boolean
    fun deleteByMemberIdAndShopId(memberId: MemberId, shopId: ShopId)
    fun countByShopId(shopId: ShopId): Int
}
