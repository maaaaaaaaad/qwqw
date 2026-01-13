package com.mad.jellomarkserver.treatment.core.domain.model

import com.mad.jellomarkserver.beautishop.core.domain.model.ShopId
import java.time.Clock
import java.time.Instant

class Treatment private constructor(
    val id: TreatmentId,
    val shopId: ShopId,
    val name: TreatmentName,
    val price: TreatmentPrice,
    val duration: TreatmentDuration,
    val description: TreatmentDescription?,
    val createdAt: Instant,
    val updatedAt: Instant
) {
    companion object {
        fun create(
            shopId: ShopId,
            name: TreatmentName,
            price: TreatmentPrice,
            duration: TreatmentDuration,
            description: TreatmentDescription?,
            clock: Clock = Clock.systemUTC()
        ): Treatment {
            val now = Instant.now(clock)
            return Treatment(
                id = TreatmentId.new(),
                shopId = shopId,
                name = name,
                price = price,
                duration = duration,
                description = description,
                createdAt = now,
                updatedAt = now
            )
        }

        fun reconstruct(
            id: TreatmentId,
            shopId: ShopId,
            name: TreatmentName,
            price: TreatmentPrice,
            duration: TreatmentDuration,
            description: TreatmentDescription?,
            createdAt: Instant,
            updatedAt: Instant
        ): Treatment {
            return Treatment(
                id = id,
                shopId = shopId,
                name = name,
                price = price,
                duration = duration,
                description = description,
                createdAt = createdAt,
                updatedAt = updatedAt
            )
        }
    }

    fun update(
        name: TreatmentName,
        price: TreatmentPrice,
        duration: TreatmentDuration,
        description: TreatmentDescription?,
        clock: Clock = Clock.systemUTC()
    ): Treatment {
        return Treatment(
            id = this.id,
            shopId = this.shopId,
            name = name,
            price = price,
            duration = duration,
            description = description,
            createdAt = this.createdAt,
            updatedAt = Instant.now(clock)
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Treatment) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}
