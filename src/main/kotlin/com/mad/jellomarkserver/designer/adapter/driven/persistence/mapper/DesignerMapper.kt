package com.mad.jellomarkserver.designer.adapter.driven.persistence.mapper

import com.mad.jellomarkserver.designer.adapter.driven.persistence.entity.DesignerJpaEntity
import com.mad.jellomarkserver.designer.core.domain.model.Designer

interface DesignerMapper {
    fun toEntity(domain: Designer): DesignerJpaEntity
    fun toDomain(entity: DesignerJpaEntity): Designer
}
