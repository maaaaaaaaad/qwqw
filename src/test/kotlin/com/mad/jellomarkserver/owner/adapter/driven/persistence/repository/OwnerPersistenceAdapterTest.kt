package com.mad.jellomarkserver.owner.adapter.driven.persistence.repository

import com.mad.jellomarkserver.common.persistence.ConstraintViolationTranslator
import com.mad.jellomarkserver.common.persistence.ConstraintViolationTranslatorImpl
import com.mad.jellomarkserver.owner.adapter.driven.persistence.entity.OwnerJpaEntity
import com.mad.jellomarkserver.owner.adapter.driven.persistence.mapper.OwnerMapper
import com.mad.jellomarkserver.owner.core.domain.exception.DuplicateOwnerBusinessNumberException
import com.mad.jellomarkserver.owner.core.domain.exception.DuplicateOwnerPhoneNumberException
import com.mad.jellomarkserver.owner.core.domain.exception.InvalidOwnerBusinessNumberException
import com.mad.jellomarkserver.owner.core.domain.model.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.dao.DataIntegrityViolationException
import java.time.Instant
import java.util.*
import kotlin.test.assertFailsWith

@ExtendWith(MockitoExtension::class)
class OwnerPersistenceAdapterTest {

    @Mock
    private lateinit var jpaRepository: OwnerJpaRepository

    @Mock
    private lateinit var mapper: OwnerMapper

    private val constraintTranslator: ConstraintViolationTranslator = ConstraintViolationTranslatorImpl()

    private lateinit var adapter: OwnerPersistenceAdapter

    @org.junit.jupiter.api.BeforeEach
    fun setup() {
        adapter = OwnerPersistenceAdapter(jpaRepository, mapper, constraintTranslator)
    }

    @Test
    fun `should save owner successfully`() {
        val id = OwnerId.from(UUID.randomUUID())
        val businessNumber = BusinessNumber.of("1234567890")
        val ownerPhoneNumber = OwnerPhoneNumber.of("010-1234-5678")
        val createdAt = Instant.parse("2025-01-01T00:00:00Z")
        val updatedAt = Instant.parse("2025-01-01T00:00:00Z")
        val owner = Owner.reconstruct(
            id,
            businessNumber,
            ownerPhoneNumber,
            OwnerNickname.of("test"),
            OwnerEmail.of("test@example.com"),
            createdAt,
            updatedAt
        )

        val entity = OwnerJpaEntity(
            id = id.value,
            businessNumber = businessNumber.value,
            phoneNumber = ownerPhoneNumber.value,
            email = "test@example.com",
            nickname = "test",
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        `when`(mapper.toEntity(owner)).thenReturn(entity)
        `when`(jpaRepository.saveAndFlush(entity)).thenReturn(entity)
        `when`(mapper.toDomain(entity)).thenReturn(owner)

        val result = adapter.save(owner)

        assertEquals(owner, result)
        verify(mapper).toEntity(owner)
        verify(jpaRepository).saveAndFlush(entity)
        verify(mapper).toDomain(entity)
    }

    @Test
    fun `should save owner with valid business number`() {
        val id = OwnerId.from(UUID.randomUUID())
        val businessNumber = BusinessNumber.of("9876543210")
        val ownerPhoneNumber = OwnerPhoneNumber.of("010-9876-5432")
        val createdAt = Instant.EPOCH
        val updatedAt = Instant.EPOCH
        val owner = Owner.reconstruct(
            id,
            businessNumber,
            ownerPhoneNumber,
            OwnerNickname.of("test"),
            OwnerEmail.of("test@example.com"),
            createdAt,
            updatedAt
        )

        val entity = OwnerJpaEntity(
            id = id.value,
            businessNumber = businessNumber.value,
            phoneNumber = ownerPhoneNumber.value,
            email = "test@example.com",
            nickname = "test",
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        `when`(mapper.toEntity(owner)).thenReturn(entity)
        `when`(jpaRepository.saveAndFlush(entity)).thenReturn(entity)
        `when`(mapper.toDomain(entity)).thenReturn(owner)

        val result = adapter.save(owner)

        assertEquals(owner, result)
    }

    @Test
    fun `should save owner with all zeros business number`() {
        val id = OwnerId.from(UUID.randomUUID())
        val businessNumber = BusinessNumber.of("0000000000")
        val ownerPhoneNumber = OwnerPhoneNumber.of("010-0000-0000")
        val createdAt = Instant.parse("2099-12-31T23:59:59Z")
        val updatedAt = Instant.parse("2099-12-31T23:59:59Z")
        val owner = Owner.reconstruct(
            id,
            businessNumber,
            ownerPhoneNumber,
            OwnerNickname.of("test"),
            OwnerEmail.of("test@example.com"),
            createdAt,
            updatedAt
        )

        val entity = OwnerJpaEntity(
            id = id.value,
            businessNumber = businessNumber.value,
            phoneNumber = ownerPhoneNumber.value,
            email = "test@example.com",
            nickname = "test",
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        `when`(mapper.toEntity(owner)).thenReturn(entity)
        `when`(jpaRepository.saveAndFlush(entity)).thenReturn(entity)
        `when`(mapper.toDomain(entity)).thenReturn(owner)

        val result = adapter.save(owner)

        assertEquals(owner, result)
    }

    @Test
    fun `should save owner with alphanumeric business number`() {
        val id = OwnerId.from(UUID.randomUUID())
        val businessNumber = BusinessNumber.of("1234509876")
        val ownerPhoneNumber = OwnerPhoneNumber.of("010-1234-5678")
        val createdAt = Instant.now()
        val updatedAt = Instant.now()
        val owner = Owner.reconstruct(
            id,
            businessNumber,
            ownerPhoneNumber,
            OwnerNickname.of("test"),
            OwnerEmail.of("test@example.com"),
            createdAt,
            updatedAt
        )

        val entity = OwnerJpaEntity(
            id = id.value,
            businessNumber = businessNumber.value,
            phoneNumber = ownerPhoneNumber.value,
            email = "test@example.com",
            nickname = "test",
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        `when`(mapper.toEntity(owner)).thenReturn(entity)
        `when`(jpaRepository.saveAndFlush(entity)).thenReturn(entity)
        `when`(mapper.toDomain(entity)).thenReturn(owner)

        val result = adapter.save(owner)

        assertEquals(owner, result)
    }

    @Test
    fun `should save owner with mobile phone number`() {
        val id = OwnerId.from(UUID.randomUUID())
        val businessNumber = BusinessNumber.of("1111111110")
        val ownerPhoneNumber = OwnerPhoneNumber.of("010-1111-1111")
        val createdAt = Instant.now()
        val updatedAt = Instant.now()
        val owner = Owner.reconstruct(
            id,
            businessNumber,
            ownerPhoneNumber,
            OwnerNickname.of("test"),
            OwnerEmail.of("test@example.com"),
            createdAt,
            updatedAt
        )

        val entity = OwnerJpaEntity(
            id = id.value,
            businessNumber = businessNumber.value,
            phoneNumber = ownerPhoneNumber.value,
            email = "test@example.com",
            nickname = "test",
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        `when`(mapper.toEntity(owner)).thenReturn(entity)
        `when`(jpaRepository.saveAndFlush(entity)).thenReturn(entity)
        `when`(mapper.toDomain(entity)).thenReturn(owner)

        val result = adapter.save(owner)

        assertEquals(owner, result)
    }

    @Test
    fun `should save owner with Seoul area phone number`() {
        val id = OwnerId.from(UUID.randomUUID())
        val businessNumber = BusinessNumber.of("2222222220")
        val ownerPhoneNumber = OwnerPhoneNumber.of("02-1234-5678")
        val createdAt = Instant.now()
        val updatedAt = Instant.now()
        val owner = Owner.reconstruct(
            id,
            businessNumber,
            ownerPhoneNumber,
            OwnerNickname.of("test"),
            OwnerEmail.of("test@example.com"),
            createdAt,
            updatedAt
        )

        val entity = OwnerJpaEntity(
            id = id.value,
            businessNumber = businessNumber.value,
            phoneNumber = ownerPhoneNumber.value,
            email = "test@example.com",
            nickname = "test",
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        `when`(mapper.toEntity(owner)).thenReturn(entity)
        `when`(jpaRepository.saveAndFlush(entity)).thenReturn(entity)
        `when`(mapper.toDomain(entity)).thenReturn(owner)

        val result = adapter.save(owner)

        assertEquals(owner, result)
    }

    @Test
    fun `should save owner with regional phone number`() {
        val id = OwnerId.from(UUID.randomUUID())
        val businessNumber = BusinessNumber.of("3333333330")
        val ownerPhoneNumber = OwnerPhoneNumber.of("031-123-4567")
        val createdAt = Instant.now()
        val updatedAt = Instant.now()
        val owner = Owner.reconstruct(
            id,
            businessNumber,
            ownerPhoneNumber,
            OwnerNickname.of("test"),
            OwnerEmail.of("test@example.com"),
            createdAt,
            updatedAt
        )

        val entity = OwnerJpaEntity(
            id = id.value,
            businessNumber = businessNumber.value,
            phoneNumber = ownerPhoneNumber.value,
            email = "test@example.com",
            nickname = "test",
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        `when`(mapper.toEntity(owner)).thenReturn(entity)
        `when`(jpaRepository.saveAndFlush(entity)).thenReturn(entity)
        `when`(mapper.toDomain(entity)).thenReturn(owner)

        val result = adapter.save(owner)

        assertEquals(owner, result)
    }

    @Test
    fun `should save owner with different created and updated timestamps`() {
        val id = OwnerId.from(UUID.randomUUID())
        val businessNumber = BusinessNumber.of("4444444440")
        val ownerPhoneNumber = OwnerPhoneNumber.of("010-4444-4444")
        val createdAt = Instant.parse("2025-01-01T00:00:00Z")
        val updatedAt = Instant.parse("2025-06-01T12:30:45Z")
        val owner = Owner.reconstruct(
            id,
            businessNumber,
            ownerPhoneNumber,
            OwnerNickname.of("test"),
            OwnerEmail.of("test@example.com"),
            createdAt,
            updatedAt
        )

        val entity = OwnerJpaEntity(
            id = id.value,
            businessNumber = businessNumber.value,
            phoneNumber = ownerPhoneNumber.value,
            email = "test@example.com",
            nickname = "test",
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        `when`(mapper.toEntity(owner)).thenReturn(entity)
        `when`(jpaRepository.saveAndFlush(entity)).thenReturn(entity)
        `when`(mapper.toDomain(entity)).thenReturn(owner)

        val result = adapter.save(owner)

        assertEquals(owner, result)
    }

    @Test
    fun `should correctly handle round-trip with mapper`() {
        val id = OwnerId.from(UUID.randomUUID())
        val businessNumber = BusinessNumber.of("5555555550")
        val ownerPhoneNumber = OwnerPhoneNumber.of("010-5555-5555")
        val createdAt = Instant.parse("2025-01-01T00:00:00Z")
        val updatedAt = Instant.parse("2025-01-01T00:00:00Z")
        val originalOwner = Owner.reconstruct(
            id,
            businessNumber,
            ownerPhoneNumber,
            OwnerNickname.of("test"),
            OwnerEmail.of("test@example.com"),
            createdAt,
            updatedAt
        )

        val entity = OwnerJpaEntity(
            id = id.value,
            businessNumber = businessNumber.value,
            phoneNumber = ownerPhoneNumber.value,
            email = "test@example.com",
            nickname = "test",
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        val reconstructedOwner = Owner.reconstruct(
            OwnerId.from(entity.id),
            BusinessNumber.of(entity.businessNumber),
            OwnerPhoneNumber.of(entity.phoneNumber),
            OwnerNickname.of("test"),
            OwnerEmail.of("test@example.com"),
            entity.createdAt,
            entity.updatedAt
        )

        `when`(mapper.toEntity(originalOwner)).thenReturn(entity)
        `when`(jpaRepository.saveAndFlush(entity)).thenReturn(entity)
        `when`(mapper.toDomain(entity)).thenReturn(reconstructedOwner)

        val result = adapter.save(originalOwner)

        assertEquals(originalOwner.id, result.id)
        assertEquals(originalOwner.businessNumber, result.businessNumber)
        assertEquals(originalOwner.ownerPhoneNumber, result.ownerPhoneNumber)
        assertEquals(originalOwner.createdAt, result.createdAt)
        assertEquals(originalOwner.updatedAt, result.updatedAt)
    }

    @Test
    fun `should save multiple owners with different values`() {
        val owner1 = Owner.reconstruct(
            OwnerId.from(UUID.randomUUID()),
            BusinessNumber.of("1111111110"),
            OwnerPhoneNumber.of("010-1111-1111"),
            OwnerNickname.of("test"),
            OwnerEmail.of("test@example.com"),
            Instant.parse("2025-01-01T00:00:00Z"),
            Instant.parse("2025-01-01T00:00:00Z")
        )

        val owner2 = Owner.reconstruct(
            OwnerId.from(UUID.randomUUID()),
            BusinessNumber.of("2222222220"),
            OwnerPhoneNumber.of("010-2222-2222"),
            OwnerNickname.of("test"),
            OwnerEmail.of("test@example.com"),
            Instant.parse("2025-02-01T00:00:00Z"),
            Instant.parse("2025-02-01T00:00:00Z")
        )

        val entity1 = OwnerJpaEntity(
            id = owner1.id.value,
            businessNumber = owner1.businessNumber.value,
            phoneNumber = owner1.ownerPhoneNumber.value,
            email = "test@example.com",
            nickname = "test",
            createdAt = owner1.createdAt,
            updatedAt = owner1.updatedAt
        )

        val entity2 = OwnerJpaEntity(
            id = owner2.id.value,
            businessNumber = owner2.businessNumber.value,
            phoneNumber = owner2.ownerPhoneNumber.value,
            email = "test@example.com",
            nickname = "test",
            createdAt = owner2.createdAt,
            updatedAt = owner2.updatedAt
        )

        `when`(mapper.toEntity(owner1)).thenReturn(entity1)
        `when`(jpaRepository.saveAndFlush(entity1)).thenReturn(entity1)
        `when`(mapper.toDomain(entity1)).thenReturn(owner1)

        `when`(mapper.toEntity(owner2)).thenReturn(entity2)
        `when`(jpaRepository.saveAndFlush(entity2)).thenReturn(entity2)
        `when`(mapper.toDomain(entity2)).thenReturn(owner2)

        val result1 = adapter.save(owner1)
        val result2 = adapter.save(owner2)

        assertEquals(owner1, result1)
        assertEquals(owner2, result2)
    }

    @Test
    fun `should save owner with phone number starting with 011`() {
        val id = OwnerId.from(UUID.randomUUID())
        val businessNumber = BusinessNumber.of("6666666660")
        val ownerPhoneNumber = OwnerPhoneNumber.of("011-123-4567")
        val createdAt = Instant.now()
        val updatedAt = Instant.now()
        val owner = Owner.reconstruct(
            id,
            businessNumber,
            ownerPhoneNumber,
            OwnerNickname.of("test"),
            OwnerEmail.of("test@example.com"),
            createdAt,
            updatedAt
        )

        val entity = OwnerJpaEntity(
            id = id.value,
            businessNumber = businessNumber.value,
            phoneNumber = ownerPhoneNumber.value,
            email = "test@example.com",
            nickname = "test",
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        `when`(mapper.toEntity(owner)).thenReturn(entity)
        `when`(jpaRepository.saveAndFlush(entity)).thenReturn(entity)
        `when`(mapper.toDomain(entity)).thenReturn(owner)

        val result = adapter.save(owner)

        assertEquals(owner, result)
    }

    @Test
    fun `should save owner with all zeros UUID`() {
        val id = OwnerId.from(UUID(0, 0))
        val businessNumber = BusinessNumber.of("7777777770")
        val ownerPhoneNumber = OwnerPhoneNumber.of("010-7777-7777")
        val createdAt = Instant.now()
        val updatedAt = Instant.now()
        val owner = Owner.reconstruct(
            id,
            businessNumber,
            ownerPhoneNumber,
            OwnerNickname.of("test"),
            OwnerEmail.of("test@example.com"),
            createdAt,
            updatedAt
        )

        val entity = OwnerJpaEntity(
            id = id.value,
            businessNumber = businessNumber.value,
            phoneNumber = ownerPhoneNumber.value,
            email = "test@example.com",
            nickname = "test",
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        `when`(mapper.toEntity(owner)).thenReturn(entity)
        `when`(jpaRepository.saveAndFlush(entity)).thenReturn(entity)
        `when`(mapper.toDomain(entity)).thenReturn(owner)

        val result = adapter.save(owner)

        assertEquals(owner, result)
    }

    @Test
    fun `should save owner with high precision timestamp`() {
        val id = OwnerId.from(UUID.randomUUID())
        val businessNumber = BusinessNumber.of("8888888880")
        val ownerPhoneNumber = OwnerPhoneNumber.of("010-8888-8888")
        val createdAt = Instant.parse("2025-01-01T12:34:56.123456789Z")
        val updatedAt = Instant.parse("2025-01-01T12:34:56.987654321Z")
        val owner = Owner.reconstruct(
            id,
            businessNumber,
            ownerPhoneNumber,
            OwnerNickname.of("test"),
            OwnerEmail.of("test@example.com"),
            createdAt,
            updatedAt
        )

        val entity = OwnerJpaEntity(
            id = id.value,
            businessNumber = businessNumber.value,
            phoneNumber = ownerPhoneNumber.value,
            email = "test@example.com",
            nickname = "test",
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        `when`(mapper.toEntity(owner)).thenReturn(entity)
        `when`(jpaRepository.saveAndFlush(entity)).thenReturn(entity)
        `when`(mapper.toDomain(entity)).thenReturn(owner)

        val result = adapter.save(owner)

        assertEquals(owner, result)
    }

    @Test
    fun `should throw when business number contains special characters`() {
        assertFailsWith<InvalidOwnerBusinessNumberException> {
            BusinessNumber.of("12-34.567")
        }
    }

    @Test
    fun `should throw when business number contains letters`() {
        assertFailsWith<InvalidOwnerBusinessNumberException> {
            BusinessNumber.of("ABCDEFGHI")
        }
    }

    @Test
    fun `should throw DuplicateBusinessNumberException when business number constraint is violated`() {
        val id = OwnerId.from(UUID.randomUUID())
        val businessNumber = BusinessNumber.of("1234567890")
        val ownerPhoneNumber = OwnerPhoneNumber.of("010-1234-5678")
        val createdAt = Instant.now()
        val updatedAt = Instant.now()
        val owner = Owner.reconstruct(
            id,
            businessNumber,
            ownerPhoneNumber,
            OwnerNickname.of("test"),
            OwnerEmail.of("test@example.com"),
            createdAt,
            updatedAt
        )

        val entity = OwnerJpaEntity(
            id = id.value,
            businessNumber = businessNumber.value,
            phoneNumber = ownerPhoneNumber.value,
            email = "test@example.com",
            nickname = "test",
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        val exception = DataIntegrityViolationException("uk_owners_business_number")

        `when`(mapper.toEntity(owner)).thenReturn(entity)
        `when`(jpaRepository.saveAndFlush(entity)).thenThrow(exception)

        val thrownException = assertFailsWith<DuplicateOwnerBusinessNumberException> {
            adapter.save(owner)
        }

        assertEquals("Duplicate business number: 1234567890", thrownException.message)
        verify(mapper).toEntity(owner)
        verify(jpaRepository).saveAndFlush(entity)
    }

    @Test
    fun `should throw DuplicatePhoneNumberException when phone number constraint is violated`() {
        val id = OwnerId.from(UUID.randomUUID())
        val businessNumber = BusinessNumber.of("9876543210")
        val ownerPhoneNumber = OwnerPhoneNumber.of("010-9876-5432")
        val createdAt = Instant.now()
        val updatedAt = Instant.now()
        val owner = Owner.reconstruct(
            id,
            businessNumber,
            ownerPhoneNumber,
            OwnerNickname.of("test"),
            OwnerEmail.of("test@example.com"),
            createdAt,
            updatedAt
        )

        val entity = OwnerJpaEntity(
            id = id.value,
            businessNumber = businessNumber.value,
            phoneNumber = ownerPhoneNumber.value,
            email = "test@example.com",
            nickname = "test",
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        val exception = DataIntegrityViolationException("uk_owners_phone_number")

        `when`(mapper.toEntity(owner)).thenReturn(entity)
        `when`(jpaRepository.saveAndFlush(entity)).thenThrow(exception)

        val thrownException = assertFailsWith<DuplicateOwnerPhoneNumberException> {
            adapter.save(owner)
        }

        assertEquals("Duplicate phone number: 010-9876-5432", thrownException.message)
        verify(mapper).toEntity(owner)
        verify(jpaRepository).saveAndFlush(entity)
    }

    @Test
    fun `should throw DuplicateBusinessNumberException with correct business number value`() {
        val id = OwnerId.from(UUID.randomUUID())
        val businessNumber = BusinessNumber.of("1112223334")
        val ownerPhoneNumber = OwnerPhoneNumber.of("010-1111-2222")
        val createdAt = Instant.EPOCH
        val updatedAt = Instant.EPOCH
        val owner = Owner.reconstruct(
            id,
            businessNumber,
            ownerPhoneNumber,
            OwnerNickname.of("test"),
            OwnerEmail.of("test@example.com"),
            createdAt,
            updatedAt
        )

        val entity = OwnerJpaEntity(
            id = id.value,
            businessNumber = businessNumber.value,
            phoneNumber = ownerPhoneNumber.value,
            email = "test@example.com",
            nickname = "test",
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        val exception = DataIntegrityViolationException("uk_owners_business_number")

        `when`(mapper.toEntity(owner)).thenReturn(entity)
        `when`(jpaRepository.saveAndFlush(entity)).thenThrow(exception)

        val thrownException = assertFailsWith<DuplicateOwnerBusinessNumberException> {
            adapter.save(owner)
        }

        assertEquals("Duplicate business number: 1112223334", thrownException.message)
    }

    @Test
    fun `should throw DuplicatePhoneNumberException with correct phone number value`() {
        val id = OwnerId.from(UUID.randomUUID())
        val businessNumber = BusinessNumber.of("4445556660")
        val ownerPhoneNumber = OwnerPhoneNumber.of("02-4444-5555")
        val createdAt = Instant.EPOCH
        val updatedAt = Instant.EPOCH
        val owner = Owner.reconstruct(
            id,
            businessNumber,
            ownerPhoneNumber,
            OwnerNickname.of("test"),
            OwnerEmail.of("test@example.com"),
            createdAt,
            updatedAt
        )

        val entity = OwnerJpaEntity(
            id = id.value,
            businessNumber = businessNumber.value,
            phoneNumber = ownerPhoneNumber.value,
            email = "test@example.com",
            nickname = "test",
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        val exception = DataIntegrityViolationException("uk_owners_phone_number")

        `when`(mapper.toEntity(owner)).thenReturn(entity)
        `when`(jpaRepository.saveAndFlush(entity)).thenThrow(exception)

        val thrownException = assertFailsWith<DuplicateOwnerPhoneNumberException> {
            adapter.save(owner)
        }

        assertEquals("Duplicate phone number: 02-4444-5555", thrownException.message)
    }

    @Test
    fun `should throw when business number is alphanumeric`() {
        assertFailsWith<InvalidOwnerBusinessNumberException> {
            BusinessNumber.of("abc123xyz")
        }
    }

    @Test
    fun `should throw DuplicatePhoneNumberException for regional phone number`() {
        val id = OwnerId.from(UUID.randomUUID())
        val businessNumber = BusinessNumber.of("1234567890")
        val ownerPhoneNumber = OwnerPhoneNumber.of("031-123-4567")
        val createdAt = Instant.now()
        val updatedAt = Instant.now()
        val owner = Owner.reconstruct(
            id,
            businessNumber,
            ownerPhoneNumber,
            OwnerNickname.of("test"),
            OwnerEmail.of("test@example.com"),
            createdAt,
            updatedAt
        )

        val entity = OwnerJpaEntity(
            id = id.value,
            businessNumber = businessNumber.value,
            phoneNumber = ownerPhoneNumber.value,
            email = "test@example.com",
            nickname = "test",
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        val exception = DataIntegrityViolationException("uk_owners_phone_number")

        `when`(mapper.toEntity(owner)).thenReturn(entity)
        `when`(jpaRepository.saveAndFlush(entity)).thenThrow(exception)

        val thrownException = assertFailsWith<DuplicateOwnerPhoneNumberException> {
            adapter.save(owner)
        }

        assertEquals("Duplicate phone number: 031-123-4567", thrownException.message)
    }

    @Test
    fun `should call constraintTranslator when DataIntegrityViolationException occurs`() {
        val id = OwnerId.from(UUID.randomUUID())
        val businessNumber = BusinessNumber.of("9999999990")
        val ownerPhoneNumber = OwnerPhoneNumber.of("010-9999-9999")
        val createdAt = Instant.now()
        val updatedAt = Instant.now()
        val owner = Owner.reconstruct(
            id,
            businessNumber,
            ownerPhoneNumber,
            OwnerNickname.of("test"),
            OwnerEmail.of("test@example.com"),
            createdAt,
            updatedAt
        )

        val entity = OwnerJpaEntity(
            id = id.value,
            businessNumber = businessNumber.value,
            phoneNumber = ownerPhoneNumber.value,
            email = "test@example.com",
            nickname = "test",
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        val exception = DataIntegrityViolationException("uk_owners_business_number")

        `when`(mapper.toEntity(owner)).thenReturn(entity)
        `when`(jpaRepository.saveAndFlush(entity)).thenThrow(exception)

        assertFailsWith<DuplicateOwnerBusinessNumberException> {
            adapter.save(owner)
        }
    }
}
