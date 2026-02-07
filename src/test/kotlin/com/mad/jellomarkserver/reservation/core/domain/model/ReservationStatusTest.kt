package com.mad.jellomarkserver.reservation.core.domain.model

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ReservationStatusTest {

    @Test
    fun `PENDING can transition to CONFIRMED`() {
        assertTrue(ReservationStatus.PENDING.canTransitionTo(ReservationStatus.CONFIRMED))
    }

    @Test
    fun `PENDING can transition to REJECTED`() {
        assertTrue(ReservationStatus.PENDING.canTransitionTo(ReservationStatus.REJECTED))
    }

    @Test
    fun `PENDING can transition to CANCELLED`() {
        assertTrue(ReservationStatus.PENDING.canTransitionTo(ReservationStatus.CANCELLED))
    }

    @Test
    fun `PENDING cannot transition to COMPLETED`() {
        assertFalse(ReservationStatus.PENDING.canTransitionTo(ReservationStatus.COMPLETED))
    }

    @Test
    fun `CONFIRMED can transition to COMPLETED`() {
        assertTrue(ReservationStatus.CONFIRMED.canTransitionTo(ReservationStatus.COMPLETED))
    }

    @Test
    fun `CONFIRMED can transition to NO_SHOW`() {
        assertTrue(ReservationStatus.CONFIRMED.canTransitionTo(ReservationStatus.NO_SHOW))
    }

    @Test
    fun `CONFIRMED can transition to CANCELLED`() {
        assertTrue(ReservationStatus.CONFIRMED.canTransitionTo(ReservationStatus.CANCELLED))
    }

    @Test
    fun `CONFIRMED cannot transition to PENDING`() {
        assertFalse(ReservationStatus.CONFIRMED.canTransitionTo(ReservationStatus.PENDING))
    }

    @Test
    fun `REJECTED cannot transition to any status`() {
        ReservationStatus.entries.forEach { target ->
            assertFalse(ReservationStatus.REJECTED.canTransitionTo(target))
        }
    }

    @Test
    fun `CANCELLED cannot transition to any status`() {
        ReservationStatus.entries.forEach { target ->
            assertFalse(ReservationStatus.CANCELLED.canTransitionTo(target))
        }
    }

    @Test
    fun `COMPLETED cannot transition to any status`() {
        ReservationStatus.entries.forEach { target ->
            assertFalse(ReservationStatus.COMPLETED.canTransitionTo(target))
        }
    }

    @Test
    fun `NO_SHOW cannot transition to any status`() {
        ReservationStatus.entries.forEach { target ->
            assertFalse(ReservationStatus.NO_SHOW.canTransitionTo(target))
        }
    }
}
