package com.mad.jellomarkserver.review.adapter.driven.persistence.mapper

import com.mad.jellomarkserver.review.adapter.driven.persistence.entity.ShopReviewJpaEntity
import com.mad.jellomarkserver.review.core.domain.model.ShopReview

interface ShopReviewMapper {
    fun toEntity(domain: ShopReview): ShopReviewJpaEntity
    fun toDomain(entity: ShopReviewJpaEntity): ShopReview
}
