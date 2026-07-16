package com.mad.jellomarkserver.designer.core.application

import com.mad.jellomarkserver.beautishop.core.domain.model.ShopId
import com.mad.jellomarkserver.designer.core.domain.exception.DesignerNotFoundException
import com.mad.jellomarkserver.designer.core.domain.model.*
import com.mad.jellomarkserver.designer.port.driven.DesignerPort
import com.mad.jellomarkserver.designer.port.driving.GetDesignerCommand
import com.mad.jellomarkserver.designer.port.driving.GetDesignerUseCase
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import java.time.Instant
import kotlin.test.assertFailsWith

@ExtendWith(MockitoExtension::class)
class GetDesignerUseCaseImplTest {

    @Mock
    private lateinit var designerPort: DesignerPort

    private lateinit var useCase: GetDesignerUseCase

    @BeforeEach
    fun setup() {
        useCase = GetDesignerUseCaseImpl(designerPort)
    }

    private fun createDesigner(): Designer {
        return Designer.reconstruct(
            id = DesignerId.new(),
            shopId = ShopId.new(),
            name = DesignerName.of("김디자이너"),
            nickname = null,
            intro = null,
            photoUrls = DesignerPhotos.empty(),
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
    }

    @Test
    fun `should return designer when it exists`() {
        val designer = createDesigner()

        whenever(designerPort.findById(designer.id)).thenReturn(designer)

        val command = GetDesignerCommand(designerId = designer.id.value.toString())
        val result = useCase.execute(command)

        assertEquals(designer.id, result.id)
        assertEquals(designer.name, result.name)
    }

    @Test
    fun `should throw DesignerNotFoundException when designer does not exist`() {
        val designerId = DesignerId.new()

        whenever(designerPort.findById(designerId)).thenReturn(null)

        val command = GetDesignerCommand(designerId = designerId.value.toString())

        assertFailsWith<DesignerNotFoundException> {
            useCase.execute(command)
        }
    }
}
