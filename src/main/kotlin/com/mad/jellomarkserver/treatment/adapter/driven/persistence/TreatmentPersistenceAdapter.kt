package com.mad.jellomarkserver.treatment.adapter.driven.persistence

import com.mad.jellomarkserver.beautishop.core.domain.model.ShopId
import com.mad.jellomarkserver.treatment.adapter.driven.persistence.mapper.TreatmentMapper
import com.mad.jellomarkserver.treatment.adapter.driven.persistence.repository.TreatmentJpaRepository
import com.mad.jellomarkserver.treatment.core.domain.model.Treatment
import com.mad.jellomarkserver.treatment.core.domain.model.TreatmentId
import com.mad.jellomarkserver.treatment.port.driven.TreatmentPort
import org.springframework.stereotype.Component

@Component
class TreatmentPersistenceAdapter(
    private val repository: TreatmentJpaRepository,
    private val mapper: TreatmentMapper
) : TreatmentPort {

    override fun save(treatment: Treatment): Treatment {
        val entity = mapper.toEntity(treatment)
        val savedEntity = repository.save(entity)
        return mapper.toDomain(savedEntity)
    }

    override fun findById(id: TreatmentId): Treatment? {
        return repository.findById(id.value)
            .map { mapper.toDomain(it) }
            .orElse(null)
    }

    override fun findByShopId(shopId: ShopId): List<Treatment> {
        return repository.findByShopId(shopId.value)
            .map { mapper.toDomain(it) }
    }

    override fun delete(id: TreatmentId) {
        repository.deleteById(id.value)
    }
}
