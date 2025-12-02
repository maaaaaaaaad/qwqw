package com.mad.jellomarkserver.auth.adapter.driven.persistence.mapper

import com.mad.jellomarkserver.auth.adapter.driven.persistence.entity.AuthJpaEntity
import com.mad.jellomarkserver.auth.core.domain.model.Auth

interface AuthMapper {
    fun toEntity(domain: Auth): AuthJpaEntity
    fun toDomain(entity: AuthJpaEntity): Auth
}
