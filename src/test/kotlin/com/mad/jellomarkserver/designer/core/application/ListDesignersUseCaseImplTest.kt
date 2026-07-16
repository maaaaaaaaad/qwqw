package com.mad.jellomarkserver.designer.core.application

import com.mad.jellomarkserver.beautishop.core.domain.model.ShopId
import com.mad.jellomarkserver.designer.core.domain.model.*
import com.mad.jellomarkserver.designer.port.driven.DesignerPort
import com.mad.jellomarkserver.designer.port.driving.ListDesignersCommand
import com.mad.jellomarkserver.designer.port.driving.ListDesignersUseCase
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import java.time.Instant

@ExtendWith(MockitoExtension::class)
class ListDesignersUseCaseImplTest {

    @Mock
    private lateinit var designerPort: DesignerPort

    private lateinit var useCase: ListDesignersUseCase

    @BeforeEach
    fun setup() {
        useCase = ListDesignersUseCaseImpl(designerPort)
    }

    private fun createDesigner(shopId: ShopId, name: String): Designer {
        return Designer.reconstruct(
            id = DesignerId.new(),
            shopId = shopId,
            name = DesignerName.of(name),
            nickname = null,
            intro = null,
            photoUrls = DesignerPhotos.empty(),
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
    }

    @Test
    fun `should return list of designers for shop`() {
        val shopId = ShopId.new()
        val d1 = createDesigner(shopId, "김디자이너")
        val d2 = createDesigner(shopId, "박디자이너")

        whenever(designerPort.findByShopId(shopId)).thenReturn(listOf(d1, d2))

        val command = ListDesignersCommand(shopId = shopId.value.toString())
        val result = useCase.execute(command)

        assertEquals(2, result.size)
        assertEquals("김디자이너", result[0].name.value)
        assertEquals("박디자이너", result[1].name.value)
    }

    @Test
    fun `should return empty list when shop has no designers`() {
        val shopId = ShopId.new()

        whenever(designerPort.findByShopId(shopId)).thenReturn(emptyList())

        val command = ListDesignersCommand(shopId = shopId.value.toString())
        val result = useCase.execute(command)

        assertTrue(result.isEmpty())
    }
}
