package com.mad.jellomarkserver.owner.adapter.driven.persistence.repository

import com.mad.jellomarkserver.common.persistence.ConstraintViolationTranslator
import com.mad.jellomarkserver.owner.adapter.driven.persistence.mapper.OwnerMapper
import com.mad.jellomarkserver.owner.core.domain.exception.DuplicateBusinessNumberException
import com.mad.jellomarkserver.owner.core.domain.exception.DuplicatePhoneNumberException
import com.mad.jellomarkserver.owner.core.domain.model.Owner
import com.mad.jellomarkserver.owner.port.driven.OwnerPort
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Component

@Component
class OwnerPersistenceAdapter(
    private val jpaRepository: OwnerJpaRepository,
    private val mapper: OwnerMapper,
    private val constraintTranslator: ConstraintViolationTranslator
) : OwnerPort {
    override fun save(owner: Owner): Owner {
        try {
            val entity = mapper.toEntity(owner)
            val saved = jpaRepository.saveAndFlush(entity)
            return mapper.toDomain(saved)
        } catch (e: DataIntegrityViolationException) {
            constraintTranslator.translateAndThrow(
                e, mapOf(
                    "uk_owners_business_number" to { DuplicateBusinessNumberException(owner.businessNumber.value) },
                    "uk_owners_phone_number" to { DuplicatePhoneNumberException(owner.phoneNumber.value) },
                )
            )
        }
    }
}
