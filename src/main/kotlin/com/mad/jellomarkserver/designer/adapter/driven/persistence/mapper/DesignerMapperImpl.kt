package com.mad.jellomarkserver.designer.adapter.driven.persistence.mapper

import com.mad.jellomarkserver.beautishop.core.domain.model.ShopId
import com.mad.jellomarkserver.designer.adapter.driven.persistence.entity.DesignerJpaEntity
import com.mad.jellomarkserver.designer.core.domain.model.*
import org.springframework.stereotype.Component

@Component
class DesignerMapperImpl : DesignerMapper {

    private companion object {
        const val PHOTO_DELIMITER = "|"
    }

    override fun toEntity(domain: Designer): DesignerJpaEntity {
        return DesignerJpaEntity(
            id = domain.id.value,
            shopId = domain.shopId.value,
            name = domain.name.value,
            nickname = domain.nickname?.value,
            intro = domain.intro?.value,
            photoUrls = serializePhotoUrls(domain.photoUrls),
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt
        )
    }

    override fun toDomain(entity: DesignerJpaEntity): Designer {
        return Designer.reconstruct(
            id = DesignerId.from(entity.id),
            shopId = ShopId.from(entity.shopId),
            name = DesignerName.of(entity.name),
            nickname = entity.nickname?.let { DesignerNickname.of(it) },
            intro = entity.intro?.let { DesignerIntro.of(it) },
            photoUrls = deserializePhotoUrls(entity.photoUrls),
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }

    private fun serializePhotoUrls(photoUrls: DesignerPhotos): String? {
        if (photoUrls.isEmpty()) return null
        return photoUrls.toStringList().joinToString(PHOTO_DELIMITER)
    }

    private fun deserializePhotoUrls(data: String?): DesignerPhotos {
        if (data.isNullOrBlank()) return DesignerPhotos.empty()
        val urls = data.split(PHOTO_DELIMITER).filter { it.isNotBlank() }
        return DesignerPhotos.of(urls)
    }
}
