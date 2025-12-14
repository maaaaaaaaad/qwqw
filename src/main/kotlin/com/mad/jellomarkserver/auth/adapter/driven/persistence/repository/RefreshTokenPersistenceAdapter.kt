package com.mad.jellomarkserver.auth.adapter.driven.persistence.repository

import com.mad.jellomarkserver.auth.adapter.driven.persistence.mapper.RefreshTokenMapper
import com.mad.jellomarkserver.auth.core.domain.model.AuthEmail
import com.mad.jellomarkserver.auth.core.domain.model.RefreshToken
import com.mad.jellomarkserver.auth.port.driven.RefreshTokenPort
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class RefreshTokenPersistenceAdapter(
    private val jpaRepository: RefreshTokenJpaRepository,
    private val mapper: RefreshTokenMapper
) : RefreshTokenPort {
    override fun save(refreshToken: RefreshToken): RefreshToken {
        val entity = mapper.toEntity(refreshToken)
        val saved = jpaRepository.saveAndFlush(entity)
        return mapper.toDomain(saved)
    }

    override fun findByEmail(email: AuthEmail): RefreshToken? {
        val entity = jpaRepository.findByEmail(email.value)
        return entity?.let { mapper.toDomain(it) }
    }

    override fun findByToken(token: String): RefreshToken? {
        val entity = jpaRepository.findByToken(token)
        return entity?.let { mapper.toDomain(it) }
    }

    @Transactional
    override fun deleteByEmail(email: AuthEmail) {
        jpaRepository.deleteByEmail(email.value)
    }
}
