package com.mad.jellomarkserver.notification.adapter.driven.persistence.repository

import com.mad.jellomarkserver.notification.adapter.driven.persistence.entity.DeviceTokenJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface DeviceTokenJpaRepository : JpaRepository<DeviceTokenJpaEntity, UUID> {
    fun findByUserIdAndUserRole(userId: UUID, userRole: String): List<DeviceTokenJpaEntity>
    fun findByToken(token: String): DeviceTokenJpaEntity?
    fun deleteByToken(token: String)
}
