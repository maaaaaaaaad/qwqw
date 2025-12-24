package com.mad.jellomarkserver.owner.core.domain.model

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import java.util.*

class OwnerTest {

    @Test
    fun `should create Owner with valid business number`() {
        val businessNumber = BusinessNumber.of("123456789")
        val ownerPhoneNumber = OwnerPhoneNumber.of("010-1234-5678")

        val owner =
            Owner.create(businessNumber, ownerPhoneNumber, OwnerNickname.of("test"), OwnerEmail.of("test@example.com"))

        assertNotNull(owner.id)
        assertEquals(businessNumber, owner.businessNumber)
        assertEquals(ownerPhoneNumber, owner.ownerPhoneNumber)
        assertNotNull(owner.createdAt)
        assertNotNull(owner.updatedAt)
        assertEquals(owner.createdAt, owner.updatedAt)
    }

    @Test
    fun `should create Owner with 9 digit business number`() {
        val businessNumber = BusinessNumber.of("101234567")
        val ownerPhoneNumber = OwnerPhoneNumber.of("010-1234-5678")

        val owner =
            Owner.create(businessNumber, ownerPhoneNumber, OwnerNickname.of("test"), OwnerEmail.of("test@example.com"))

        assertEquals(businessNumber, owner.businessNumber)
    }

    @Test
    fun `should create Owner with alphanumeric business number`() {
        val businessNumber = BusinessNumber.of("abc123def")
        val ownerPhoneNumber = OwnerPhoneNumber.of("010-1234-5678")

        val owner =
            Owner.create(businessNumber, ownerPhoneNumber, OwnerNickname.of("test"), OwnerEmail.of("test@example.com"))

        assertEquals(businessNumber, owner.businessNumber)
    }

    @Test
    fun `should create Owner with uppercase business number`() {
        val businessNumber = BusinessNumber.of("ABCDEFGHI")
        val ownerPhoneNumber = OwnerPhoneNumber.of("010-1234-5678")

        val owner =
            Owner.create(businessNumber, ownerPhoneNumber, OwnerNickname.of("test"), OwnerEmail.of("test@example.com"))

        assertEquals(businessNumber, owner.businessNumber)
    }

    @Test
    fun `should create Owner with lowercase business number`() {
        val businessNumber = BusinessNumber.of("abcdefghi")
        val ownerPhoneNumber = OwnerPhoneNumber.of("010-1234-5678")

        val owner =
            Owner.create(businessNumber, ownerPhoneNumber, OwnerNickname.of("test"), OwnerEmail.of("test@example.com"))

        assertEquals(businessNumber, owner.businessNumber)
    }

    @Test
    fun `should create Owner with special characters in business number`() {
        val businessNumber = BusinessNumber.of("!@#$%^&*(")
        val ownerPhoneNumber = OwnerPhoneNumber.of("010-1234-5678")

        val owner =
            Owner.create(businessNumber, ownerPhoneNumber, OwnerNickname.of("test"), OwnerEmail.of("test@example.com"))

        assertEquals(businessNumber, owner.businessNumber)
    }

    @Test
    fun `should create Owner with hyphens in business number`() {
        val businessNumber = BusinessNumber.of("123-45-67")
        val ownerPhoneNumber = OwnerPhoneNumber.of("010-1234-5678")

        val owner =
            Owner.create(businessNumber, ownerPhoneNumber, OwnerNickname.of("test"), OwnerEmail.of("test@example.com"))

        assertEquals(businessNumber, owner.businessNumber)
    }

    @Test
    fun `should create Owner with dots in business number`() {
        val businessNumber = BusinessNumber.of("123.456.7")
        val ownerPhoneNumber = OwnerPhoneNumber.of("010-1234-5678")

        val owner =
            Owner.create(businessNumber, ownerPhoneNumber, OwnerNickname.of("test"), OwnerEmail.of("test@example.com"))

        assertEquals(businessNumber, owner.businessNumber)
    }

    @Test
    fun `should create Owner with all zeros business number`() {
        val businessNumber = BusinessNumber.of("000000000")
        val ownerPhoneNumber = OwnerPhoneNumber.of("010-1234-5678")

        val owner =
            Owner.create(businessNumber, ownerPhoneNumber, OwnerNickname.of("test"), OwnerEmail.of("test@example.com"))

        assertEquals(businessNumber, owner.businessNumber)
    }

    @Test
    fun `should create Owner with all nines business number`() {
        val businessNumber = BusinessNumber.of("999999999")
        val ownerPhoneNumber = OwnerPhoneNumber.of("010-1234-5678")

        val owner =
            Owner.create(businessNumber, ownerPhoneNumber, OwnerNickname.of("test"), OwnerEmail.of("test@example.com"))

        assertEquals(businessNumber, owner.businessNumber)
    }

    @Test
    fun `should create Owner with fixed clock`() {
        val businessNumber = BusinessNumber.of("123456789")
        val ownerPhoneNumber = OwnerPhoneNumber.of("010-1234-5678")
        val fixedInstant = Instant.parse("2025-01-01T00:00:00Z")
        val fixedClock = Clock.fixed(fixedInstant, ZoneId.of("UTC"))

        val owner = Owner.create(
            businessNumber,
            ownerPhoneNumber,
            OwnerNickname.of("test"),
            OwnerEmail.of("test@example.com"),
            fixedClock
        )

        assertEquals(fixedInstant, owner.createdAt)
        assertEquals(fixedInstant, owner.updatedAt)
    }

    @Test
    fun `should create Owner with system clock by default`() {
        val businessNumber = BusinessNumber.of("123456789")
        val ownerPhoneNumber = OwnerPhoneNumber.of("010-1234-5678")
        val before = Instant.now()

        val owner =
            Owner.create(businessNumber, ownerPhoneNumber, OwnerNickname.of("test"), OwnerEmail.of("test@example.com"))

        val after = Instant.now()
        org.junit.jupiter.api.Assertions.assertTrue(owner.createdAt in before..after)
        org.junit.jupiter.api.Assertions.assertTrue(owner.updatedAt in before..after)
    }

    @Test
    fun `should reconstruct Owner with all fields`() {
        val id = OwnerId.from(UUID.randomUUID())
        val businessNumber = BusinessNumber.of("123456789")
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

        assertEquals(id, owner.id)
        assertEquals(businessNumber, owner.businessNumber)
        assertEquals(ownerPhoneNumber, owner.ownerPhoneNumber)
        assertEquals(createdAt, owner.createdAt)
        assertEquals(updatedAt, owner.updatedAt)
    }

    @Test
    fun `should reconstruct Owner with epoch timestamps`() {
        val id = OwnerId.from(UUID.randomUUID())
        val businessNumber = BusinessNumber.of("123456789")
        val ownerPhoneNumber = OwnerPhoneNumber.of("010-1234-5678")
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

        assertEquals(id, owner.id)
        assertEquals(businessNumber, owner.businessNumber)
        assertEquals(ownerPhoneNumber, owner.ownerPhoneNumber)
        assertEquals(createdAt, owner.createdAt)
        assertEquals(updatedAt, owner.updatedAt)
    }

    @Test
    fun `should reconstruct Owner with far future timestamps`() {
        val id = OwnerId.from(UUID.randomUUID())
        val businessNumber = BusinessNumber.of("123456789")
        val ownerPhoneNumber = OwnerPhoneNumber.of("010-1234-5678")
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

        assertEquals(createdAt, owner.createdAt)
        assertEquals(updatedAt, owner.updatedAt)
    }

    @Test
    fun `should reconstruct Owner with different created and updated timestamps`() {
        val id = OwnerId.from(UUID.randomUUID())
        val businessNumber = BusinessNumber.of("123456789")
        val ownerPhoneNumber = OwnerPhoneNumber.of("010-1234-5678")
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

        assertEquals(createdAt, owner.createdAt)
        assertEquals(updatedAt, owner.updatedAt)
        assertNotEquals(owner.createdAt, owner.updatedAt)
    }

    @Test
    fun `should reconstruct Owner with high precision timestamp`() {
        val id = OwnerId.from(UUID.randomUUID())
        val businessNumber = BusinessNumber.of("123456789")
        val ownerPhoneNumber = OwnerPhoneNumber.of("010-1234-5678")
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

        assertEquals(createdAt, owner.createdAt)
        assertEquals(updatedAt, owner.updatedAt)
    }

    @Test
    fun `should reconstruct Owner with all zeros UUID`() {
        val id = OwnerId.from(UUID(0, 0))
        val businessNumber = BusinessNumber.of("123456789")
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

        assertEquals(id, owner.id)
    }

    @Test
    fun `should have equality based on id`() {
        val id = OwnerId.from(UUID.randomUUID())
        val businessNumber1 = BusinessNumber.of("123456789")
        val businessNumber2 = BusinessNumber.of("987654321")
        val ownerPhoneNumber = OwnerPhoneNumber.of("010-1234-5678")
        val createdAt = Instant.parse("2025-01-01T00:00:00Z")
        val updatedAt = Instant.parse("2025-01-01T00:00:00Z")

        val owner1 = Owner.reconstruct(
            id,
            businessNumber1,
            ownerPhoneNumber,
            OwnerNickname.of("test"),
            OwnerEmail.of("test@example.com"),
            createdAt,
            updatedAt
        )
        val owner2 = Owner.reconstruct(
            id,
            businessNumber2,
            ownerPhoneNumber,
            OwnerNickname.of("test"),
            OwnerEmail.of("test@example.com"),
            createdAt,
            updatedAt
        )

        assertEquals(owner1, owner2)
    }

    @Test
    fun `should not be equal when ids are different`() {
        val id1 = OwnerId.from(UUID.randomUUID())
        val id2 = OwnerId.from(UUID.randomUUID())
        val businessNumber = BusinessNumber.of("123456789")
        val ownerPhoneNumber = OwnerPhoneNumber.of("010-1234-5678")
        val createdAt = Instant.parse("2025-01-01T00:00:00Z")
        val updatedAt = Instant.parse("2025-01-01T00:00:00Z")

        val owner1 = Owner.reconstruct(
            id1,
            businessNumber,
            ownerPhoneNumber,
            OwnerNickname.of("test"),
            OwnerEmail.of("test@example.com"),
            createdAt,
            updatedAt
        )
        val owner2 = Owner.reconstruct(
            id2,
            businessNumber,
            ownerPhoneNumber,
            OwnerNickname.of("test"),
            OwnerEmail.of("test@example.com"),
            createdAt,
            updatedAt
        )

        assertNotEquals(owner1, owner2)
    }

    @Test
    fun `should be equal to itself`() {
        val owner = Owner.create(
            BusinessNumber.of("123456789"),
            OwnerPhoneNumber.of("010-1234-5678"),
            OwnerNickname.of("test"),
            OwnerEmail.of("test@example.com")
        )

        assertEquals(owner, owner)
    }

    @Test
    fun `should not be equal to null`() {
        val owner = Owner.create(
            BusinessNumber.of("123456789"),
            OwnerPhoneNumber.of("010-1234-5678"),
            OwnerNickname.of("test"),
            OwnerEmail.of("test@example.com")
        )

        assertNotEquals(owner, null)
    }

    @Test
    fun `should not be equal to different type`() {
        val owner = Owner.create(
            BusinessNumber.of("123456789"),
            OwnerPhoneNumber.of("010-1234-5678"),
            OwnerNickname.of("test"),
            OwnerEmail.of("test@example.com")
        )

        assertNotEquals(owner, "string")
    }

    @Test
    fun `should have same hashCode when ids are equal`() {
        val id = OwnerId.from(UUID.randomUUID())
        val businessNumber1 = BusinessNumber.of("123456789")
        val businessNumber2 = BusinessNumber.of("987654321")
        val ownerPhoneNumber = OwnerPhoneNumber.of("010-1234-5678")
        val createdAt = Instant.parse("2025-01-01T00:00:00Z")
        val updatedAt = Instant.parse("2025-01-01T00:00:00Z")

        val owner1 = Owner.reconstruct(
            id,
            businessNumber1,
            ownerPhoneNumber,
            OwnerNickname.of("test"),
            OwnerEmail.of("test@example.com"),
            createdAt,
            updatedAt
        )
        val owner2 = Owner.reconstruct(
            id,
            businessNumber2,
            ownerPhoneNumber,
            OwnerNickname.of("test"),
            OwnerEmail.of("test@example.com"),
            createdAt,
            updatedAt
        )

        assertEquals(owner1.hashCode(), owner2.hashCode())
    }

    @Test
    fun `should have different hashCode when ids are different`() {
        val id1 = OwnerId.from(UUID.randomUUID())
        val id2 = OwnerId.from(UUID.randomUUID())
        val businessNumber = BusinessNumber.of("123456789")
        val ownerPhoneNumber = OwnerPhoneNumber.of("010-1234-5678")
        val createdAt = Instant.parse("2025-01-01T00:00:00Z")
        val updatedAt = Instant.parse("2025-01-01T00:00:00Z")

        val owner1 = Owner.reconstruct(
            id1,
            businessNumber,
            ownerPhoneNumber,
            OwnerNickname.of("test"),
            OwnerEmail.of("test@example.com"),
            createdAt,
            updatedAt
        )
        val owner2 = Owner.reconstruct(
            id2,
            businessNumber,
            ownerPhoneNumber,
            OwnerNickname.of("test"),
            OwnerEmail.of("test@example.com"),
            createdAt,
            updatedAt
        )

        assertNotEquals(owner1.hashCode(), owner2.hashCode())
    }

    @Test
    fun `should generate unique ids for different owners created`() {
        val businessNumber = BusinessNumber.of("123456789")
        val ownerPhoneNumber = OwnerPhoneNumber.of("010-1234-5678")

        val owner1 =
            Owner.create(businessNumber, ownerPhoneNumber, OwnerNickname.of("test"), OwnerEmail.of("test@example.com"))
        val owner2 =
            Owner.create(businessNumber, ownerPhoneNumber, OwnerNickname.of("test"), OwnerEmail.of("test@example.com"))

        assertNotEquals(owner1.id, owner2.id)
        assertNotEquals(owner1, owner2)
    }

    @Test
    fun `should create Owner with epoch timestamp using fixed clock`() {
        val businessNumber = BusinessNumber.of("123456789")
        val ownerPhoneNumber = OwnerPhoneNumber.of("010-1234-5678")
        val fixedClock = Clock.fixed(Instant.EPOCH, ZoneId.of("UTC"))

        val owner = Owner.create(
            businessNumber,
            ownerPhoneNumber,
            OwnerNickname.of("test"),
            OwnerEmail.of("test@example.com"),
            fixedClock
        )

        assertEquals(Instant.EPOCH, owner.createdAt)
        assertEquals(Instant.EPOCH, owner.updatedAt)
    }

    @Test
    fun `should create Owner with far future timestamp using fixed clock`() {
        val businessNumber = BusinessNumber.of("123456789")
        val ownerPhoneNumber = OwnerPhoneNumber.of("010-1234-5678")
        val futureInstant = Instant.parse("2099-12-31T23:59:59Z")
        val fixedClock = Clock.fixed(futureInstant, ZoneId.of("UTC"))

        val owner = Owner.create(
            businessNumber,
            ownerPhoneNumber,
            OwnerNickname.of("test"),
            OwnerEmail.of("test@example.com"),
            fixedClock
        )

        assertEquals(futureInstant, owner.createdAt)
        assertEquals(futureInstant, owner.updatedAt)
    }

    @Test
    fun `should reconstruct Owner with alphanumeric business number`() {
        val id = OwnerId.from(UUID.randomUUID())
        val businessNumber = BusinessNumber.of("abc123XYZ")
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

        assertEquals(businessNumber, owner.businessNumber)
    }

    @Test
    fun `should create multiple owners with different values`() {
        val ownerPhoneNumber = OwnerPhoneNumber.of("010-1234-5678")
        val owner1 = Owner.create(
            BusinessNumber.of("123456789"),
            ownerPhoneNumber,
            OwnerNickname.of("test"),
            OwnerEmail.of("test@example.com")
        )
        val owner2 = Owner.create(
            BusinessNumber.of("987654321"),
            ownerPhoneNumber,
            OwnerNickname.of("test"),
            OwnerEmail.of("test@example.com")
        )

        assertNotEquals(owner1.id, owner2.id)
        assertNotEquals(owner1.businessNumber, owner2.businessNumber)
    }

    @Test
    fun `should maintain id consistency across multiple operations`() {
        val id = OwnerId.from(UUID.randomUUID())
        val businessNumber = BusinessNumber.of("123456789")
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

        assertEquals(id, owner.id)
        assertEquals(id.hashCode(), owner.id.hashCode())
    }

    @Test
    fun `should reconstruct Owner with mixed case business number`() {
        val id = OwnerId.from(UUID.randomUUID())
        val businessNumber = BusinessNumber.of("AbCdEfGhI")
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

        assertEquals(businessNumber, owner.businessNumber)
    }

    @Test
    fun `should reconstruct Owner with business number containing hyphens and dots`() {
        val id = OwnerId.from(UUID.randomUUID())
        val businessNumber = BusinessNumber.of("12-34.567")
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

        assertEquals(businessNumber, owner.businessNumber)
    }

    @Test
    fun `should create Owner with different business number formats`() {
        val ownerPhoneNumber = OwnerPhoneNumber.of("010-1234-5678")
        val owner1 = Owner.create(
            BusinessNumber.of("123456789"),
            ownerPhoneNumber,
            OwnerNickname.of("test"),
            OwnerEmail.of("test@example.com")
        )
        val owner2 = Owner.create(
            BusinessNumber.of("ABCDEFGHI"),
            ownerPhoneNumber,
            OwnerNickname.of("test"),
            OwnerEmail.of("test@example.com")
        )
        val owner3 = Owner.create(
            BusinessNumber.of("abc123xyz"),
            ownerPhoneNumber,
            OwnerNickname.of("test"),
            OwnerEmail.of("test@example.com")
        )

        assertNotEquals(owner1.businessNumber, owner2.businessNumber)
        assertNotEquals(owner2.businessNumber, owner3.businessNumber)
        assertNotEquals(owner1.businessNumber, owner3.businessNumber)
    }

    @Test
    fun `should create Owner and verify all properties are set`() {
        val businessNumber = BusinessNumber.of("123456789")
        val ownerPhoneNumber = OwnerPhoneNumber.of("010-1234-5678")
        val fixedInstant = Instant.parse("2025-01-01T00:00:00Z")
        val fixedClock = Clock.fixed(fixedInstant, ZoneId.of("UTC"))

        val owner = Owner.create(
            businessNumber,
            ownerPhoneNumber,
            OwnerNickname.of("test"),
            OwnerEmail.of("test@example.com"),
            fixedClock
        )

        assertNotNull(owner.id)
        assertNotNull(owner.id.value)
        assertEquals(businessNumber, owner.businessNumber)
        assertEquals(ownerPhoneNumber, owner.ownerPhoneNumber)
        assertEquals(fixedInstant, owner.createdAt)
        assertEquals(fixedInstant, owner.updatedAt)
    }

    @Test
    fun `should reconstruct Owner and verify all properties match input`() {
        val id = OwnerId.from(UUID.randomUUID())
        val businessNumber = BusinessNumber.of("987654321")
        val ownerPhoneNumber = OwnerPhoneNumber.of("010-9876-5432")
        val createdAt = Instant.parse("2024-06-15T10:30:45.123456789Z")
        val updatedAt = Instant.parse("2024-06-15T10:30:45.987654321Z")

        val owner = Owner.reconstruct(
            id,
            businessNumber,
            ownerPhoneNumber,
            OwnerNickname.of("test"),
            OwnerEmail.of("test@example.com"),
            createdAt,
            updatedAt
        )

        assertEquals(id, owner.id)
        assertEquals(id.value, owner.id.value)
        assertEquals(businessNumber, owner.businessNumber)
        assertEquals(businessNumber.value, owner.businessNumber.value)
        assertEquals(ownerPhoneNumber, owner.ownerPhoneNumber)
        assertEquals(ownerPhoneNumber.value, owner.ownerPhoneNumber.value)
        assertEquals(createdAt, owner.createdAt)
        assertEquals(updatedAt, owner.updatedAt)
    }
}
