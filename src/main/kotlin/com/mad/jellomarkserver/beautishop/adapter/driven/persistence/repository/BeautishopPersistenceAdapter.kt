package com.mad.jellomarkserver.beautishop.adapter.driven.persistence.repository

import com.mad.jellomarkserver.beautishop.adapter.driven.persistence.mapper.BeautishopMapper
import com.mad.jellomarkserver.beautishop.adapter.driven.persistence.specification.BeautishopSpecifications
import com.mad.jellomarkserver.beautishop.core.domain.exception.DuplicateShopRegNumException
import com.mad.jellomarkserver.beautishop.core.domain.model.Beautishop
import com.mad.jellomarkserver.beautishop.core.domain.model.ShopId
import com.mad.jellomarkserver.beautishop.core.domain.model.ShopRegNum
import com.mad.jellomarkserver.beautishop.port.driven.BeautishopFilterCriteria
import com.mad.jellomarkserver.beautishop.port.driven.BeautishopPort
import com.mad.jellomarkserver.beautishop.port.driving.SortBy
import com.mad.jellomarkserver.beautishop.port.driving.SortOrder
import com.mad.jellomarkserver.common.persistence.ConstraintViolationTranslator
import com.mad.jellomarkserver.owner.core.domain.model.OwnerId
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
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

    override fun findAllFiltered(criteria: BeautishopFilterCriteria, pageable: Pageable): Page<Beautishop> {
        val spec = Specification.allOf(
            BeautishopSpecifications.hasKeywordContaining(criteria.keyword),
            BeautishopSpecifications.hasCategory(criteria.categoryId),
            BeautishopSpecifications.hasMinRating(criteria.minRating)
        )

        val sortedPageable = createSortedPageable(criteria, pageable)

        return jpaRepository.findAll(spec, sortedPageable).map { mapper.toDomain(it) }
    }

    private fun createSortedPageable(criteria: BeautishopFilterCriteria, pageable: Pageable): Pageable {
        val sortField = when (criteria.sortBy) {
            SortBy.RATING -> "averageRating"
            SortBy.REVIEW_COUNT -> "reviewCount"
            SortBy.CREATED_AT -> "createdAt"
            SortBy.DISTANCE -> return pageable
        }

        val direction = when (criteria.sortOrder) {
            SortOrder.ASC -> Sort.Direction.ASC
            SortOrder.DESC -> Sort.Direction.DESC
        }

        return PageRequest.of(pageable.pageNumber, pageable.pageSize, Sort.by(direction, sortField))
    }

    override fun delete(id: ShopId) {
        jpaRepository.deleteById(id.value)
    }

    override fun updateStats(id: ShopId, averageRating: Double, reviewCount: Int) {
        jpaRepository.updateStats(id.value, averageRating, reviewCount)
    }
}
