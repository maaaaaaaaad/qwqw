package com.mad.jellomarkserver.beautishop.adapter.driven.persistence.repository

import com.mad.jellomarkserver.beautishop.adapter.driven.persistence.mapper.BeautishopMapper
import com.mad.jellomarkserver.beautishop.core.domain.exception.DuplicateShopRegNumException
import com.mad.jellomarkserver.beautishop.core.domain.model.Beautishop
import com.mad.jellomarkserver.beautishop.core.domain.model.ShopId
import com.mad.jellomarkserver.beautishop.core.domain.model.ShopRegNum
import com.mad.jellomarkserver.beautishop.port.driven.BeautishopPort
import com.mad.jellomarkserver.common.persistence.ConstraintViolationTranslator
import com.mad.jellomarkserver.owner.core.domain.model.OwnerId
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

@Component
class BeautishopPersistenceAdapter(
    private val jpaRepository: BeautishopJpaRepository,
    private val mapper: BeautishopMapper,
    private val constraintTranslator: ConstraintViolationTranslator
) : BeautishopPort {
    override fun save(beautishop: Beautishop, ownerId: OwnerId): Beautishop {
        try {
            val entity = mapper.toEntity(beautishop, ownerId)
            val saved = jpaRepository.saveAndFlush(entity)
            return mapper.toDomain(saved)
        } catch (e: DataIntegrityViolationException) {
            constraintTranslator.translateAndThrow(
                e, mapOf(
                    "uk_beautishops_shop_reg_num" to { DuplicateShopRegNumException(beautishop.regNum.value) }
                )
            )
        }
    }

    override fun findById(id: ShopId): Beautishop? {
        return jpaRepository.findById(id.value).map { mapper.toDomain(it) }.orElse(null)
    }

    override fun findByOwnerId(ownerId: OwnerId): List<Beautishop> {
        return jpaRepository.findByOwnerId(ownerId.value).map { mapper.toDomain(it) }
    }

    override fun findByShopRegNum(shopRegNum: ShopRegNum): Beautishop? {
        return jpaRepository.findByShopRegNum(shopRegNum.value)?.let { mapper.toDomain(it) }
    }

    override fun findAllPaged(pageable: Pageable): Page<Beautishop> {
        return jpaRepository.findAll(pageable).map { mapper.toDomain(it) }
    }

    override fun delete(id: ShopId) {
        jpaRepository.deleteById(id.value)
    }
}
