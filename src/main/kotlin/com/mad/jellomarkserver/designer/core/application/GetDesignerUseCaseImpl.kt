package com.mad.jellomarkserver.designer.core.application

import com.mad.jellomarkserver.designer.core.domain.exception.DesignerNotFoundException
import com.mad.jellomarkserver.designer.core.domain.model.Designer
import com.mad.jellomarkserver.designer.core.domain.model.DesignerId
import com.mad.jellomarkserver.designer.port.driven.DesignerPort
import com.mad.jellomarkserver.designer.port.driving.GetDesignerCommand
import com.mad.jellomarkserver.designer.port.driving.GetDesignerUseCase
import org.springframework.stereotype.Service
import java.util.*

@Service
class GetDesignerUseCaseImpl(
    private val designerPort: DesignerPort
) : GetDesignerUseCase {

    override fun execute(command: GetDesignerCommand): Designer {
        val designerId = DesignerId.from(UUID.fromString(command.designerId))

        return designerPort.findById(designerId)
            ?: throw DesignerNotFoundException(command.designerId)
    }
}
