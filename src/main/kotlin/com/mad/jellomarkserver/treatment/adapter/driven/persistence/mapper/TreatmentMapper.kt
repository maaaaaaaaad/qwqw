package com.mad.jellomarkserver.treatment.adapter.driven.persistence.mapper

import com.mad.jellomarkserver.treatment.adapter.driven.persistence.entity.TreatmentJpaEntity
import com.mad.jellomarkserver.treatment.core.domain.model.Treatment

interface TreatmentMapper {
    fun toEntity(domain: Treatment): TreatmentJpaEntity
    fun toDomain(entity: TreatmentJpaEntity): Treatment
}
