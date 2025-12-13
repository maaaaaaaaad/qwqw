package com.mad.jellomarkserver.auth.adapter.driven.persistence.mapper

import com.mad.jellomarkserver.auth.adapter.driven.persistence.entity.RefreshTokenJpaEntity
import com.mad.jellomarkserver.auth.core.domain.model.RefreshToken

interface RefreshTokenMapper {
    fun toEntity(domain: RefreshToken): RefreshTokenJpaEntity
    fun toDomain(entity: RefreshTokenJpaEntity): RefreshToken
}
