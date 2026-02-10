package com.mad.jellomarkserver.notification.adapter.driven.persistence.mapper

import com.mad.jellomarkserver.notification.adapter.driven.persistence.entity.DeviceTokenJpaEntity
import com.mad.jellomarkserver.notification.core.domain.model.DeviceToken

interface DeviceTokenMapper {
    fun toEntity(domain: DeviceToken): DeviceTokenJpaEntity
    fun toDomain(entity: DeviceTokenJpaEntity): DeviceToken
}
