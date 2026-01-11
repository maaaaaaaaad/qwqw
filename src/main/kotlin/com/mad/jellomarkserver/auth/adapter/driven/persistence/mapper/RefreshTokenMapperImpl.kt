package com.mad.jellomarkserver.auth.adapter.driven.persistence.mapper

import com.mad.jellomarkserver.auth.adapter.driven.persistence.entity.RefreshTokenJpaEntity
import com.mad.jellomarkserver.auth.core.domain.model.RefreshToken
import com.mad.jellomarkserver.auth.core.domain.model.RefreshTokenId
import org.springframework.stereotype.Component

@Component
class RefreshTokenMapperImpl : RefreshTokenMapper {
    override fun toEntity(domain: RefreshToken): RefreshTokenJpaEntity {
        return RefreshTokenJpaEntity(
            id = domain.id.value,
            identifier = domain.identifier,
            userType = domain.userType,
            token = domain.token,
            expiresAt = domain.expiresAt,
            createdAt = domain.createdAt
        )
    }

    override fun toDomain(entity: RefreshTokenJpaEntity): RefreshToken {
        val id = RefreshTokenId.from(entity.id)
        return RefreshToken.reconstruct(
            id = id,
            identifier = entity.identifier,
            userType = entity.userType,
            token = entity.token,
            expiresAt = entity.expiresAt,
            createdAt = entity.createdAt
        )
    }
}
