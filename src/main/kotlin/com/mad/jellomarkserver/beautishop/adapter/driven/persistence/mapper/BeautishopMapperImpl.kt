package com.mad.jellomarkserver.beautishop.adapter.driven.persistence.mapper

import com.mad.jellomarkserver.beautishop.adapter.driven.persistence.entity.BeautishopJpaEntity
import com.mad.jellomarkserver.beautishop.core.domain.model.*
import com.mad.jellomarkserver.owner.core.domain.model.OwnerId
import org.springframework.stereotype.Component

@Component
class BeautishopMapperImpl : BeautishopMapper {
    override fun toEntity(domain: Beautishop, ownerId: OwnerId): BeautishopJpaEntity {
        return BeautishopJpaEntity(
            id = domain.id.value,
            ownerId = ownerId.value,
            name = domain.name.value,
            shopRegNum = domain.regNum.value,
            phoneNumber = domain.phoneNumber.value,
            address = domain.address.value,
            latitude = domain.gps.latitude,
            longitude = domain.gps.longitude,
            operatingTime = serializeOperatingTime(domain.operatingTime),
            description = domain.description?.value,
            image = domain.image?.value,
            averageRating = domain.averageRating,
            reviewCount = domain.reviewCount,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt
        )
    }

    override fun toDomain(entity: BeautishopJpaEntity): Beautishop {
        val id = ShopId.from(entity.id)
        val name = ShopName.of(entity.name)
        val regNum = ShopRegNum.of(entity.shopRegNum)
        val phoneNumber = ShopPhoneNumber.of(entity.phoneNumber)
        val address = ShopAddress.of(entity.address)
        val gps = ShopGPS.of(entity.latitude, entity.longitude)
        val operatingTime = deserializeOperatingTime(entity.operatingTime)
        val description = entity.description?.let { ShopDescription.of(it) }
        val image = entity.image?.let { ShopImage.of(it) }

        return Beautishop.reconstruct(
            id = id,
            name = name,
            regNum = regNum,
            phoneNumber = phoneNumber,
            address = address,
            gps = gps,
            operatingTime = operatingTime,
            description = description,
            image = image,
            averageRating = entity.averageRating,
            reviewCount = entity.reviewCount,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }

    private fun serializeOperatingTime(operatingTime: OperatingTime): String {
        return operatingTime.schedule.entries.joinToString(",") { (day, time) ->
            "$day:$time"
        }
    }

    private fun deserializeOperatingTime(json: String): OperatingTime {
        val schedule = json.split(",").associate { entry ->
            val (day, time) = entry.split(":", limit = 2)
            day to time
        }
        return OperatingTime.of(schedule)
    }
}
