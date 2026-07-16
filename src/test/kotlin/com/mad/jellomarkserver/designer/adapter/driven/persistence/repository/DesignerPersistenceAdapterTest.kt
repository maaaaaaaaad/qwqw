package com.mad.jellomarkserver.designer.adapter.driven.persistence.repository

import com.mad.jellomarkserver.beautishop.core.domain.model.ShopId
import com.mad.jellomarkserver.designer.adapter.driven.persistence.DesignerPersistenceAdapter
import com.mad.jellomarkserver.designer.adapter.driven.persistence.entity.DesignerJpaEntity
import com.mad.jellomarkserver.designer.adapter.driven.persistence.mapper.DesignerMapper
import com.mad.jellomarkserver.designer.core.domain.model.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.time.Instant
import java.util.*

@ExtendWith(MockitoExtension::class)
class DesignerPersistenceAdapterTest {

    @Mock
    private lateinit var repository: DesignerJpaRepository

    @Mock
    private lateinit var mapper: DesignerMapper

    private lateinit var adapter: DesignerPersistenceAdapter

    @BeforeEach
    fun setup() {
        adapter = DesignerPersistenceAdapter(repository, mapper)
    }

    private fun createEntity(id: UUID = UUID.randomUUID()): DesignerJpaEntity {
        return DesignerJpaEntity(
            id = id,
            shopId = UUID.randomUUID(),
            name = "Test",
            nickname = null,
            intro = null,
            photoUrls = null,
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
    }

    private fun createDesigner(id: DesignerId = DesignerId.new()): Designer {
        return Designer.reconstruct(
            id = id,
            shopId = ShopId.new(),
            name = DesignerName.of("Test"),
            nickname = null,
            intro = null,
            photoUrls = DesignerPhotos.empty(),
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
    }

    @Test
    fun `should find designers by ids`() {
        val id1 = DesignerId.new()
        val id2 = DesignerId.new()
        val entity1 = createEntity(id1.value)
        val entity2 = createEntity(id2.value)
        val designer1 = createDesigner(id1)
        val designer2 = createDesigner(id2)

        `when`(repository.findAllById(listOf(id1.value, id2.value)))
            .thenReturn(listOf(entity1, entity2))
        `when`(mapper.toDomain(entity1)).thenReturn(designer1)
        `when`(mapper.toDomain(entity2)).thenReturn(designer2)

        val result = adapter.findByIds(listOf(id1, id2))

        assertEquals(2, result.size)
        assertEquals(designer1, result[0])
        assertEquals(designer2, result[1])
        verify(repository).findAllById(listOf(id1.value, id2.value))
    }

    @Test
    fun `should return empty list when ids is empty`() {
        val result = adapter.findByIds(emptyList())

        assertTrue(result.isEmpty())
    }

    @Test
    fun `should find designers by shopId`() {
        val shopId = ShopId.new()
        val entity = createEntity()
        val designer = createDesigner()

        `when`(repository.findByShopId(shopId.value)).thenReturn(listOf(entity))
        `when`(mapper.toDomain(entity)).thenReturn(designer)

        val result = adapter.findByShopId(shopId)

        assertEquals(1, result.size)
        assertEquals(designer, result[0])
    }

    @Test
    fun `delete should delegate to repository`() {
        val id = DesignerId.new()

        adapter.delete(id)

        verify(repository).deleteById(id.value)
    }
}
