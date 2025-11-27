package com.mad.jellomarkserver.auth.core.domain.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertFailsWith

class AuthIdTest {

    @Test
    fun `should create AuthId with new()`() {
        val authId = AuthId.new()
        assertEquals(36, authId.value.toString().length)
    }

    @Test
    fun `should create AuthId with from() using valid UUID`() {
        val uuid = UUID.randomUUID()
        val authId = AuthId.from(uuid)
        assertEquals(uuid, authId.value)
    }

    @Test
    fun `should create AuthId with from() using specific UUID`() {
        val uuid = UUID.fromString("123e4567-e89b-12d3-a456-426614174000")
        val authId = AuthId.from(uuid)
        assertEquals(uuid, authId.value)
        assertEquals("123e4567-e89b-12d3-a456-426614174000", authId.value.toString())
    }

    @Test
    fun `should create AuthId with all zeros UUID`() {
        val uuid = UUID(0, 0)
        val authId = AuthId.from(uuid)
        assertEquals(uuid, authId.value)
        assertEquals("00000000-0000-0000-0000-000000000000", authId.value.toString())
    }

    @Test
    fun `should create AuthId with all ones UUID`() {
        val uuid = UUID(-1, -1)
        val authId = AuthId.from(uuid)
        assertEquals(uuid, authId.value)
        assertEquals("ffffffff-ffff-ffff-ffff-ffffffffffff", authId.value.toString())
    }

    @Test
    fun `should throw when from() receives null UUID`() {
        assertFailsWith<IllegalArgumentException> {
            AuthId.from(null)
        }
    }

    @Test
    fun `should create different AuthIds with new()`() {
        val authId1 = AuthId.new()
        val authId2 = AuthId.new()
        assertNotEquals(authId1.value, authId2.value)
    }

    @Test
    fun `should create multiple different AuthIds`() {
        val ids = (1..100).map { AuthId.new() }
        val uniqueIds = ids.map { it.value }.toSet()
        assertEquals(100, uniqueIds.size)
    }

    @Test
    fun `should maintain equality for same UUID`() {
        val uuid = UUID.randomUUID()
        val authId1 = AuthId.from(uuid)
        val authId2 = AuthId.from(uuid)
        assertEquals(authId1, authId2)
        assertEquals(authId1.value, authId2.value)
    }

    @Test
    fun `should not be equal for different UUIDs`() {
        val uuid1 = UUID.randomUUID()
        val uuid2 = UUID.randomUUID()
        val authId1 = AuthId.from(uuid1)
        val authId2 = AuthId.from(uuid2)
        assertNotEquals(authId1, authId2)
        assertNotEquals(authId1.value, authId2.value)
    }

    @Test
    fun `should have same hashCode for same UUID`() {
        val uuid = UUID.randomUUID()
        val authId1 = AuthId.from(uuid)
        val authId2 = AuthId.from(uuid)
        assertEquals(authId1.hashCode(), authId2.hashCode())
    }

    @Test
    fun `should preserve UUID through from()`() {
        val uuid = UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890")
        val authId = AuthId.from(uuid)
        assertEquals("a1b2c3d4-e5f6-7890-abcd-ef1234567890", authId.value.toString())
    }

    @Test
    fun `should create AuthId with UUID version 4 pattern`() {
        val authId = AuthId.new()
        val version = (authId.value.mostSignificantBits shr 12) and 0xf
        assertEquals(4, version)
    }

    @Test
    fun `should create multiple AuthIds and maintain uniqueness`() {
        val authId1 = AuthId.new()
        val authId2 = AuthId.new()
        val authId3 = AuthId.new()

        assertNotEquals(authId1, authId2)
        assertNotEquals(authId2, authId3)
        assertNotEquals(authId1, authId3)
    }
}
