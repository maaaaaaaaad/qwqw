package com.mad.jellomarkserver.owner.adapter.driven.persistence.mapper

import com.mad.jellomarkserver.owner.adapter.driven.persistence.entity.OwnerJpaEntity
import com.mad.jellomarkserver.owner.core.domain.model.BusinessNumber
import com.mad.jellomarkserver.owner.core.domain.model.Owner
import com.mad.jellomarkserver.owner.core.domain.model.OwnerId
import com.mad.jellomarkserver.owner.core.domain.model.PhoneNumber
import org.springframework.stereotype.Component

@Component
class OwnerMapperImpl : OwnerMapper {
    override fun toEntity(domain: Owner): OwnerJpaEntity {
        return OwnerJpaEntity(
            id = domain.id.value,
            businessNumber = domain.businessNumber.value,
            phoneNumber = domain.phoneNumber.value,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt
        )
    }

    override fun toDomain(entity: OwnerJpaEntity): Owner {
        val id = OwnerId.from(entity.id)
        val businessNumber = BusinessNumber.of(entity.businessNumber)
        val phoneNumber = PhoneNumber.of(entity.phoneNumber)
        return Owner.reconstruct(
            id = id,
            businessNumber = businessNumber,
            phoneNumber = phoneNumber,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }
}
