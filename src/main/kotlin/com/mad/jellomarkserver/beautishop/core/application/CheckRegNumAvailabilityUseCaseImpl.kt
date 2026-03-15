package com.mad.jellomarkserver.beautishop.core.application

import com.mad.jellomarkserver.beautishop.core.domain.exception.DuplicateShopRegNumException
import com.mad.jellomarkserver.beautishop.core.domain.model.ShopRegNum
import com.mad.jellomarkserver.beautishop.port.driven.BeautishopPort
import com.mad.jellomarkserver.beautishop.port.driving.CheckRegNumAvailabilityUseCase
import org.springframework.stereotype.Service

@Service
class CheckRegNumAvailabilityUseCaseImpl(
    private val beautishopPort: BeautishopPort
) : CheckRegNumAvailabilityUseCase {

    override fun check(regNum: String) {
        val shopRegNum = ShopRegNum.of(regNum)
        val existing = beautishopPort.findByShopRegNum(shopRegNum)
        if (existing != null) {
            throw DuplicateShopRegNumException(shopRegNum.value)
        }
    }
}
