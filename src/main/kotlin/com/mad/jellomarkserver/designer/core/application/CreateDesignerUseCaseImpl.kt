package com.mad.jellomarkserver.designer.core.application

import com.mad.jellomarkserver.beautishop.core.domain.exception.BeautishopNotFoundException
import com.mad.jellomarkserver.beautishop.core.domain.exception.UnauthorizedBeautishopAccessException
import com.mad.jellomarkserver.beautishop.core.domain.model.ShopId
import com.mad.jellomarkserver.beautishop.port.driven.BeautishopPort
import com.mad.jellomarkserver.designer.core.domain.model.*
import com.mad.jellomarkserver.designer.port.driven.DesignerPort
import com.mad.jellomarkserver.designer.port.driving.CreateDesignerCommand
import com.mad.jellomarkserver.designer.port.driving.CreateDesignerUseCase
import com.mad.jellomarkserver.owner.core.domain.model.OwnerId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class CreateDesignerUseCaseImpl(
    private val designerPort: DesignerPort,
    private val beautishopPort: BeautishopPort
) : CreateDesignerUseCase {

    @Transactional(isolation = Isolation.READ_COMMITTED)
    override fun create(command: CreateDesignerCommand): Designer {
        val ownerId = OwnerId.from(UUID.fromString(command.ownerId))
        val shopId = ShopId.from(UUID.fromString(command.shopId))

        val ownerShops = beautishopPort.findByOwnerId(ownerId)
        val shop = ownerShops.find { it.id == shopId }
            ?: run {
                val existingShop = beautishopPort.findById(shopId)
                    ?: throw BeautishopNotFoundException(shopId.value.toString())
                throw UnauthorizedBeautishopAccessException(existingShop.id.value.toString())
            }

        val designer = Designer.create(
            shopId = shop.id,
            name = DesignerName.of(command.name),
            nickname = DesignerNickname.ofNullable(command.nickname),
            intro = DesignerIntro.ofNullable(command.intro),
            photoUrls = DesignerPhotos.ofNullable(command.photoUrls)
        )

        return designerPort.save(designer)
    }
}
