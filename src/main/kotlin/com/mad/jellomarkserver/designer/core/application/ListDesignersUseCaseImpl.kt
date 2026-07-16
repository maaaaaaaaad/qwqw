package com.mad.jellomarkserver.designer.core.application

import com.mad.jellomarkserver.beautishop.core.domain.model.ShopId
import com.mad.jellomarkserver.designer.core.domain.model.Designer
import com.mad.jellomarkserver.designer.port.driven.DesignerPort
import com.mad.jellomarkserver.designer.port.driving.ListDesignersCommand
import com.mad.jellomarkserver.designer.port.driving.ListDesignersUseCase
import org.springframework.stereotype.Service
import java.util.*

@Service
class ListDesignersUseCaseImpl(
    private val designerPort: DesignerPort
) : ListDesignersUseCase {

    override fun execute(command: ListDesignersCommand): List<Designer> {
        val shopId = ShopId.from(UUID.fromString(command.shopId))

        return designerPort.findByShopId(shopId)
    }
}
