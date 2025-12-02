package com.mad.jellomarkserver.auth.adapter.driven.persistence.mapper

import com.mad.jellomarkserver.auth.adapter.driven.persistence.entity.AuthJpaEntity
import com.mad.jellomarkserver.auth.core.domain.model.*
import org.springframework.stereotype.Component

@Component
class AuthMapperImpl : AuthMapper {
    override fun toEntity(domain: Auth): AuthJpaEntity {
        return AuthJpaEntity(
            id = domain.id.value,
            email = domain.email.value,
            hashedPassword = domain.hashedPassword.value,
            userType = domain.userType.name,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt
        )
    }

    override fun toDomain(entity: AuthJpaEntity): Auth {
        val id = AuthId.from(entity.id)
        val email = AuthEmail.of(entity.email)
        val hashedPassword = HashedPassword.from(entity.hashedPassword)
        val userType = UserType.valueOf(entity.userType)
        return Auth.reconstruct(
            id = id,
            email = email,
            hashedPassword = hashedPassword,
            userType = userType,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }
}
