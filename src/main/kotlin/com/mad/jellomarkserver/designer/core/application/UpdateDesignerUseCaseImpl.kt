package com.mad.jellomarkserver.designer.core.application

import com.mad.jellomarkserver.beautishop.port.driven.BeautishopPort
import com.mad.jellomarkserver.designer.core.domain.exception.DesignerNotFoundException
import com.mad.jellomarkserver.designer.core.domain.exception.UnauthorizedDesignerAccessException
import com.mad.jellomarkserver.designer.core.domain.model.*
import com.mad.jellomarkserver.designer.port.driven.DesignerPort
import com.mad.jellomarkserver.designer.port.driving.UpdateDesignerCommand
import com.mad.jellomarkserver.designer.port.driving.UpdateDesignerUseCase
import com.mad.jellomarkserver.owner.core.domain.model.OwnerId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class UpdateDesignerUseCaseImpl(
    private val designerPort: DesignerPort,
    private val beautishopPort: BeautishopPort
) : UpdateDesignerUseCase {

    @Transactional(isolation = Isolation.READ_COMMITTED)
    override fun update(command: UpdateDesignerCommand): Designer {
        val designerId = DesignerId.from(UUID.fromString(command.designerId))
        val ownerId = OwnerId.from(UUID.fromString(command.ownerId))

        val designer = designerPort.findById(designerId)
            ?: throw DesignerNotFoundException(command.designerId)

        val ownerShops = beautishopPort.findByOwnerId(ownerId)
        val ownsShop = ownerShops.any { it.id == designer.shopId }

        if (!ownsShop) {
            throw UnauthorizedDesignerAccessException(command.designerId)
        }

        val updatedDesigner = designer.update(
            name = command.name?.let { DesignerName.of(it) },
            nickname = command.nickname?.let { DesignerNickname.ofNullable(it) },
            intro = command.intro?.let { DesignerIntro.ofNullable(it) },
            photoUrls = command.photoUrls?.let { DesignerPhotos.ofNullable(it) }
        )

        return designerPort.save(updatedDesigner)
    }
}
