package com.mad.jellomarkserver.member.core.domain.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test
import java.util.UUID
import kotlin.test.assertFailsWith

class MemberIdTest {

    @Test
    fun `should create MemberId with new()`() {
        val memberId = MemberId.new()
        assertEquals(36, memberId.value.toString().length)
    }

    @Test
    fun `should create MemberId with from() using valid UUID`() {
        val uuid = UUID.randomUUID()
        val memberId = MemberId.from(uuid)
        assertEquals(uuid, memberId.value)
    }

    @Test
    fun `should create MemberId with from() using specific UUID`() {
        val uuid = UUID.fromString("123e4567-e89b-12d3-a456-426614174000")
        val memberId = MemberId.from(uuid)
        assertEquals(uuid, memberId.value)
        assertEquals("123e4567-e89b-12d3-a456-426614174000", memberId.value.toString())
    }

    @Test
    fun `should create MemberId with all zeros UUID`() {
        val uuid = UUID(0, 0)
        val memberId = MemberId.from(uuid)
        assertEquals(uuid, memberId.value)
        assertEquals("00000000-0000-0000-0000-000000000000", memberId.value.toString())
    }

    @Test
    fun `should create MemberId with all ones UUID`() {
        val uuid = UUID(-1, -1)
        val memberId = MemberId.from(uuid)
        assertEquals(uuid, memberId.value)
        assertEquals("ffffffff-ffff-ffff-ffff-ffffffffffff", memberId.value.toString())
    }

    @Test
    fun `should create MemberId with maximum long values`() {
        val uuid = UUID(Long.MAX_VALUE, Long.MAX_VALUE)
        val memberId = MemberId.from(uuid)
        assertEquals(uuid, memberId.value)
    }

    @Test
    fun `should create MemberId with minimum long values`() {
        val uuid = UUID(Long.MIN_VALUE, Long.MIN_VALUE)
        val memberId = MemberId.from(uuid)
        assertEquals(uuid, memberId.value)
    }

    @Test
    fun `should throw when from() receives null UUID`() {
        assertFailsWith<IllegalArgumentException> {
            MemberId.from(null)
        }
    }

    @Test
    fun `should create different MemberIds with new()`() {
        val memberId1 = MemberId.new()
        val memberId2 = MemberId.new()
        assertNotEquals(memberId1.value, memberId2.value)
    }

    @Test
    fun `should create multiple different MemberIds`() {
        val ids = (1..100).map { MemberId.new() }
        val uniqueIds = ids.map { it.value }.toSet()
        assertEquals(100, uniqueIds.size)
    }

    @Test
    fun `should maintain equality for same UUID`() {
        val uuid = UUID.randomUUID()
        val memberId1 = MemberId.from(uuid)
        val memberId2 = MemberId.from(uuid)
        assertEquals(memberId1, memberId2)
        assertEquals(memberId1.value, memberId2.value)
    }

    @Test
    fun `should not be equal for different UUIDs`() {
        val uuid1 = UUID.randomUUID()
        val uuid2 = UUID.randomUUID()
        val memberId1 = MemberId.from(uuid1)
        val memberId2 = MemberId.from(uuid2)
        assertNotEquals(memberId1, memberId2)
        assertNotEquals(memberId1.value, memberId2.value)
    }

    @Test
    fun `should have same hashCode for same UUID`() {
        val uuid = UUID.randomUUID()
        val memberId1 = MemberId.from(uuid)
        val memberId2 = MemberId.from(uuid)
        assertEquals(memberId1.hashCode(), memberId2.hashCode())
    }

    @Test
    fun `should preserve UUID through from()`() {
        val uuid = UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890")
        val memberId = MemberId.from(uuid)
        assertEquals("a1b2c3d4-e5f6-7890-abcd-ef1234567890", memberId.value.toString())
    }

    @Test
    fun `should create MemberId with UUID version 4 pattern`() {
        val memberId = MemberId.new()
        val version = (memberId.value.mostSignificantBits shr 12) and 0xf
        assertEquals(4, version)
    }

    @Test
    fun `should create MemberId with specific most and least significant bits`() {
        val uuid = UUID(123456789L, 987654321L)
        val memberId = MemberId.from(uuid)
        assertEquals(123456789L, memberId.value.mostSignificantBits)
        assertEquals(987654321L, memberId.value.leastSignificantBits)
    }

    @Test
    fun `should create multiple MemberIds and maintain uniqueness`() {
        val memberId1 = MemberId.new()
        val memberId2 = MemberId.new()
        val memberId3 = MemberId.new()

        assertNotEquals(memberId1, memberId2)
        assertNotEquals(memberId2, memberId3)
        assertNotEquals(memberId1, memberId3)
    }

    @Test
    fun `should create MemberId with zero most significant bits`() {
        val uuid = UUID(0, 123456789L)
        val memberId = MemberId.from(uuid)
        assertEquals(0L, memberId.value.mostSignificantBits)
        assertEquals(123456789L, memberId.value.leastSignificantBits)
    }

    @Test
    fun `should create MemberId with zero least significant bits`() {
        val uuid = UUID(123456789L, 0)
        val memberId = MemberId.from(uuid)
        assertEquals(123456789L, memberId.value.mostSignificantBits)
        assertEquals(0L, memberId.value.leastSignificantBits)
    }

    @Test
    fun `should create MemberId with positive and negative long values`() {
        val uuid = UUID(Long.MAX_VALUE, Long.MIN_VALUE)
        val memberId = MemberId.from(uuid)
        assertEquals(Long.MAX_VALUE, memberId.value.mostSignificantBits)
        assertEquals(Long.MIN_VALUE, memberId.value.leastSignificantBits)
    }
}
