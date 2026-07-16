package com.mad.jellomarkserver.designer.core.domain.model

import com.mad.jellomarkserver.beautishop.core.domain.model.ShopId
import java.time.Clock
import java.time.Instant

class Designer private constructor(
    val id: DesignerId,
    val shopId: ShopId,
    val name: DesignerName,
    val nickname: DesignerNickname?,
    val intro: DesignerIntro?,
    val photoUrls: DesignerPhotos,
    val createdAt: Instant,
    val updatedAt: Instant
) {
    companion object {
        fun create(
            shopId: ShopId,
            name: DesignerName,
            nickname: DesignerNickname?,
            intro: DesignerIntro?,
            photoUrls: DesignerPhotos,
            clock: Clock = Clock.systemUTC()
        ): Designer {
            val now = Instant.now(clock)
            return Designer(
                id = DesignerId.new(),
                shopId = shopId,
                name = name,
                nickname = nickname,
                intro = intro,
                photoUrls = photoUrls,
                createdAt = now,
                updatedAt = now
            )
        }

        fun reconstruct(
            id: DesignerId,
            shopId: ShopId,
            name: DesignerName,
            nickname: DesignerNickname?,
            intro: DesignerIntro?,
            photoUrls: DesignerPhotos,
            createdAt: Instant,
            updatedAt: Instant
        ): Designer {
            return Designer(
                id = id,
                shopId = shopId,
                name = name,
                nickname = nickname,
                intro = intro,
                photoUrls = photoUrls,
                createdAt = createdAt,
                updatedAt = updatedAt
            )
        }
    }

    fun update(
        name: DesignerName? = null,
        nickname: DesignerNickname? = null,
        intro: DesignerIntro? = null,
        photoUrls: DesignerPhotos? = null,
        clearNickname: Boolean = false,
        clearIntro: Boolean = false,
        clock: Clock = Clock.systemUTC()
    ): Designer {
        return Designer(
            id = this.id,
            shopId = this.shopId,
            name = name ?: this.name,
            nickname = when {
                clearNickname -> null
                nickname != null -> nickname
                else -> this.nickname
            },
            intro = when {
                clearIntro -> null
                intro != null -> intro
                else -> this.intro
            },
            photoUrls = photoUrls ?: this.photoUrls,
            createdAt = this.createdAt,
            updatedAt = Instant.now(clock)
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Designer) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}
