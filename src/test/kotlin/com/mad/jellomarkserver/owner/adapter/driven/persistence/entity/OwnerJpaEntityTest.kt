package com.mad.jellomarkserver.owner.adapter.driven.persistence.entity

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.Instant
import java.util.*

class OwnerJpaEntityTest {

    @Test
    fun `should create OwnerJpaEntity with valid values`() {
        val id = UUID.randomUUID()
        val businessNumber = "123456789"
        val phoneNumber = "010-1234-5678"
        val createdAt = Instant.now()
        val updatedAt = Instant.now()

        val entity = OwnerJpaEntity(
            id = id,
            businessNumber = businessNumber,
            phoneNumber = phoneNumber,
            nickname = "test",
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        assertEquals(id, entity.id)
        assertEquals(businessNumber, entity.businessNumber)
        assertEquals(phoneNumber, entity.phoneNumber)
        assertEquals(createdAt, entity.createdAt)
        assertEquals(updatedAt, entity.updatedAt)
    }

    @Test
    fun `should create OwnerJpaEntity with mobile phone number starting with 010`() {
        val id = UUID.randomUUID()
        val businessNumber = "987654321"
        val phoneNumber = "010-9876-5432"
        val createdAt = Instant.EPOCH
        val updatedAt = Instant.EPOCH

        val entity = OwnerJpaEntity(
            id = id,
            businessNumber = businessNumber,
            phoneNumber = phoneNumber,
            nickname = "test",
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        assertEquals(id, entity.id)
        assertEquals(businessNumber, entity.businessNumber)
        assertEquals(phoneNumber, entity.phoneNumber)
        assertEquals(13, entity.phoneNumber.length)
        assertEquals(createdAt, entity.createdAt)
        assertEquals(updatedAt, entity.updatedAt)
    }

    @Test
    fun `should create OwnerJpaEntity with Seoul area phone number`() {
        val id = UUID.randomUUID()
        val businessNumber = "111111111"
        val phoneNumber = "02-1234-5678"
        val createdAt = Instant.now()
        val updatedAt = Instant.now()

        val entity = OwnerJpaEntity(
            id = id,
            businessNumber = businessNumber,
            phoneNumber = phoneNumber,
            nickname = "test",
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        assertEquals(id, entity.id)
        assertEquals(businessNumber, entity.businessNumber)
        assertEquals(phoneNumber, entity.phoneNumber)
        assertEquals(createdAt, entity.createdAt)
        assertEquals(updatedAt, entity.updatedAt)
    }

    @Test
    fun `should create OwnerJpaEntity with regional phone number`() {
        val id = UUID.randomUUID()
        val businessNumber = "222222222"
        val phoneNumber = "031-123-4567"
        val createdAt = Instant.EPOCH
        val updatedAt = Instant.EPOCH

        val entity = OwnerJpaEntity(
            id = id,
            businessNumber = businessNumber,
            phoneNumber = phoneNumber,
            nickname = "test",
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        assertEquals(id, entity.id)
        assertEquals(businessNumber, entity.businessNumber)
        assertEquals(phoneNumber, entity.phoneNumber)
        assertEquals(createdAt, entity.createdAt)
        assertEquals(updatedAt, entity.updatedAt)
    }

    @Test
    fun `should create OwnerJpaEntity with phone number starting with 011`() {
        val id = UUID.randomUUID()
        val businessNumber = "333333333"
        val phoneNumber = "011-123-4567"
        val createdAt = Instant.now()
        val updatedAt = Instant.now()

        val entity = OwnerJpaEntity(
            id = id,
            businessNumber = businessNumber,
            phoneNumber = phoneNumber,
            nickname = "test",
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        assertEquals(id, entity.id)
        assertEquals(businessNumber, entity.businessNumber)
        assertEquals(phoneNumber, entity.phoneNumber)
        assertEquals(createdAt, entity.createdAt)
        assertEquals(updatedAt, entity.updatedAt)
    }

    @Test
    fun `should create OwnerJpaEntity with epoch timestamps`() {
        val id = UUID.randomUUID()
        val businessNumber = "000000000"
        val phoneNumber = "010-0000-0000"
        val createdAt = Instant.EPOCH
        val updatedAt = Instant.EPOCH

        val entity = OwnerJpaEntity(
            id = id,
            businessNumber = businessNumber,
            phoneNumber = phoneNumber,
            nickname = "test",
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        assertEquals(id, entity.id)
        assertEquals(businessNumber, entity.businessNumber)
        assertEquals(phoneNumber, entity.phoneNumber)
        assertEquals(createdAt, entity.createdAt)
        assertEquals(updatedAt, entity.updatedAt)
    }

    @Test
    fun `should create OwnerJpaEntity with far future timestamps`() {
        val id = UUID.randomUUID()
        val businessNumber = "999999999"
        val phoneNumber = "010-9999-9999"
        val createdAt = Instant.parse("2099-12-31T23:59:59Z")
        val updatedAt = Instant.parse("2099-12-31T23:59:59Z")

        val entity = OwnerJpaEntity(
            id = id,
            businessNumber = businessNumber,
            phoneNumber = phoneNumber,
            nickname = "test",
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        assertEquals(id, entity.id)
        assertEquals(businessNumber, entity.businessNumber)
        assertEquals(phoneNumber, entity.phoneNumber)
        assertEquals(createdAt, entity.createdAt)
        assertEquals(updatedAt, entity.updatedAt)
    }

    @Test
    fun `should create OwnerJpaEntity with different created and updated timestamps`() {
        val id = UUID.randomUUID()
        val businessNumber = "123456789"
        val phoneNumber = "010-1234-5678"
        val createdAt = Instant.parse("2024-01-01T00:00:00Z")
        val updatedAt = Instant.parse("2024-12-31T23:59:59Z")

        val entity = OwnerJpaEntity(
            id = id,
            businessNumber = businessNumber,
            phoneNumber = phoneNumber,
            nickname = "test",
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        assertEquals(id, entity.id)
        assertEquals(businessNumber, entity.businessNumber)
        assertEquals(phoneNumber, entity.phoneNumber)
        assertEquals(createdAt, entity.createdAt)
        assertEquals(updatedAt, entity.updatedAt)
    }

    @Test
    fun `should update businessNumber field`() {
        val entity = OwnerJpaEntity(
            id = UUID.randomUUID(),
            businessNumber = "123456789",
            phoneNumber = "010-1234-5678",
            nickname = "test",
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )

        val newBusinessNumber = "987654321"
        entity.businessNumber = newBusinessNumber

        assertEquals(newBusinessNumber, entity.businessNumber)
    }

    @Test
    fun `should update phoneNumber field`() {
        val entity = OwnerJpaEntity(
            id = UUID.randomUUID(),
            businessNumber = "123456789",
            phoneNumber = "010-1234-5678",
            nickname = "test",
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )

        val newPhoneNumber = "010-9999-8888"
        entity.phoneNumber = newPhoneNumber

        assertEquals(newPhoneNumber, entity.phoneNumber)
    }

    @Test
    fun `should update updatedAt field`() {
        val createdAt = Instant.parse("2024-01-01T00:00:00Z")
        val entity = OwnerJpaEntity(
            id = UUID.randomUUID(),
            businessNumber = "123456789",
            phoneNumber = "010-1234-5678",
            nickname = "test",
            createdAt = createdAt,
            updatedAt = createdAt
        )

        val newUpdatedAt = Instant.parse("2024-12-31T23:59:59Z")
        entity.updatedAt = newUpdatedAt

        assertEquals(createdAt, entity.createdAt)
        assertEquals(newUpdatedAt, entity.updatedAt)
    }

    @Test
    fun `should update id field`() {
        val entity = OwnerJpaEntity(
            id = UUID.randomUUID(),
            businessNumber = "123456789",
            phoneNumber = "010-1234-5678",
            nickname = "test",
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )

        val newId = UUID.randomUUID()
        entity.id = newId

        assertEquals(newId, entity.id)
    }

    @Test
    fun `should update createdAt field`() {
        val entity = OwnerJpaEntity(
            id = UUID.randomUUID(),
            businessNumber = "123456789",
            phoneNumber = "010-1234-5678",
            nickname = "test",
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )

        val newCreatedAt = Instant.parse("2024-01-01T00:00:00Z")
        entity.createdAt = newCreatedAt

        assertEquals(newCreatedAt, entity.createdAt)
    }

    @Test
    fun `should create OwnerJpaEntity with all zeros UUID`() {
        val id = UUID(0L, 0L)
        val businessNumber = "123456789"
        val phoneNumber = "010-1234-5678"
        val createdAt = Instant.EPOCH
        val updatedAt = Instant.EPOCH

        val entity = OwnerJpaEntity(
            id = id,
            businessNumber = businessNumber,
            phoneNumber = phoneNumber,
            nickname = "test",
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        assertEquals(id, entity.id)
        assertEquals(businessNumber, entity.businessNumber)
        assertEquals(phoneNumber, entity.phoneNumber)
        assertEquals(createdAt, entity.createdAt)
        assertEquals(updatedAt, entity.updatedAt)
    }

    @Test
    fun `should create multiple OwnerJpaEntity instances with different values`() {
        val entity1 = OwnerJpaEntity(
            id = UUID.randomUUID(),
            businessNumber = "111111111",
            phoneNumber = "010-1111-1111",
            nickname = "test",
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
        val entity2 = OwnerJpaEntity(
            id = UUID.randomUUID(),
            businessNumber = "222222222",
            phoneNumber = "010-2222-2222",
            nickname = "test",
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )

        assertEquals("111111111", entity1.businessNumber)
        assertEquals("010-1111-1111", entity1.phoneNumber)
        assertEquals("222222222", entity2.businessNumber)
        assertEquals("010-2222-2222", entity2.phoneNumber)
    }

    @Test
    fun `should update all mutable fields`() {
        val entity = OwnerJpaEntity(
            id = UUID.randomUUID(),
            businessNumber = "123456789",
            phoneNumber = "010-1234-5678",
            nickname = "test",
            createdAt = Instant.parse("2024-01-01T00:00:00Z"),
            updatedAt = Instant.parse("2024-01-01T00:00:00Z")
        )

        val newId = UUID.randomUUID()
        val newBusinessNumber = "987654321"
        val newPhoneNumber = "010-9876-5432"
        val newCreatedAt = Instant.parse("2024-06-01T00:00:00Z")
        val newUpdatedAt = Instant.parse("2024-12-31T23:59:59Z")

        entity.id = newId
        entity.businessNumber = newBusinessNumber
        entity.phoneNumber = newPhoneNumber
        entity.createdAt = newCreatedAt
        entity.updatedAt = newUpdatedAt

        assertEquals(newId, entity.id)
        assertEquals(newBusinessNumber, entity.businessNumber)
        assertEquals(newPhoneNumber, entity.phoneNumber)
        assertEquals(newCreatedAt, entity.createdAt)
        assertEquals(newUpdatedAt, entity.updatedAt)
    }

    @Test
    fun `should create OwnerJpaEntity with timestamp precision`() {
        val id = UUID.randomUUID()
        val businessNumber = "123456789"
        val phoneNumber = "010-1234-5678"
        val createdAt = Instant.parse("2024-10-27T12:34:56.789123456Z")
        val updatedAt = Instant.parse("2024-10-27T12:34:56.789123456Z")

        val entity = OwnerJpaEntity(
            id = id,
            businessNumber = businessNumber,
            phoneNumber = phoneNumber,
            nickname = "test",
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        assertEquals(id, entity.id)
        assertEquals(businessNumber, entity.businessNumber)
        assertEquals(phoneNumber, entity.phoneNumber)
        assertEquals(createdAt, entity.createdAt)
        assertEquals(updatedAt, entity.updatedAt)
    }

    @Test
    fun `should create OwnerJpaEntity with maximum length phone number`() {
        val id = UUID.randomUUID()
        val businessNumber = "555555555"
        val phoneNumber = "010-1234-5678"
        val createdAt = Instant.now()
        val updatedAt = Instant.now()

        val entity = OwnerJpaEntity(
            id = id,
            businessNumber = businessNumber,
            phoneNumber = phoneNumber,
            nickname = "test",
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        assertEquals(id, entity.id)
        assertEquals(businessNumber, entity.businessNumber)
        assertEquals(phoneNumber, entity.phoneNumber)
        assertEquals(13, entity.phoneNumber.length)
        assertEquals(createdAt, entity.createdAt)
        assertEquals(updatedAt, entity.updatedAt)
    }

    @Test
    fun `should create OwnerJpaEntity with 4-digit middle part phone number`() {
        val id = UUID.randomUUID()
        val businessNumber = "666666666"
        val phoneNumber = "02-1234-5678"
        val createdAt = Instant.now()
        val updatedAt = Instant.now()

        val entity = OwnerJpaEntity(
            id = id,
            businessNumber = businessNumber,
            phoneNumber = phoneNumber,
            nickname = "test",
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        assertEquals(id, entity.id)
        assertEquals(businessNumber, entity.businessNumber)
        assertEquals(phoneNumber, entity.phoneNumber)
        assertEquals(12, entity.phoneNumber.length)
        assertEquals(createdAt, entity.createdAt)
        assertEquals(updatedAt, entity.updatedAt)
    }

    @Test
    fun `should create OwnerJpaEntity with 3-digit middle part phone number`() {
        val id = UUID.randomUUID()
        val businessNumber = "777777777"
        val phoneNumber = "031-123-4567"
        val createdAt = Instant.now()
        val updatedAt = Instant.now()

        val entity = OwnerJpaEntity(
            id = id,
            businessNumber = businessNumber,
            phoneNumber = phoneNumber,
            nickname = "test",
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        assertEquals(id, entity.id)
        assertEquals(businessNumber, entity.businessNumber)
        assertEquals(phoneNumber, entity.phoneNumber)
        assertEquals(12, entity.phoneNumber.length)
        assertEquals(createdAt, entity.createdAt)
        assertEquals(updatedAt, entity.updatedAt)
    }

    @Test
    fun `should create OwnerJpaEntity with minimum length nickname`() {
        val id = UUID.randomUUID()
        val businessNumber = "123456789"
        val phoneNumber = "010-1234-5678"
        val nickname = "ab"
        val createdAt = Instant.now()
        val updatedAt = Instant.now()

        val entity = OwnerJpaEntity(
            id = id,
            businessNumber = businessNumber,
            phoneNumber = phoneNumber,
            nickname = nickname,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        assertEquals(id, entity.id)
        assertEquals(businessNumber, entity.businessNumber)
        assertEquals(phoneNumber, entity.phoneNumber)
        assertEquals(nickname, entity.nickname)
        assertEquals(createdAt, entity.createdAt)
        assertEquals(updatedAt, entity.updatedAt)
    }

    @Test
    fun `should create OwnerJpaEntity with maximum length nickname`() {
        val id = UUID.randomUUID()
        val businessNumber = "123456789"
        val phoneNumber = "010-1234-5678"
        val nickname = "a".repeat(100)
        val createdAt = Instant.now()
        val updatedAt = Instant.now()

        val entity = OwnerJpaEntity(
            id = id,
            businessNumber = businessNumber,
            phoneNumber = phoneNumber,
            nickname = nickname,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        assertEquals(id, entity.id)
        assertEquals(businessNumber, entity.businessNumber)
        assertEquals(phoneNumber, entity.phoneNumber)
        assertEquals(nickname, entity.nickname)
        assertEquals(createdAt, entity.createdAt)
        assertEquals(updatedAt, entity.updatedAt)
    }

    @Test
    fun `should create OwnerJpaEntity with special characters in nickname`() {
        val id = UUID.randomUUID()
        val businessNumber = "123456789"
        val phoneNumber = "010-1234-5678"
        val nickname = "user_123-test.name"
        val createdAt = Instant.now()
        val updatedAt = Instant.now()

        val entity = OwnerJpaEntity(
            id = id,
            businessNumber = businessNumber,
            phoneNumber = phoneNumber,
            nickname = nickname,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        assertEquals(id, entity.id)
        assertEquals(businessNumber, entity.businessNumber)
        assertEquals(phoneNumber, entity.phoneNumber)
        assertEquals(nickname, entity.nickname)
        assertEquals(createdAt, entity.createdAt)
        assertEquals(updatedAt, entity.updatedAt)
    }

    @Test
    fun `should create OwnerJpaEntity with Korean characters in nickname`() {
        val id = UUID.randomUUID()
        val businessNumber = "123456789"
        val phoneNumber = "010-1234-5678"
        val nickname = "한글닉네임"
        val createdAt = Instant.now()
        val updatedAt = Instant.now()

        val entity = OwnerJpaEntity(
            id = id,
            businessNumber = businessNumber,
            phoneNumber = phoneNumber,
            nickname = nickname,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        assertEquals(id, entity.id)
        assertEquals(businessNumber, entity.businessNumber)
        assertEquals(phoneNumber, entity.phoneNumber)
        assertEquals(nickname, entity.nickname)
        assertEquals(createdAt, entity.createdAt)
        assertEquals(updatedAt, entity.updatedAt)
    }

    @Test
    fun `should create OwnerJpaEntity with numeric nickname`() {
        val id = UUID.randomUUID()
        val businessNumber = "123456789"
        val phoneNumber = "010-1234-5678"
        val nickname = "12345678"
        val createdAt = Instant.now()
        val updatedAt = Instant.now()

        val entity = OwnerJpaEntity(
            id = id,
            businessNumber = businessNumber,
            phoneNumber = phoneNumber,
            nickname = nickname,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        assertEquals(id, entity.id)
        assertEquals(businessNumber, entity.businessNumber)
        assertEquals(phoneNumber, entity.phoneNumber)
        assertEquals(nickname, entity.nickname)
        assertEquals(createdAt, entity.createdAt)
        assertEquals(updatedAt, entity.updatedAt)
    }

    @Test
    fun `should create OwnerJpaEntity with mixed case nickname`() {
        val id = UUID.randomUUID()
        val businessNumber = "123456789"
        val phoneNumber = "010-1234-5678"
        val nickname = "TestUser123"
        val createdAt = Instant.now()
        val updatedAt = Instant.now()

        val entity = OwnerJpaEntity(
            id = id,
            businessNumber = businessNumber,
            phoneNumber = phoneNumber,
            nickname = nickname,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        assertEquals(id, entity.id)
        assertEquals(businessNumber, entity.businessNumber)
        assertEquals(phoneNumber, entity.phoneNumber)
        assertEquals(nickname, entity.nickname)
        assertEquals(createdAt, entity.createdAt)
        assertEquals(updatedAt, entity.updatedAt)
    }

    @Test
    fun `should create OwnerJpaEntity with uppercase nickname`() {
        val id = UUID.randomUUID()
        val businessNumber = "123456789"
        val phoneNumber = "010-1234-5678"
        val nickname = "TESTUSER"
        val createdAt = Instant.now()
        val updatedAt = Instant.now()

        val entity = OwnerJpaEntity(
            id = id,
            businessNumber = businessNumber,
            phoneNumber = phoneNumber,
            nickname = nickname,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        assertEquals(id, entity.id)
        assertEquals(businessNumber, entity.businessNumber)
        assertEquals(phoneNumber, entity.phoneNumber)
        assertEquals(nickname, entity.nickname)
        assertEquals(createdAt, entity.createdAt)
        assertEquals(updatedAt, entity.updatedAt)
    }

    @Test
    fun `should update nickname field`() {
        val entity = OwnerJpaEntity(
            id = UUID.randomUUID(),
            businessNumber = "123456789",
            phoneNumber = "010-1234-5678",
            nickname = "oldnickname",
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )

        val newNickname = "newnickname"
        entity.nickname = newNickname

        assertEquals(newNickname, entity.nickname)
    }

    @Test
    fun `should create OwnerJpaEntity with maximum length businessNumber`() {
        val id = UUID.randomUUID()
        val businessNumber = "a".repeat(50)
        val phoneNumber = "010-1234-5678"
        val nickname = "test"
        val createdAt = Instant.now()
        val updatedAt = Instant.now()

        val entity = OwnerJpaEntity(
            id = id,
            businessNumber = businessNumber,
            phoneNumber = phoneNumber,
            nickname = nickname,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        assertEquals(id, entity.id)
        assertEquals(businessNumber, entity.businessNumber)
        assertEquals(phoneNumber, entity.phoneNumber)
        assertEquals(nickname, entity.nickname)
        assertEquals(createdAt, entity.createdAt)
        assertEquals(updatedAt, entity.updatedAt)
    }

    @Test
    fun `should create OwnerJpaEntity with alphanumeric businessNumber`() {
        val id = UUID.randomUUID()
        val businessNumber = "ABC123XYZ"
        val phoneNumber = "010-1234-5678"
        val nickname = "test"
        val createdAt = Instant.now()
        val updatedAt = Instant.now()

        val entity = OwnerJpaEntity(
            id = id,
            businessNumber = businessNumber,
            phoneNumber = phoneNumber,
            nickname = nickname,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        assertEquals(id, entity.id)
        assertEquals(businessNumber, entity.businessNumber)
        assertEquals(phoneNumber, entity.phoneNumber)
        assertEquals(nickname, entity.nickname)
        assertEquals(createdAt, entity.createdAt)
        assertEquals(updatedAt, entity.updatedAt)
    }
}
