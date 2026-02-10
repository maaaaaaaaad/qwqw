package com.mad.jellomarkserver.notification.adapter.driven.persistence.mapper

import com.mad.jellomarkserver.notification.adapter.driven.persistence.entity.DeviceTokenJpaEntity
import com.mad.jellomarkserver.notification.core.domain.model.*
import org.springframework.stereotype.Component

@Component
class DeviceTokenMapperImpl : DeviceTokenMapper {

    override fun toEntity(domain: DeviceToken): DeviceTokenJpaEntity {
        return DeviceTokenJpaEntity(
            id = domain.id.value,
            userId = domain.userId,
            userRole = domain.userRole.name,
            token = domain.token,
            platform = domain.platform.name,
            createdAt = domain.createdAt
        )
    }

    override fun toDomain(entity: DeviceTokenJpaEntity): DeviceToken {
        return DeviceToken.reconstruct(
            id = DeviceTokenId.from(entity.id),
            userId = entity.userId,
            userRole = UserRole.valueOf(entity.userRole),
            token = entity.token,
            platform = DevicePlatform.valueOf(entity.platform),
            createdAt = entity.createdAt
        )
    }
}
