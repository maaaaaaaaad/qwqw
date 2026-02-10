package com.mad.jellomarkserver.beautishop.port.driven

import com.mad.jellomarkserver.beautishop.core.domain.model.Beautishop
import com.mad.jellomarkserver.beautishop.core.domain.model.ShopId
import com.mad.jellomarkserver.beautishop.core.domain.model.ShopRegNum
import com.mad.jellomarkserver.owner.core.domain.model.OwnerId
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface BeautishopPort {
    fun save(beautishop: Beautishop, ownerId: OwnerId): Beautishop
    fun findById(id: ShopId): Beautishop?
    fun findByIds(ids: List<ShopId>): List<Beautishop>
    fun findByOwnerId(ownerId: OwnerId): List<Beautishop>
    fun findByShopRegNum(shopRegNum: ShopRegNum): Beautishop?
    fun findAllPaged(pageable: Pageable): Page<Beautishop>
    fun findAllFiltered(criteria: BeautishopFilterCriteria, pageable: Pageable): Page<Beautishop>
    fun findAllFilteredWithoutPaging(criteria: BeautishopFilterCriteria): List<Beautishop>
    fun findOwnerIdByShopId(shopId: ShopId): OwnerId?
    fun delete(id: ShopId)
    fun updateStats(id: ShopId, averageRating: Double, reviewCount: Int)
}
