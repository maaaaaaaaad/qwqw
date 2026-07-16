package com.mad.jellomarkserver.designer.core.application

import com.mad.jellomarkserver.beautishop.core.domain.model.*
import com.mad.jellomarkserver.beautishop.port.driven.BeautishopPort
import com.mad.jellomarkserver.designer.core.domain.exception.DesignerNotFoundException
import com.mad.jellomarkserver.designer.core.domain.exception.UnauthorizedDesignerAccessException
import com.mad.jellomarkserver.designer.core.domain.model.*
import com.mad.jellomarkserver.designer.port.driven.DesignerPort
import com.mad.jellomarkserver.designer.port.driving.DeleteDesignerCommand
import com.mad.jellomarkserver.designer.port.driving.DeleteDesignerUseCase
import com.mad.jellomarkserver.owner.core.domain.model.OwnerId
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.Instant
import kotlin.test.assertFailsWith

@ExtendWith(MockitoExtension::class)
class DeleteDesignerUseCaseImplTest {

    @Mock
    private lateinit var designerPort: DesignerPort

    @Mock
    private lateinit var beautishopPort: BeautishopPort

    private lateinit var useCase: DeleteDesignerUseCase

    @BeforeEach
    fun setup() {
        useCase = DeleteDesignerUseCaseImpl(designerPort, beautishopPort)
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
            nickname = null,
            intro = null,
            photoUrls = DesignerPhotos.empty(),
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
    }

    @Test
    fun `should delete designer successfully`() {
        val ownerId = OwnerId.new()
        val shop = createBeautishop()
        val designer = createDesigner(shop.id)

        whenever(designerPort.findById(designer.id)).thenReturn(designer)
        whenever(beautishopPort.findByOwnerId(ownerId)).thenReturn(listOf(shop))

        val command = DeleteDesignerCommand(
            designerId = designer.id.value.toString(),
            ownerId = ownerId.value.toString()
        )

        useCase.delete(command)

        verify(designerPort).delete(designer.id)
    }

    @Test
    fun `should throw DesignerNotFoundException when designer does not exist`() {
        val ownerId = OwnerId.new()
        val designerId = DesignerId.new()

        whenever(designerPort.findById(designerId)).thenReturn(null)

        val command = DeleteDesignerCommand(
            designerId = designerId.value.toString(),
            ownerId = ownerId.value.toString()
        )

        assertFailsWith<DesignerNotFoundException> {
            useCase.delete(command)
        }
    }

    @Test
    fun `should throw UnauthorizedDesignerAccessException when owner does not own the shop`() {
        val ownerId = OwnerId.new()
        val otherShop = createBeautishop()
        val designer = createDesigner(otherShop.id)

        whenever(designerPort.findById(designer.id)).thenReturn(designer)
        whenever(beautishopPort.findByOwnerId(ownerId)).thenReturn(emptyList())

        val command = DeleteDesignerCommand(
            designerId = designer.id.value.toString(),
            ownerId = ownerId.value.toString()
        )

        assertFailsWith<UnauthorizedDesignerAccessException> {
            useCase.delete(command)
        }
    }
}
