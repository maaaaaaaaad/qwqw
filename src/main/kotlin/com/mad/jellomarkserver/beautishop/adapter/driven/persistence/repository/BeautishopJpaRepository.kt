package com.mad.jellomarkserver.beautishop.adapter.driven.persistence.repository

import com.mad.jellomarkserver.beautishop.adapter.driven.persistence.entity.BeautishopJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface BeautishopJpaRepository : JpaRepository<BeautishopJpaEntity, UUID> {
    fun findByOwnerId(ownerId: UUID): List<BeautishopJpaEntity>
    fun findByShopRegNum(shopRegNum: String): BeautishopJpaEntity?
}
