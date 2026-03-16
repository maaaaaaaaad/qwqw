package com.mad.jellomarkserver.treatment.adapter.driven.persistence.repository

import com.mad.jellomarkserver.beautishop.core.domain.model.ShopId
import com.mad.jellomarkserver.treatment.adapter.driven.persistence.TreatmentPersistenceAdapter
import com.mad.jellomarkserver.treatment.adapter.driven.persistence.entity.TreatmentJpaEntity
import com.mad.jellomarkserver.treatment.adapter.driven.persistence.mapper.TreatmentMapper
import com.mad.jellomarkserver.treatment.core.domain.model.*
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
class TreatmentPersistenceAdapterTest {

    @Mock
    private lateinit var repository: TreatmentJpaRepository

    @Mock
    private lateinit var mapper: TreatmentMapper

    private lateinit var adapter: TreatmentPersistenceAdapter

    @BeforeEach
    fun setup() {
        adapter = TreatmentPersistenceAdapter(repository, mapper)
    }

    @Test
    fun `should find treatments by ids`() {
        val id1 = TreatmentId.new()
        val id2 = TreatmentId.new()
        val entity1 = createEntity(id1.value)
        val entity2 = createEntity(id2.value)
        val treatment1 = createTreatment(id1)
        val treatment2 = createTreatment(id2)

        `when`(repository.findAllById(listOf(id1.value, id2.value)))
            .thenReturn(listOf(entity1, entity2))
        `when`(mapper.toDomain(entity1)).thenReturn(treatment1)
        `when`(mapper.toDomain(entity2)).thenReturn(treatment2)

        val result = adapter.findByIds(listOf(id1, id2))

        assertEquals(2, result.size)
        assertEquals(treatment1, result[0])
        assertEquals(treatment2, result[1])
        verify(repository).findAllById(listOf(id1.value, id2.value))
    }

    @Test
    fun `should return empty list when ids is empty`() {
        val result = adapter.findByIds(emptyList())

        assertTrue(result.isEmpty())
    }

    private fun createEntity(id: UUID = UUID.randomUUID()): TreatmentJpaEntity {
        return TreatmentJpaEntity(
            id = id,
            shopId = UUID.randomUUID(),
            name = "Test Treatment",
            price = 30000,
            duration = 60,
            description = null,
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
    }

    private fun createTreatment(id: TreatmentId = TreatmentId.new()): Treatment {
        return Treatment.reconstruct(
            id = id,
            shopId = ShopId.new(),
            name = TreatmentName.of("Test Treatment"),
            price = TreatmentPrice.of(30000),
            duration = TreatmentDuration.of(60),
            description = null,
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
    }
}
