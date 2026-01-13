package com.mad.jellomarkserver.beautishop.core.application

import com.mad.jellomarkserver.beautishop.core.domain.exception.BeautishopNotFoundException
import com.mad.jellomarkserver.beautishop.core.domain.exception.UnauthorizedBeautishopAccessException
import com.mad.jellomarkserver.beautishop.core.domain.model.*
import com.mad.jellomarkserver.beautishop.port.driven.BeautishopPort
import com.mad.jellomarkserver.beautishop.port.driving.UpdateBeautishopCommand
import com.mad.jellomarkserver.beautishop.port.driving.UpdateBeautishopUseCase
import com.mad.jellomarkserver.owner.core.domain.model.OwnerId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class UpdateBeautishopUseCaseImpl(
    private val beautishopPort: BeautishopPort
) : UpdateBeautishopUseCase {

    @Transactional(isolation = Isolation.READ_COMMITTED)
    override fun update(command: UpdateBeautishopCommand): Beautishop {
        val ownerId = OwnerId.from(UUID.fromString(command.ownerId))
        val shopId = ShopId.from(UUID.fromString(command.shopId))

        val ownerShops = beautishopPort.findByOwnerId(ownerId)
        val shop = ownerShops.find { it.id == shopId }
            ?: run {
                val existingShop = beautishopPort.findById(shopId)
                    ?: throw BeautishopNotFoundException(shopId.value.toString())
                throw UnauthorizedBeautishopAccessException(existingShop.id.value.toString())
            }

        val operatingTime = command.operatingTime?.let { OperatingTime.of(it) } ?: shop.operatingTime
        val description = ShopDescription.ofNullable(command.shopDescription)
        val image = ShopImage.ofNullable(command.shopImage)

        val updatedShop = shop.update(
            operatingTime = operatingTime,
            description = description,
            image = image
        )

        return beautishopPort.save(updatedShop, ownerId)
    }
}
