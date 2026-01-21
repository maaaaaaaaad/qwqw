package com.mad.jellomarkserver.favorite.adapter.driven.persistence.repository

import com.mad.jellomarkserver.beautishop.core.domain.model.ShopId
import com.mad.jellomarkserver.common.persistence.ConstraintViolationTranslator
import com.mad.jellomarkserver.favorite.adapter.driven.persistence.mapper.FavoriteMapper
import com.mad.jellomarkserver.favorite.core.domain.exception.DuplicateFavoriteException
import com.mad.jellomarkserver.favorite.core.domain.model.Favorite
import com.mad.jellomarkserver.favorite.core.domain.model.FavoriteId
import com.mad.jellomarkserver.favorite.port.driven.FavoritePort
import com.mad.jellomarkserver.member.core.domain.model.MemberId
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

@Component
class FavoritePersistenceAdapter(
    private val jpaRepository: FavoriteJpaRepository,
    private val mapper: FavoriteMapper,
    private val constraintTranslator: ConstraintViolationTranslator
) : FavoritePort {

    override fun save(favorite: Favorite): Favorite {
        try {
            val entity = mapper.toEntity(favorite)
            val saved = jpaRepository.saveAndFlush(entity)
            return mapper.toDomain(saved)
        } catch (e: DataIntegrityViolationException) {
            constraintTranslator.translateAndThrow(
                e, mapOf(
                    "uk_favorites_member_shop" to {
                        DuplicateFavoriteException(
                            favorite.shopId.value.toString(),
                            favorite.memberId.value.toString()
                        )
                    }
                )
            )
        }
    }

    override fun findById(id: FavoriteId): Favorite? {
        return jpaRepository.findById(id.value).map { mapper.toDomain(it) }.orElse(null)
    }

    override fun findByMemberIdAndShopId(memberId: MemberId, shopId: ShopId): Favorite? {
        return jpaRepository.findByMemberIdAndShopId(memberId.value, shopId.value)
            ?.let { mapper.toDomain(it) }
    }

    override fun findByMemberId(memberId: MemberId, pageable: Pageable): Page<Favorite> {
        return jpaRepository.findByMemberId(memberId.value, pageable).map { mapper.toDomain(it) }
    }

    override fun existsByMemberIdAndShopId(memberId: MemberId, shopId: ShopId): Boolean {
        return jpaRepository.existsByMemberIdAndShopId(memberId.value, shopId.value)
    }

    override fun deleteByMemberIdAndShopId(memberId: MemberId, shopId: ShopId) {
        jpaRepository.deleteByMemberIdAndShopId(memberId.value, shopId.value)
    }

    override fun countByShopId(shopId: ShopId): Int {
        return jpaRepository.countByShopId(shopId.value)
    }
}
