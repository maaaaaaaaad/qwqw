package com.mad.jellomarkserver.beautishop.core.application

import com.mad.jellomarkserver.beautishop.core.domain.exception.BeautishopNotFoundException
import com.mad.jellomarkserver.beautishop.core.domain.model.Beautishop
import com.mad.jellomarkserver.beautishop.core.domain.model.ShopId
import com.mad.jellomarkserver.beautishop.port.driven.BeautishopPort
import com.mad.jellomarkserver.beautishop.port.driving.GetBeautishopCommand
import com.mad.jellomarkserver.beautishop.port.driving.GetBeautishopUseCase
import org.springframework.stereotype.Service
import java.util.*

@Service
class GetBeautishopUseCaseImpl(
    private val beautishopPort: BeautishopPort
) : GetBeautishopUseCase {

    override fun execute(command: GetBeautishopCommand): Beautishop {
        val shopId = ShopId.from(UUID.fromString(command.shopId))

        return beautishopPort.findById(shopId)
            ?: throw BeautishopNotFoundException(command.shopId)
    }
}
