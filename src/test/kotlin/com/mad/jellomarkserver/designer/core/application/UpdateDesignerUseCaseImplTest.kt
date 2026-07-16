package com.mad.jellomarkserver.designer.core.application

import com.mad.jellomarkserver.beautishop.core.domain.model.*
import com.mad.jellomarkserver.beautishop.port.driven.BeautishopPort
import com.mad.jellomarkserver.designer.core.domain.exception.DesignerNotFoundException
import com.mad.jellomarkserver.designer.core.domain.exception.UnauthorizedDesignerAccessException
import com.mad.jellomarkserver.designer.core.domain.model.*
import com.mad.jellomarkserver.designer.port.driven.DesignerPort
import com.mad.jellomarkserver.designer.port.driving.UpdateDesignerCommand
import com.mad.jellomarkserver.designer.port.driving.UpdateDesignerUseCase
import com.mad.jellomarkserver.owner.core.domain.model.OwnerId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import java.time.Instant
import kotlin.test.assertFailsWith

@ExtendWith(MockitoExtension::class)
class UpdateDesignerUseCaseImplTest {

    @Mock
    private lateinit var designerPort: DesignerPort

    @Mock
    private lateinit var beautishopPort: BeautishopPort

    private lateinit var useCase: UpdateDesignerUseCase

    @BeforeEach
    fun setup() {
        useCase = UpdateDesignerUseCaseImpl(designerPort, beautishopPort)
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

    private fun createDesigner(shopId: ShopId): Designer {
        return Designer.reconstruct(
            id = DesignerId.new(),
            shopId = shopId,
            name = DesignerName.of("김디자이너"),
            nickname = DesignerNickname.of("네일요정"),
            intro = DesignerIntro.of("소개"),
            photoUrls = DesignerPhotos.empty(),
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
    }

    @Test
    fun `should update designer name only`() {
        val ownerId = OwnerId.new()
        val shop = createBeautishop()
        val designer = createDesigner(shop.id)

        whenever(designerPort.findById(designer.id)).thenReturn(designer)
        whenever(beautishopPort.findByOwnerId(ownerId)).thenReturn(listOf(shop))
        whenever(designerPort.save(any())).thenAnswer { it.arguments[0] as Designer }

        val command = UpdateDesignerCommand(
            designerId = designer.id.value.toString(),
            ownerId = ownerId.value.toString(),
            name = "박디자이너",
            nickname = null,
            intro = null,
            photoUrls = null
        )

        val result = useCase.update(command)

        assertEquals("박디자이너", result.name.value)
        assertEquals("네일요정", result.nickname?.value)
        assertEquals("소개", result.intro?.value)
    }

    @Test
    fun `should update all fields when provided`() {
        val ownerId = OwnerId.new()
        val shop = createBeautishop()
        val designer = createDesigner(shop.id)

        whenever(designerPort.findById(designer.id)).thenReturn(designer)
        whenever(beautishopPort.findByOwnerId(ownerId)).thenReturn(listOf(shop))
        whenever(designerPort.save(any())).thenAnswer { it.arguments[0] as Designer }

        val command = UpdateDesignerCommand(
            designerId = designer.id.value.toString(),
            ownerId = ownerId.value.toString(),
            name = "박디자이너",
            nickname = "새닉네임",
            intro = "새 소개",
            photoUrls = listOf("https://example.com/new.jpg")
        )

        val result = useCase.update(command)

        assertEquals("박디자이너", result.name.value)
        assertEquals("새닉네임", result.nickname?.value)
        assertEquals("새 소개", result.intro?.value)
        assertEquals(1, result.photoUrls.size)
    }

    @Test
    fun `should throw DesignerNotFoundException when designer does not exist`() {
        val ownerId = OwnerId.new()
        val designerId = DesignerId.new()

        whenever(designerPort.findById(designerId)).thenReturn(null)

        val command = UpdateDesignerCommand(
            designerId = designerId.value.toString(),
            ownerId = ownerId.value.toString(),
            name = "박디자이너",
            nickname = null,
            intro = null,
            photoUrls = null
        )

        assertFailsWith<DesignerNotFoundException> {
            useCase.update(command)
        }
    }

    @Test
    fun `should throw UnauthorizedDesignerAccessException when owner does not own the shop`() {
        val ownerId = OwnerId.new()
        val otherShop = createBeautishop()
        val designer = createDesigner(otherShop.id)

        whenever(designerPort.findById(designer.id)).thenReturn(designer)
        whenever(beautishopPort.findByOwnerId(ownerId)).thenReturn(emptyList())

        val command = UpdateDesignerCommand(
            designerId = designer.id.value.toString(),
            ownerId = ownerId.value.toString(),
            name = "박디자이너",
            nickname = null,
            intro = null,
            photoUrls = null
        )

        assertFailsWith<UnauthorizedDesignerAccessException> {
            useCase.update(command)
        }
    }

    @Test
    fun `update with all nulls should keep existing values`() {
        val ownerId = OwnerId.new()
        val shop = createBeautishop()
        val designer = createDesigner(shop.id)

        whenever(designerPort.findById(designer.id)).thenReturn(designer)
        whenever(beautishopPort.findByOwnerId(ownerId)).thenReturn(listOf(shop))
        whenever(designerPort.save(any())).thenAnswer { it.arguments[0] as Designer }

        val command = UpdateDesignerCommand(
            designerId = designer.id.value.toString(),
            ownerId = ownerId.value.toString(),
            name = null,
            nickname = null,
            intro = null,
            photoUrls = null
        )

        val result = useCase.update(command)

        assertEquals(designer.name, result.name)
        assertEquals(designer.nickname, result.nickname)
        assertEquals(designer.intro, result.intro)
    }
}
