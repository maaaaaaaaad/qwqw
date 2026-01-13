package com.mad.jellomarkserver.treatment.adapter.driven.persistence.repository

import com.mad.jellomarkserver.treatment.adapter.driven.persistence.entity.TreatmentJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface TreatmentJpaRepository : JpaRepository<TreatmentJpaEntity, UUID> {
    fun findByShopId(shopId: UUID): List<TreatmentJpaEntity>
}
