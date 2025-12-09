package com.mad.jellomarkserver.auth.adapter.driven.persistence.repository

import com.mad.jellomarkserver.auth.adapter.driven.persistence.mapper.AuthMapper
import com.mad.jellomarkserver.auth.core.domain.exception.DuplicateAuthEmailException
import com.mad.jellomarkserver.auth.core.domain.model.Auth
import com.mad.jellomarkserver.auth.core.domain.model.AuthEmail
import com.mad.jellomarkserver.auth.port.driven.AuthPort
import com.mad.jellomarkserver.common.persistence.ConstraintViolationTranslator
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Component

@Component
class AuthPersistenceAdapter(
    private val jpaRepository: AuthJpaRepository,
    private val mapper: AuthMapper,
    private val constraintTranslator: ConstraintViolationTranslator
) : AuthPort {
    override fun save(auth: Auth): Auth {
        try {
            val entity = mapper.toEntity(auth)
            val saved = jpaRepository.saveAndFlush(entity)
            return mapper.toDomain(saved)
        } catch (e: DataIntegrityViolationException) {
            constraintTranslator.translateAndThrow(
                e, mapOf(
                    "uk_auths_email" to { DuplicateAuthEmailException(auth.email.value) }
                )
            )
        }
    }

    override fun findByEmail(email: AuthEmail): Auth? {
        val entity = jpaRepository.findByEmail(email.value)
        return entity?.let { mapper.toDomain(it) }
    }
}
