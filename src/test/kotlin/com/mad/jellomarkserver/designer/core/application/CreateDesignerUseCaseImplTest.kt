package com.mad.jellomarkserver.designer.core.application

import com.mad.jellomarkserver.beautishop.core.domain.exception.BeautishopNotFoundException
import com.mad.jellomarkserver.beautishop.core.domain.exception.UnauthorizedBeautishopAccessException
import com.mad.jellomarkserver.beautishop.core.domain.model.*
import com.mad.jellomarkserver.beautishop.port.driven.BeautishopPort
import com.mad.jellomarkserver.designer.core.domain.exception.InvalidDesignerNameException
import com.mad.jellomarkserver.designer.core.domain.exception.InvalidDesignerPhotosException
import com.mad.jellomarkserver.designer.core.domain.model.Designer
import com.mad.jellomarkserver.designer.port.driven.DesignerPort
import com.mad.jellomarkserver.designer.port.driving.CreateDesignerCommand
import com.mad.jellomarkserver.designer.port.driving.CreateDesignerUseCase
import com.mad.jellomarkserver.owner.core.domain.model.OwnerId
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import kotlin.test.assertFailsWith

@ExtendWith(MockitoExtension::class)
class CreateDesignerUseCaseImplTest {

    @Mock
    private lateinit var designerPort: DesignerPort

    @Mock
    private lateinit var beautishopPort: BeautishopPort

    private lateinit var useCase: CreateDesignerUseCase

    @BeforeEach
    fun setup() {
        useCase = CreateDesignerUseCaseImpl(designerPort, beautishopPort)
    }

    private fun createBeautishop(): Beautishop {
        return Beautishop.create(
            name = ShopName.of("Test Shop"),
            regNum = ShopRegNum.of("123-45-67890"),
            phoneNumber = ShopPhoneNumber.of("010-1234-5678"),
            address = ShopAddress.of("서울특별시 강남구"),
            gps = ShopGPS.of(37.5, 127.0),
            operatingTime = OperatingTime.of(mapOf("monday" to "09:00-18:00")),
            description = null,
            images = ShopImages.empty()
        )
    }

    @Test
    fun `should create designer with all fields`() {
        val ownerId = OwnerId.new()
        val shop = createBeautishop()

        whenever(beautishopPort.findByOwnerId(ownerId)).thenReturn(listOf(shop))
        whenever(designerPort.save(any())).thenAnswer { it.arguments[0] as Designer }

        val command = CreateDesignerCommand(
            shopId = shop.id.value.toString(),
            ownerId = ownerId.value.toString(),
            name = "김디자이너",
            nickname = "네일요정",
            intro = "10년 경력",
            photoUrls = listOf("https://example.com/1.jpg")
        )

        val result = useCase.create(command)

        assertNotNull(result.id)
        assertEquals(shop.id, result.shopId)
        assertEquals("김디자이너", result.name.value)
        assertEquals("네일요정", result.nickname?.value)
        assertEquals("10년 경력", result.intro?.value)
        assertEquals(1, result.photoUrls.size)
    }

    @Test
    fun `should create designer with only required fields`() {
        val ownerId = OwnerId.new()
        val shop = createBeautishop()

        whenever(beautishopPort.findByOwnerId(ownerId)).thenReturn(listOf(shop))
        whenever(designerPort.save(any())).thenAnswer { it.arguments[0] as Designer }

        val command = CreateDesignerCommand(
            shopId = shop.id.value.toString(),
            ownerId = ownerId.value.toString(),
            name = "김디자이너",
            nickname = null,
            intro = null,
            photoUrls = null
        )

        val result = useCase.create(command)

        assertEquals("김디자이너", result.name.value)
        assertNull(result.nickname)
        assertNull(result.intro)
        assertTrue(result.photoUrls.isEmpty())
    }

    @Test
    fun `should throw BeautishopNotFoundException when shop does not exist`() {
        val ownerId = OwnerId.new()
        val nonExistentShopId = ShopId.new()

        whenever(beautishopPort.findByOwnerId(ownerId)).thenReturn(emptyList())
        whenever(beautishopPort.findById(nonExistentShopId)).thenReturn(null)

        val command = CreateDesignerCommand(
            shopId = nonExistentShopId.value.toString(),
            ownerId = ownerId.value.toString(),
            name = "김디자이너",
            nickname = null,
            intro = null,
            photoUrls = null
        )

        assertFailsWith<BeautishopNotFoundException> {
            useCase.create(command)
        }
    }

    @Test
    fun `should throw UnauthorizedBeautishopAccessException when owner does not own the shop`() {
        val ownerId = OwnerId.new()
        val otherShop = createBeautishop()

        whenever(beautishopPort.findByOwnerId(ownerId)).thenReturn(emptyList())
        whenever(beautishopPort.findById(otherShop.id)).thenReturn(otherShop)

        val command = CreateDesignerCommand(
            shopId = otherShop.id.value.toString(),
            ownerId = ownerId.value.toString(),
            name = "김디자이너",
            nickname = null,
            intro = null,
            photoUrls = null
        )

        assertFailsWith<UnauthorizedBeautishopAccessException> {
            useCase.create(command)
        }
    }

    @Test
    fun `should throw InvalidDesignerNameException when name is blank`() {
        val ownerId = OwnerId.new()
        val shop = createBeautishop()

        whenever(beautishopPort.findByOwnerId(ownerId)).thenReturn(listOf(shop))

        val command = CreateDesignerCommand(
            shopId = shop.id.value.toString(),
            ownerId = ownerId.value.toString(),
            name = "   ",
            nickname = null,
            intro = null,
            photoUrls = null
        )

        assertFailsWith<InvalidDesignerNameException> {
            useCase.create(command)
        }
    }

    @Test
    fun `should throw InvalidDesignerPhotosException when too many photos`() {
        val ownerId = OwnerId.new()
        val shop = createBeautishop()

        whenever(beautishopPort.findByOwnerId(ownerId)).thenReturn(listOf(shop))

        val command = CreateDesignerCommand(
            shopId = shop.id.value.toString(),
            ownerId = ownerId.value.toString(),
            name = "김디자이너",
            nickname = null,
            intro = null,
            photoUrls = (1..6).map { "https://example.com/$it.jpg" }
        )

        assertFailsWith<InvalidDesignerPhotosException> {
            useCase.create(command)
        }
    }
}
