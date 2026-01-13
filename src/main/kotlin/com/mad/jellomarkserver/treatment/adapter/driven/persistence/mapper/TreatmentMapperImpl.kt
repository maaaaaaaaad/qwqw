package com.mad.jellomarkserver.treatment.adapter.driven.persistence.mapper

import com.mad.jellomarkserver.beautishop.core.domain.model.ShopId
import com.mad.jellomarkserver.treatment.adapter.driven.persistence.entity.TreatmentJpaEntity
import com.mad.jellomarkserver.treatment.core.domain.model.*
import org.springframework.stereotype.Component

@Component
class TreatmentMapperImpl : TreatmentMapper {
    override fun toEntity(domain: Treatment): TreatmentJpaEntity {
        return TreatmentJpaEntity(
            id = domain.id.value,
            shopId = domain.shopId.value,
            name = domain.name.value,
            price = domain.price.value,
            duration = domain.duration.value,
            description = domain.description?.value,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt
        )
    }

    override fun toDomain(entity: TreatmentJpaEntity): Treatment {
        val id = TreatmentId.from(entity.id)
        val shopId = ShopId.from(entity.shopId)
        val name = TreatmentName.of(entity.name)
        val price = TreatmentPrice.of(entity.price)
        val duration = TreatmentDuration.of(entity.duration)
        val description = entity.description?.let { TreatmentDescription.of(it) }

        return Treatment.reconstruct(
            id = id,
            shopId = shopId,
            name = name,
            price = price,
            duration = duration,
            description = description,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }
}
