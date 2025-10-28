package com.mad.jellomarkserver.owner.adapter.driven.persistence.mapper

import com.mad.jellomarkserver.owner.adapter.driven.persistence.entity.OwnerJpaEntity
import com.mad.jellomarkserver.owner.core.domain.model.Owner

interface OwnerMapper {
    fun toEntity(domain: Owner): OwnerJpaEntity
    fun toDomain(entity: OwnerJpaEntity): Owner
}
