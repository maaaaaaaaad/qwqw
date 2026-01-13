package com.mad.jellomarkserver.category.core.application

import com.mad.jellomarkserver.beautishop.core.domain.exception.BeautishopNotFoundException
import com.mad.jellomarkserver.beautishop.core.domain.model.*
import com.mad.jellomarkserver.beautishop.port.driven.BeautishopPort
import com.mad.jellomarkserver.category.core.domain.exception.CategoryNotFoundException
import com.mad.jellomarkserver.category.core.domain.exception.UnauthorizedShopAccessException
import com.mad.jellomarkserver.category.core.domain.model.Category
import com.mad.jellomarkserver.category.core.domain.model.CategoryName
import com.mad.jellomarkserver.category.port.driven.CategoryPort
import com.mad.jellomarkserver.category.port.driven.ShopCategoryPort
import com.mad.jellomarkserver.category.port.driving.SetShopCategoriesCommand
import com.mad.jellomarkserver.owner.core.domain.model.OwnerId
import com.mad.jellomarkserver.owner.port.driven.OwnerPort
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.*

@ExtendWith(MockitoExtension::class)
class SetShopCategoriesUseCaseImplTest {

    @Mock
    private lateinit var beautishopPort: BeautishopPort

    @Mock
    private lateinit var ownerPort: OwnerPort

    @Mock
    private lateinit var categoryPort: CategoryPort

    @Mock
    private lateinit var shopCategoryPort: ShopCategoryPort

    private lateinit var useCase: SetShopCategoriesUseCaseImpl

    @BeforeEach
    fun setup() {
        useCase = SetShopCategoriesUseCaseImpl(beautishopPort, ownerPort, categoryPort, shopCategoryPort)
    }

    @Test
    fun `should set categories for shop when owner is authorized`() {
        val ownerId = OwnerId.new()
        val categoryId = UUID.randomUUID()

        val shop = createBeautishop()
        val category = Category.create(CategoryName.of("헤어"))

        whenever(beautishopPort.findByOwnerId(ownerId)).thenReturn(listOf(shop))
        whenever(categoryPort.findByIds(any())).thenReturn(listOf(category))
        whenever(shopCategoryPort.findCategoriesByShopId(any())).thenReturn(listOf(category))

        val command = SetShopCategoriesCommand(
            shopId = shop.id.value.toString(),
            ownerId = ownerId.value.toString(),
            categoryIds = listOf(categoryId.toString())
        )
        val result = useCase.execute(command)

        assertEquals(1, result.size)
        verify(shopCategoryPort).setShopCategories(any(), any())
    }

    @Test
    fun `should throw BeautishopNotFoundException when shop does not exist`() {
        val ownerId = OwnerId.new()
        val shopId = ShopId.new()

        whenever(beautishopPort.findByOwnerId(ownerId)).thenReturn(emptyList())
        whenever(beautishopPort.findById(shopId)).thenReturn(null)

        val command = SetShopCategoriesCommand(
            shopId = shopId.value.toString(),
            ownerId = ownerId.value.toString(),
            categoryIds = emptyList()
        )

        assertThrows(BeautishopNotFoundException::class.java) {
            useCase.execute(command)
        }
    }

    @Test
    fun `should throw UnauthorizedShopAccessException when owner does not own the shop`() {
        val ownerId = OwnerId.new()
        val otherOwnersShop = createBeautishop()

        whenever(beautishopPort.findByOwnerId(ownerId)).thenReturn(emptyList())
        whenever(beautishopPort.findById(otherOwnersShop.id)).thenReturn(otherOwnersShop)

        val command = SetShopCategoriesCommand(
            shopId = otherOwnersShop.id.value.toString(),
            ownerId = ownerId.value.toString(),
            categoryIds = emptyList()
        )

        assertThrows(UnauthorizedShopAccessException::class.java) {
            useCase.execute(command)
        }
    }

    @Test
    fun `should throw CategoryNotFoundException when category does not exist`() {
        val ownerId = OwnerId.new()
        val nonExistentCategoryId = UUID.randomUUID()

        val shop = createBeautishop()

        whenever(beautishopPort.findByOwnerId(ownerId)).thenReturn(listOf(shop))
        whenever(categoryPort.findByIds(any())).thenReturn(emptyList())

        val command = SetShopCategoriesCommand(
            shopId = shop.id.value.toString(),
            ownerId = ownerId.value.toString(),
            categoryIds = listOf(nonExistentCategoryId.toString())
        )

        assertThrows(CategoryNotFoundException::class.java) {
            useCase.execute(command)
        }
    }

    private fun createBeautishop(): Beautishop {
        return Beautishop.create(
            name = ShopName.of("테스트샵"),
            regNum = ShopRegNum.of("123-45-67890"),
            phoneNumber = ShopPhoneNumber.of("010-1234-5678"),
            address = ShopAddress.of("서울특별시 강남구"),
            gps = ShopGPS.of(37.5, 127.0),
            operatingTime = OperatingTime.of(mapOf("monday" to "09:00-18:00")),
            description = ShopDescription.of("설명"),
            image = ShopImage.of("https://example.com/image.jpg")
        )
    }
}
