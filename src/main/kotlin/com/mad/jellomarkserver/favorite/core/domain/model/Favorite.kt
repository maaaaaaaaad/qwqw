package com.mad.jellomarkserver.favorite.core.domain.model

import com.mad.jellomarkserver.beautishop.core.domain.model.ShopId
import com.mad.jellomarkserver.member.core.domain.model.MemberId
import java.time.Clock
import java.time.Instant

class Favorite private constructor(
    val id: FavoriteId,
    val memberId: MemberId,
    val shopId: ShopId,
    val createdAt: Instant
) {
    companion object {
        fun create(
            memberId: MemberId,
            shopId: ShopId,
            clock: Clock = Clock.systemUTC()
        ): Favorite {
            return Favorite(
                id = FavoriteId.new(),
                memberId = memberId,
                shopId = shopId,
                createdAt = Instant.now(clock)
            )
        }

        fun reconstruct(
            id: FavoriteId,
            memberId: MemberId,
            shopId: ShopId,
            createdAt: Instant
        ): Favorite {
            return Favorite(id, memberId, shopId, createdAt)
        }
    }

    fun isOwnedBy(memberId: MemberId): Boolean {
        return this.memberId == memberId
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Favorite) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}
