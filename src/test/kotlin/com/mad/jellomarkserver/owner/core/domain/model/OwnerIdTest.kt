package com.mad.jellomarkserver.owner.core.domain.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test
import java.util.UUID
import kotlin.test.assertFailsWith

class OwnerIdTest {

    @Test
    fun `should create OwnerId with new()`() {
        val ownerId = OwnerId.new()
        assertEquals(36, ownerId.value.toString().length)
    }

    @Test
    fun `should create OwnerId with from() using valid UUID`() {
        val uuid = UUID.randomUUID()
        val ownerId = OwnerId.from(uuid)
        assertEquals(uuid, ownerId.value)
    }

    @Test
    fun `should create OwnerId with from() using specific UUID`() {
        val uuid = UUID.fromString("123e4567-e89b-12d3-a456-426614174000")
        val ownerId = OwnerId.from(uuid)
        assertEquals(uuid, ownerId.value)
        assertEquals("123e4567-e89b-12d3-a456-426614174000", ownerId.value.toString())
    }

    @Test
    fun `should create OwnerId with all zeros UUID`() {
        val uuid = UUID(0, 0)
        val ownerId = OwnerId.from(uuid)
        assertEquals(uuid, ownerId.value)
        assertEquals("00000000-0000-0000-0000-000000000000", ownerId.value.toString())
    }

    @Test
    fun `should create OwnerId with all ones UUID`() {
        val uuid = UUID(-1, -1)
        val ownerId = OwnerId.from(uuid)
        assertEquals(uuid, ownerId.value)
        assertEquals("ffffffff-ffff-ffff-ffff-ffffffffffff", ownerId.value.toString())
    }

    @Test
    fun `should create OwnerId with maximum long values`() {
        val uuid = UUID(Long.MAX_VALUE, Long.MAX_VALUE)
        val ownerId = OwnerId.from(uuid)
        assertEquals(uuid, ownerId.value)
    }

    @Test
    fun `should create OwnerId with minimum long values`() {
        val uuid = UUID(Long.MIN_VALUE, Long.MIN_VALUE)
        val ownerId = OwnerId.from(uuid)
        assertEquals(uuid, ownerId.value)
    }

    @Test
    fun `should throw when from() receives null UUID`() {
        assertFailsWith<IllegalArgumentException> {
            OwnerId.from(null)
        }
    }

    @Test
    fun `should create different OwnerIds with new()`() {
        val ownerId1 = OwnerId.new()
        val ownerId2 = OwnerId.new()
        assertNotEquals(ownerId1.value, ownerId2.value)
    }

    @Test
    fun `should create multiple different OwnerIds`() {
        val ids = (1..100).map { OwnerId.new() }
        val uniqueIds = ids.map { it.value }.toSet()
        assertEquals(100, uniqueIds.size)
    }

    @Test
    fun `should maintain equality for same UUID`() {
        val uuid = UUID.randomUUID()
        val ownerId1 = OwnerId.from(uuid)
        val ownerId2 = OwnerId.from(uuid)
        assertEquals(ownerId1, ownerId2)
        assertEquals(ownerId1.value, ownerId2.value)
    }

    @Test
    fun `should not be equal for different UUIDs`() {
        val uuid1 = UUID.randomUUID()
        val uuid2 = UUID.randomUUID()
        val ownerId1 = OwnerId.from(uuid1)
        val ownerId2 = OwnerId.from(uuid2)
        assertNotEquals(ownerId1, ownerId2)
        assertNotEquals(ownerId1.value, ownerId2.value)
    }

    @Test
    fun `should have same hashCode for same UUID`() {
        val uuid = UUID.randomUUID()
        val ownerId1 = OwnerId.from(uuid)
        val ownerId2 = OwnerId.from(uuid)
        assertEquals(ownerId1.hashCode(), ownerId2.hashCode())
    }

    @Test
    fun `should preserve UUID through from()`() {
        val uuid = UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890")
        val ownerId = OwnerId.from(uuid)
        assertEquals("a1b2c3d4-e5f6-7890-abcd-ef1234567890", ownerId.value.toString())
    }

    @Test
    fun `should create OwnerId with UUID version 4 pattern`() {
        val ownerId = OwnerId.new()
        val version = (ownerId.value.mostSignificantBits shr 12) and 0xf
        assertEquals(4, version)
    }

    @Test
    fun `should create OwnerId with specific most and least significant bits`() {
        val uuid = UUID(123456789L, 987654321L)
        val ownerId = OwnerId.from(uuid)
        assertEquals(123456789L, ownerId.value.mostSignificantBits)
        assertEquals(987654321L, ownerId.value.leastSignificantBits)
    }

    @Test
    fun `should create multiple OwnerIds and maintain uniqueness`() {
        val ownerId1 = OwnerId.new()
        val ownerId2 = OwnerId.new()
        val ownerId3 = OwnerId.new()

        assertNotEquals(ownerId1, ownerId2)
        assertNotEquals(ownerId2, ownerId3)
        assertNotEquals(ownerId1, ownerId3)
    }

    @Test
    fun `should create OwnerId with zero most significant bits`() {
        val uuid = UUID(0, 123456789L)
        val ownerId = OwnerId.from(uuid)
        assertEquals(0L, ownerId.value.mostSignificantBits)
        assertEquals(123456789L, ownerId.value.leastSignificantBits)
    }

    @Test
    fun `should create OwnerId with zero least significant bits`() {
        val uuid = UUID(123456789L, 0)
        val ownerId = OwnerId.from(uuid)
        assertEquals(123456789L, ownerId.value.mostSignificantBits)
        assertEquals(0L, ownerId.value.leastSignificantBits)
    }

    @Test
    fun `should create OwnerId with positive and negative long values`() {
        val uuid = UUID(Long.MAX_VALUE, Long.MIN_VALUE)
        val ownerId = OwnerId.from(uuid)
        assertEquals(Long.MAX_VALUE, ownerId.value.mostSignificantBits)
        assertEquals(Long.MIN_VALUE, ownerId.value.leastSignificantBits)
    }
}
