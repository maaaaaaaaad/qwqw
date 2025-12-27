package com.mad.jellomarkserver.beautishop.core.domain.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test
import java.util.*

class ShopIdTest {

    @Test
    fun `should create new ShopId with random UUID`() {
        val shopId1 = ShopId.new()
        val shopId2 = ShopId.new()

        assertNotEquals(shopId1, shopId2)
        assertNotEquals(shopId1.value, shopId2.value)
    }

    @Test
    fun `should create ShopId from existing UUID`() {
        val uuid = UUID.randomUUID()
        val shopId = ShopId.from(uuid)

        assertEquals(uuid, shopId.value)
    }

    @Test
    fun `should create equal ShopId from same UUID`() {
        val uuid = UUID.randomUUID()
        val shopId1 = ShopId.from(uuid)
        val shopId2 = ShopId.from(uuid)

        assertEquals(shopId1, shopId2)
        assertEquals(shopId1.value, shopId2.value)
    }
}
