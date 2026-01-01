package com.mad.jellomarkserver.beautishop.core.application

import com.mad.jellomarkserver.beautishop.core.domain.model.*
import com.mad.jellomarkserver.beautishop.port.driven.BeautishopPort
import com.mad.jellomarkserver.beautishop.port.driving.CreateBeautishopCommand
import com.mad.jellomarkserver.beautishop.port.driving.CreateBeautishopUseCase
import com.mad.jellomarkserver.owner.core.domain.model.OwnerId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class CreateBeautishopUseCaseImpl(
    private val beautishopPort: BeautishopPort
) : CreateBeautishopUseCase {

    @Transactional(isolation = Isolation.READ_COMMITTED)
    override fun create(command: CreateBeautishopCommand): Beautishop {
        val ownerId = OwnerId.from(UUID.fromString(command.ownerId))
        val name = ShopName.of(command.shopName)
        val regNum = ShopRegNum.of(command.shopRegNum)
        val phoneNumber = ShopPhoneNumber.of(command.shopPhoneNumber)
        val address = ShopAddress.of(command.shopAddress)
        val gps = ShopGPS.of(command.latitude, command.longitude)
        val operatingTime = OperatingTime.of(command.operatingTime)
        val description = ShopDescription.ofNullable(command.shopDescription)
        val image = ShopImage.ofNullable(command.shopImage)

        val beautishop = Beautishop.create(
            name = name,
            regNum = regNum,
            phoneNumber = phoneNumber,
            address = address,
            gps = gps,
            operatingTime = operatingTime,
            description = description,
            image = image
        )

        return beautishopPort.save(beautishop, ownerId)
    }
}
