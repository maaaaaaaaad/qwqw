package com.mad.jellomarkserver.owner.adapter.driven.persistence.mapper

import com.mad.jellomarkserver.owner.adapter.driven.persistence.entity.OwnerJpaEntity
import com.mad.jellomarkserver.owner.core.domain.exception.InvalidOwnerBusinessNumberException
import com.mad.jellomarkserver.owner.core.domain.exception.InvalidOwnerPhoneNumberException
import com.mad.jellomarkserver.owner.core.domain.model.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.Instant
import java.util.*
import kotlin.test.assertFailsWith

class OwnerMapperImplTest {

    private val ownerMapper = OwnerMapperImpl()

    @Test
    fun `should correctly map OwnerJpaEntity to Owner`() {
        val id = UUID.randomUUID()
        val businessNumber = "123456789"
        val phoneNumber = "010-1234-5678"
        val createdAt = Instant.parse("2025-01-01T00:00:00Z")
        val updatedAt = Instant.parse("2025-01-01T00:00:00Z")
        val ownerJpaEntity = OwnerJpaEntity(
            id = id,
            businessNumber = businessNumber,
            phoneNumber = phoneNumber,
            nickname = "test",
            email = "test@example.com",
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        val result = ownerMapper.toDomain(ownerJpaEntity)

        assertEquals(OwnerId.from(id), result.id)
        assertEquals(BusinessNumber.of(businessNumber), result.businessNumber)
        assertEquals(OwnerPhoneNumber.of(phoneNumber), result.ownerPhoneNumber)
        assertEquals(createdAt, result.createdAt)
        assertEquals(updatedAt, result.updatedAt)
    }

    @Test
    fun `should correctly map OwnerJpaEntity with edge case values`() {
        val id = UUID.randomUUID()
        val businessNumber = "999999999"
        val phoneNumber = "02-1234-5678"
        val createdAt = Instant.parse("2000-01-01T00:00:00Z")
        val updatedAt = Instant.parse("2025-01-01T00:00:00Z")
        val ownerJpaEntity = OwnerJpaEntity(
            id = id,
            businessNumber = businessNumber,
            phoneNumber = phoneNumber,
            nickname = "test",
            email = "test@example.com",
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        val result = ownerMapper.toDomain(ownerJpaEntity)

        assertEquals(OwnerId.from(id), result.id)
        assertEquals(BusinessNumber.of(businessNumber), result.businessNumber)
        assertEquals(OwnerPhoneNumber.of(phoneNumber), result.ownerPhoneNumber)
        assertEquals(createdAt, result.createdAt)
        assertEquals(updatedAt, result.updatedAt)
    }

    @Test
    fun `should correctly map OwnerJpaEntity with various phone number formats`() {
        val id = UUID.randomUUID()
        val businessNumber = "111222333"
        val phoneNumber = "011-123-4567"
        val createdAt = Instant.EPOCH
        val updatedAt = Instant.EPOCH
        val ownerJpaEntity = OwnerJpaEntity(
            id = id,
            businessNumber = businessNumber,
            phoneNumber = phoneNumber,
            nickname = "test",
            email = "test@example.com",
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        val result = ownerMapper.toDomain(ownerJpaEntity)

        assertEquals(OwnerId.from(id), result.id)
        assertEquals(BusinessNumber.of(businessNumber), result.businessNumber)
        assertEquals(OwnerPhoneNumber.of(phoneNumber), result.ownerPhoneNumber)
        assertEquals(createdAt, result.createdAt)
        assertEquals(updatedAt, result.updatedAt)
    }

    @Test
    fun `should correctly map Owner to OwnerJpaEntity`() {
        val id = OwnerId.from(UUID.randomUUID())
        val businessNumber = BusinessNumber.of("123456789")
        val ownerPhoneNumber = OwnerPhoneNumber.of("010-1234-5678")
        val createdAt = Instant.parse("2024-01-01T00:00:00Z")
        val updatedAt = Instant.parse("2024-06-01T00:00:00Z")

        val domain = Owner.reconstruct(
            id,
            businessNumber,
            ownerPhoneNumber,
            OwnerNickname.of("test"),
            OwnerEmail.of("test@example.com"),
            createdAt,
            updatedAt
        )

        val entity = ownerMapper.toEntity(domain)

        assertEquals(id.value, entity.id)
        assertEquals(businessNumber.value, entity.businessNumber)
        assertEquals(ownerPhoneNumber.value, entity.phoneNumber)
        assertEquals(createdAt, entity.createdAt)
        assertEquals(updatedAt, entity.updatedAt)
    }

    @Test
    fun `domain - entity - domain round-trip should keep values`() {
        val id = OwnerId.from(UUID.randomUUID())
        val businessNumber = BusinessNumber.of("987654321")
        val ownerPhoneNumber = OwnerPhoneNumber.of("010-9876-5432")
        val createdAt = Instant.parse("2020-01-01T00:00:00Z")
        val updatedAt = Instant.parse("2021-01-01T00:00:00Z")
        val original = Owner.reconstruct(
            id,
            businessNumber,
            ownerPhoneNumber,
            OwnerNickname.of("test"),
            OwnerEmail.of("test@example.com"),
            createdAt,
            updatedAt
        )

        val entity = ownerMapper.toEntity(original)
        val roundTripped = ownerMapper.toDomain(entity)

        assertEquals(original.id, roundTripped.id)
        assertEquals(original.businessNumber, roundTripped.businessNumber)
        assertEquals(original.ownerPhoneNumber, roundTripped.ownerPhoneNumber)
        assertEquals(original.createdAt, roundTripped.createdAt)
        assertEquals(original.updatedAt, roundTripped.updatedAt)
    }

    @Test
    fun `should trim businessNumber and phoneNumber when mapping`() {
        val entity = OwnerJpaEntity(
            id = UUID.randomUUID(),
            businessNumber = "  123456789  ",
            phoneNumber = "  010-1234-5678  ",
            nickname = "test",
            email = "test@example.com",
            createdAt = Instant.parse("2020-01-01T00:00:00Z"),
            updatedAt = Instant.parse("2020-01-02T00:00:00Z")
        )

        val domain = ownerMapper.toDomain(entity)

        assertEquals("123456789", domain.businessNumber.value)
        assertEquals("010-1234-5678", domain.ownerPhoneNumber.value)
    }

    @Test
    fun `should throw when businessNumber is invalid - too short`() {
        val entity = OwnerJpaEntity(
            id = UUID.randomUUID(),
            businessNumber = "12345678",
            phoneNumber = "010-1234-5678",
            nickname = "test",
            email = "test@example.com",
            createdAt = Instant.EPOCH,
            updatedAt = Instant.EPOCH
        )

        assertFailsWith<InvalidOwnerBusinessNumberException> {
            ownerMapper.toDomain(entity)
        }
    }

    @Test
    fun `should throw when businessNumber is invalid - too long`() {
        val entity = OwnerJpaEntity(
            id = UUID.randomUUID(),
            businessNumber = "1234567890",
            phoneNumber = "010-1234-5678",
            nickname = "test",
            email = "test@example.com",
            createdAt = Instant.EPOCH,
            updatedAt = Instant.EPOCH
        )

        assertFailsWith<InvalidOwnerBusinessNumberException> {
            ownerMapper.toDomain(entity)
        }
    }

    @Test
    fun `should throw when phoneNumber is invalid`() {
        val entity = OwnerJpaEntity(
            id = UUID.randomUUID(),
            businessNumber = "123456789",
            phoneNumber = "invalid-phone",
            nickname = "test",
            email = "test@example.com",
            createdAt = Instant.EPOCH,
            updatedAt = Instant.EPOCH
        )

        assertFailsWith<InvalidOwnerPhoneNumberException> {
            ownerMapper.toDomain(entity)
        }
    }

    @Test
    fun `should throw when phoneNumber format is incorrect`() {
        val entity = OwnerJpaEntity(
            id = UUID.randomUUID(),
            businessNumber = "123456789",
            phoneNumber = "01012345678",
            nickname = "test",
            email = "test@example.com",
            createdAt = Instant.EPOCH,
            updatedAt = Instant.EPOCH
        )

        assertFailsWith<InvalidOwnerPhoneNumberException> {
            ownerMapper.toDomain(entity)
        }
    }
}
