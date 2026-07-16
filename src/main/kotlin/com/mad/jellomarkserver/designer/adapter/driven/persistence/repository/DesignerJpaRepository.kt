package com.mad.jellomarkserver.designer.adapter.driven.persistence.repository

import com.mad.jellomarkserver.designer.adapter.driven.persistence.entity.DesignerJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface DesignerJpaRepository : JpaRepository<DesignerJpaEntity, UUID> {
    fun findByShopId(shopId: UUID): List<DesignerJpaEntity>
    fun deleteByShopId(shopId: UUID)
}
