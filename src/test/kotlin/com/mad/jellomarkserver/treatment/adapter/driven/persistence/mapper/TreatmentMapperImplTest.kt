package com.mad.jellomarkserver.treatment.adapter.driven.persistence.mapper

import com.mad.jellomarkserver.beautishop.core.domain.model.ShopId
import com.mad.jellomarkserver.treatment.adapter.driven.persistence.entity.TreatmentJpaEntity
import com.mad.jellomarkserver.treatment.core.domain.model.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.time.Instant
import java.util.*

class TreatmentMapperImplTest {

    private val mapper = TreatmentMapperImpl()

    @Test
    fun `should correctly map TreatmentJpaEntity to Treatment`() {
        val id = UUID.randomUUID()
        val shopId = UUID.randomUUID()
        val createdAt = Instant.parse("2025-01-01T00:00:00Z")
        val updatedAt = Instant.parse("2025-01-01T00:00:00Z")
        val entity = TreatmentJpaEntity(
            id = id,
            shopId = shopId,
            name = "젤네일",
            price = 50000,
            duration = 60,
            description = "기본 젤네일 시술입니다",
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        val result = mapper.toDomain(entity)

        assertEquals(TreatmentId.from(id), result.id)
        assertEquals(ShopId.from(shopId), result.shopId)
        assertEquals(TreatmentName.of("젤네일"), result.name)
        assertEquals(TreatmentPrice.of(50000), result.price)
        assertEquals(TreatmentDuration.of(60), result.duration)
        assertEquals(TreatmentDescription.of("기본 젤네일 시술입니다"), result.description)
        assertEquals(createdAt, result.createdAt)
        assertEquals(updatedAt, result.updatedAt)
    }

    @Test
    fun `should correctly map TreatmentJpaEntity with null description`() {
        val id = UUID.randomUUID()
        val shopId = UUID.randomUUID()
        val createdAt = Instant.parse("2025-01-01T00:00:00Z")
        val updatedAt = Instant.parse("2025-01-01T00:00:00Z")
        val entity = TreatmentJpaEntity(
            id = id,
            shopId = shopId,
            name = "젤네일",
            price = 50000,
            duration = 60,
            description = null,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        val result = mapper.toDomain(entity)

        assertNull(result.description)
    }

    @Test
    fun `should correctly map Treatment to TreatmentJpaEntity`() {
        val shopId = ShopId.new()
        val createdAt = Instant.parse("2025-01-01T00:00:00Z")
        val updatedAt = Instant.parse("2025-01-01T00:00:00Z")

        val domain = Treatment.reconstruct(
            id = TreatmentId.new(),
            shopId = shopId,
            name = TreatmentName.of("젤네일"),
            price = TreatmentPrice.of(50000),
            duration = TreatmentDuration.of(60),
            description = TreatmentDescription.of("기본 젤네일 시술입니다"),
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        val entity = mapper.toEntity(domain)

        assertEquals(domain.id.value, entity.id)
        assertEquals(shopId.value, entity.shopId)
        assertEquals("젤네일", entity.name)
        assertEquals(50000, entity.price)
        assertEquals(60, entity.duration)
        assertEquals("기본 젤네일 시술입니다", entity.description)
        assertEquals(createdAt, entity.createdAt)
        assertEquals(updatedAt, entity.updatedAt)
    }

    @Test
    fun `should correctly map Treatment with null description to TreatmentJpaEntity`() {
        val shopId = ShopId.new()
        val createdAt = Instant.parse("2025-01-01T00:00:00Z")
        val updatedAt = Instant.parse("2025-01-01T00:00:00Z")

        val domain = Treatment.reconstruct(
            id = TreatmentId.new(),
            shopId = shopId,
            name = TreatmentName.of("젤네일"),
            price = TreatmentPrice.of(50000),
            duration = TreatmentDuration.of(60),
            description = null,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        val entity = mapper.toEntity(domain)

        assertNull(entity.description)
    }

    @Test
    fun `domain - entity - domain round-trip should keep values`() {
        val shopId = ShopId.new()
        val createdAt = Instant.parse("2025-01-01T00:00:00Z")
        val updatedAt = Instant.parse("2025-01-01T00:00:00Z")

        val original = Treatment.reconstruct(
            id = TreatmentId.new(),
            shopId = shopId,
            name = TreatmentName.of("젤네일"),
            price = TreatmentPrice.of(50000),
            duration = TreatmentDuration.of(60),
            description = TreatmentDescription.of("기본 젤네일 시술입니다"),
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        val entity = mapper.toEntity(original)
        val roundTripped = mapper.toDomain(entity)

        assertEquals(original.id, roundTripped.id)
        assertEquals(original.shopId, roundTripped.shopId)
        assertEquals(original.name, roundTripped.name)
        assertEquals(original.price, roundTripped.price)
        assertEquals(original.duration, roundTripped.duration)
        assertEquals(original.description, roundTripped.description)
        assertEquals(original.createdAt, roundTripped.createdAt)
        assertEquals(original.updatedAt, roundTripped.updatedAt)
    }
}
