package com.mad.jellomarkserver.beautishop.core.application

import com.mad.jellomarkserver.beautishop.core.domain.exception.BeautishopNotFoundException
import com.mad.jellomarkserver.beautishop.core.domain.exception.UnauthorizedBeautishopAccessException
import com.mad.jellomarkserver.beautishop.core.domain.model.ShopId
import com.mad.jellomarkserver.beautishop.port.driven.BeautishopPort
import com.mad.jellomarkserver.beautishop.port.driving.DeleteBeautishopCommand
import com.mad.jellomarkserver.beautishop.port.driving.DeleteBeautishopUseCase
import com.mad.jellomarkserver.owner.core.domain.model.OwnerId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class DeleteBeautishopUseCaseImpl(
    private val beautishopPort: BeautishopPort
) : DeleteBeautishopUseCase {

    @Transactional(isolation = Isolation.READ_COMMITTED)
    override fun delete(command: DeleteBeautishopCommand) {
        val ownerId = OwnerId.from(UUID.fromString(command.ownerId))
        val shopId = ShopId.from(UUID.fromString(command.shopId))

        val ownerShops = beautishopPort.findByOwnerId(ownerId)
        val shop = ownerShops.find { it.id == shopId }
            ?: run {
                val existingShop = beautishopPort.findById(shopId)
                    ?: throw BeautishopNotFoundException(shopId.value.toString())
                throw UnauthorizedBeautishopAccessException(existingShop.id.value.toString())
            }

        beautishopPort.delete(shop.id)
    }
}
