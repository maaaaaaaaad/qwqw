package com.mad.jellomarkserver.reservation.core.domain.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test
import java.util.*

class ReservationIdTest {

    @Test
    fun `should create new ReservationId with random UUID`() {
        val id1 = ReservationId.new()
        val id2 = ReservationId.new()

        assertNotEquals(id1, id2)
    }

    @Test
    fun `should create ReservationId from existing UUID`() {
        val uuid = UUID.randomUUID()
        val id = ReservationId.from(uuid)

        assertEquals(uuid, id.value)
    }

    @Test
    fun `should create equal ReservationId from same UUID`() {
        val uuid = UUID.randomUUID()
        val id1 = ReservationId.from(uuid)
        val id2 = ReservationId.from(uuid)

        assertEquals(id1, id2)
    }
}
