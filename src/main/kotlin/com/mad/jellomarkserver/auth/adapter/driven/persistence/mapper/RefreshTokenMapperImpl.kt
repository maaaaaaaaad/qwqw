package com.mad.jellomarkserver.auth.adapter.driven.persistence.mapper

import com.mad.jellomarkserver.auth.adapter.driven.persistence.entity.RefreshTokenJpaEntity
import com.mad.jellomarkserver.auth.core.domain.model.AuthEmail
import com.mad.jellomarkserver.auth.core.domain.model.RefreshToken
import com.mad.jellomarkserver.auth.core.domain.model.RefreshTokenId
import org.springframework.stereotype.Component

@Component
class RefreshTokenMapperImpl : RefreshTokenMapper {
    override fun toEntity(domain: RefreshToken): RefreshTokenJpaEntity {
        return RefreshTokenJpaEntity(
            id = domain.id.value,
            email = domain.email.value,
            token = domain.token,
            expiresAt = domain.expiresAt,
            createdAt = domain.createdAt
        )
    }

    override fun toDomain(entity: RefreshTokenJpaEntity): RefreshToken {
        val id = RefreshTokenId.from(entity.id)
        val email = AuthEmail.of(entity.email)
        return RefreshToken.reconstruct(
            id = id,
            email = email,
            token = entity.token,
            expiresAt = entity.expiresAt,
            createdAt = entity.createdAt
        )
    }
}
