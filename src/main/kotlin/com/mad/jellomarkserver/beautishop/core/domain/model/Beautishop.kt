package com.mad.jellomarkserver.beautishop.core.domain.model

import java.time.Clock
import java.time.Instant

class Beautishop private constructor(
    val id: ShopId,
    val name: ShopName,
    val regNum: ShopRegNum,
    val phoneNumber: ShopPhoneNumber,
    val address: ShopAddress,
    val gps: ShopGPS,
    val operatingTime: OperatingTime,
    val description: ShopDescription?,
    val images: ShopImages,
    val averageRating: AverageRating,
    val reviewCount: ReviewCount,
    val createdAt: Instant,
    val updatedAt: Instant
) {
    companion object {
        fun create(
            name: ShopName,
            regNum: ShopRegNum,
            phoneNumber: ShopPhoneNumber,
            address: ShopAddress,
            gps: ShopGPS,
            operatingTime: OperatingTime,
            description: ShopDescription?,
            images: ShopImages,
            clock: Clock = Clock.systemUTC()
        ): Beautishop {
            val now = Instant.now(clock)
            return Beautishop(
                id = ShopId.new(),
                name = name,
                regNum = regNum,
                phoneNumber = phoneNumber,
                address = address,
                gps = gps,
                operatingTime = operatingTime,
                description = description,
                images = images,
                averageRating = AverageRating.zero(),
                reviewCount = ReviewCount.zero(),
                createdAt = now,
                updatedAt = now
            )
        }

        fun reconstruct(
            id: ShopId,
            name: ShopName,
            regNum: ShopRegNum,
            phoneNumber: ShopPhoneNumber,
            address: ShopAddress,
            gps: ShopGPS,
            operatingTime: OperatingTime,
            description: ShopDescription?,
            images: ShopImages,
            averageRating: AverageRating,
            reviewCount: ReviewCount,
            createdAt: Instant,
            updatedAt: Instant
        ): Beautishop {
            return Beautishop(
                id = id,
                name = name,
                regNum = regNum,
                phoneNumber = phoneNumber,
                address = address,
                gps = gps,
                operatingTime = operatingTime,
                description = description,
                images = images,
                averageRating = averageRating,
                reviewCount = reviewCount,
                createdAt = createdAt,
                updatedAt = updatedAt
            )
        }
    }

    fun update(
        operatingTime: OperatingTime,
        description: ShopDescription?,
        images: ShopImages,
        clock: Clock = Clock.systemUTC()
    ): Beautishop {
        return Beautishop(
            id = this.id,
            name = this.name,
            regNum = this.regNum,
            phoneNumber = this.phoneNumber,
            address = this.address,
            gps = this.gps,
            operatingTime = operatingTime,
            description = description,
            images = images,
            averageRating = this.averageRating,
            reviewCount = this.reviewCount,
            createdAt = this.createdAt,
            updatedAt = Instant.now(clock)
        )
    }

    fun updateStats(
        averageRating: AverageRating,
        reviewCount: ReviewCount
    ): Beautishop {
        return Beautishop(
            id = this.id,
            name = this.name,
            regNum = this.regNum,
            phoneNumber = this.phoneNumber,
            address = this.address,
            gps = this.gps,
            operatingTime = this.operatingTime,
            description = this.description,
            images = this.images,
            averageRating = averageRating,
            reviewCount = reviewCount,
            createdAt = this.createdAt,
            updatedAt = this.updatedAt
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Beautishop) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}
