package com.mad.jellomarkserver.externalshop.adapter.driven.persistence.repository

import com.mad.jellomarkserver.externalshop.adapter.driven.persistence.entity.ExternalShopJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.*

interface ExternalShopJpaRepository : JpaRepository<ExternalShopJpaEntity, UUID> {

    fun findByExternalId(externalId: String): ExternalShopJpaEntity?

    @Query(
        """
        SELECT e FROM ExternalShopJpaEntity e
        WHERE e.latitude BETWEEN :minLat AND :maxLat
        AND e.longitude BETWEEN :minLng AND :maxLng
        """
    )
    fun findInBoundingBox(
        minLat: Double,
        maxLat: Double,
        minLng: Double,
        maxLng: Double
    ): List<ExternalShopJpaEntity>
}
