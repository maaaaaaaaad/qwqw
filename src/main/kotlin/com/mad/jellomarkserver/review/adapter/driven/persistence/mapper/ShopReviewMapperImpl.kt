package com.mad.jellomarkserver.review.adapter.driven.persistence.mapper

import com.mad.jellomarkserver.beautishop.core.domain.model.ShopId
import com.mad.jellomarkserver.member.core.domain.model.MemberId
import com.mad.jellomarkserver.review.adapter.driven.persistence.entity.ShopReviewJpaEntity
import com.mad.jellomarkserver.review.core.domain.model.*
import org.springframework.stereotype.Component

@Component
class ShopReviewMapperImpl : ShopReviewMapper {

    override fun toEntity(domain: ShopReview): ShopReviewJpaEntity {
        return ShopReviewJpaEntity(
            id = domain.id.value,
            shopId = domain.shopId.value,
            memberId = domain.memberId.value,
            rating = domain.rating.value,
            content = domain.content.value,
            images = serializeImages(domain.images),
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt
        )
    }

    override fun toDomain(entity: ShopReviewJpaEntity): ShopReview {
        return ShopReview.reconstruct(
            id = ReviewId.from(entity.id),
            shopId = ShopId.from(entity.shopId),
            memberId = MemberId.from(entity.memberId),
            rating = ReviewRating.of(entity.rating),
            content = ReviewContent.of(entity.content),
            images = deserializeImages(entity.images),
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }

    private fun serializeImages(images: ReviewImages?): String? {
        if (images == null || images.urls.isEmpty()) {
            return null
        }
        return images.urls.joinToString(",")
    }

    private fun deserializeImages(imagesStr: String?): ReviewImages? {
        if (imagesStr.isNullOrBlank()) {
            return null
        }
        val urls = imagesStr.split(",").filter { it.isNotBlank() }
        return ReviewImages.ofNullable(urls)
    }
}
