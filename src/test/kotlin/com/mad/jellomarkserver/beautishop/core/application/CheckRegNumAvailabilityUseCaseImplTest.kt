package com.mad.jellomarkserver.beautishop.core.application

import com.mad.jellomarkserver.beautishop.core.domain.exception.DuplicateShopRegNumException
import com.mad.jellomarkserver.beautishop.core.domain.exception.InvalidShopRegNumException
import com.mad.jellomarkserver.beautishop.core.domain.model.Beautishop
import com.mad.jellomarkserver.beautishop.core.domain.model.ShopRegNum
import com.mad.jellomarkserver.beautishop.port.driven.BeautishopPort
import com.mad.jellomarkserver.beautishop.port.driving.CheckRegNumAvailabilityUseCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import kotlin.test.assertFailsWith

@ExtendWith(MockitoExtension::class)
class CheckRegNumAvailabilityUseCaseImplTest {

    @Mock
    private lateinit var beautishopPort: BeautishopPort

    private lateinit var useCase: CheckRegNumAvailabilityUseCase

    @BeforeEach
    fun setup() {
        useCase = CheckRegNumAvailabilityUseCaseImpl(beautishopPort)
    }

    @Test
    fun `should pass when reg num is not taken`() {
        val regNum = "123-45-67890"

        `when`(beautishopPort.findByShopRegNum(ShopRegNum.of(regNum)))
            .thenReturn(null)

        useCase.check(regNum)
    }

    @Test
    fun `should throw DuplicateShopRegNumException when reg num already exists`() {
        val regNum = "123-45-67890"

        `when`(beautishopPort.findByShopRegNum(ShopRegNum.of(regNum)))
            .thenReturn(org.mockito.Mockito.mock(Beautishop::class.java))

        assertFailsWith<DuplicateShopRegNumException> {
            useCase.check(regNum)
        }
    }

    @Test
    fun `should throw InvalidShopRegNumException when reg num format is invalid`() {
        assertFailsWith<InvalidShopRegNumException> {
            useCase.check("12345")
        }
    }

    @Test
    fun `should handle reg num without hyphens`() {
        val regNum = "1234567890"

        `when`(beautishopPort.findByShopRegNum(ShopRegNum.of(regNum)))
            .thenReturn(null)

        useCase.check(regNum)
    }

    @Test
    fun `should trim whitespace from reg num`() {
        val regNum = "  123-45-67890  "

        `when`(beautishopPort.findByShopRegNum(ShopRegNum.of(regNum)))
            .thenReturn(null)

        useCase.check(regNum)
    }
}
