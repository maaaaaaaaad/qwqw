package com.mad.jellomarkserver.beautishop.adapter.driven.persistence.mapper

import com.mad.jellomarkserver.beautishop.adapter.driven.persistence.entity.BeautishopJpaEntity
import com.mad.jellomarkserver.beautishop.core.domain.model.Beautishop
import com.mad.jellomarkserver.owner.core.domain.model.OwnerId

interface BeautishopMapper {
    fun toEntity(domain: Beautishop, ownerId: OwnerId): BeautishopJpaEntity
    fun toDomain(entity: BeautishopJpaEntity): Beautishop
}
