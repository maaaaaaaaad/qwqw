package com.mad.jellomarkserver.designer.adapter.driven.persistence

import com.mad.jellomarkserver.beautishop.core.domain.model.ShopId
import com.mad.jellomarkserver.designer.adapter.driven.persistence.mapper.DesignerMapper
import com.mad.jellomarkserver.designer.adapter.driven.persistence.repository.DesignerJpaRepository
import com.mad.jellomarkserver.designer.core.domain.model.Designer
import com.mad.jellomarkserver.designer.core.domain.model.DesignerId
import com.mad.jellomarkserver.designer.port.driven.DesignerPort
import org.springframework.stereotype.Component

@Component
class DesignerPersistenceAdapter(
    private val repository: DesignerJpaRepository,
    private val mapper: DesignerMapper
) : DesignerPort {

    override fun save(designer: Designer): Designer {
        val entity = mapper.toEntity(designer)
        val savedEntity = repository.save(entity)
        return mapper.toDomain(savedEntity)
    }

    override fun findById(id: DesignerId): Designer? {
        return repository.findById(id.value)
            .map { mapper.toDomain(it) }
            .orElse(null)
    }

    override fun findByIds(ids: List<DesignerId>): List<Designer> {
        if (ids.isEmpty()) return emptyList()
        return repository.findAllById(ids.map { it.value }).map { mapper.toDomain(it) }
    }

    override fun findByShopId(shopId: ShopId): List<Designer> {
        return repository.findByShopId(shopId.value)
            .map { mapper.toDomain(it) }
    }

    override fun delete(id: DesignerId) {
        repository.deleteById(id.value)
    }

    @org.springframework.transaction.annotation.Transactional
    override fun deleteAllByShopId(shopId: ShopId) {
        repository.deleteByShopId(shopId.value)
        repository.flush()
    }
}
