package com.mad.jellomarkserver.reservation.core.domain.model

import com.mad.jellomarkserver.reservation.core.domain.exception.InvalidReservationMemoException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class ReservationMemoTest {

    @Test
    fun `should create memo with valid content`() {
        val memo = ReservationMemo.of("예약 메모입니다")

        assertEquals("예약 메모입니다", memo.value)
    }

    @Test
    fun `should trim whitespace`() {
        val memo = ReservationMemo.of("  메모  ")

        assertEquals("메모", memo.value)
    }

    @Test
    fun `should throw when memo is empty`() {
        assertFailsWith<InvalidReservationMemoException> {
            ReservationMemo.of("")
        }
    }

    @Test
    fun `should throw when memo is blank`() {
        assertFailsWith<InvalidReservationMemoException> {
            ReservationMemo.of("   ")
        }
    }

    @Test
    fun `should throw when memo exceeds 200 characters`() {
        val longMemo = "a".repeat(201)

        assertFailsWith<InvalidReservationMemoException> {
            ReservationMemo.of(longMemo)
        }
    }

    @Test
    fun `should create memo with exactly 200 characters`() {
        val maxMemo = "a".repeat(200)
        val memo = ReservationMemo.of(maxMemo)

        assertEquals(200, memo.value.length)
    }

    @Test
    fun `ofNullable should return null for null input`() {
        assertNull(ReservationMemo.ofNullable(null))
    }

    @Test
    fun `ofNullable should return null for blank input`() {
        assertNull(ReservationMemo.ofNullable("   "))
    }

    @Test
    fun `ofNullable should return memo for valid input`() {
        val memo = ReservationMemo.ofNullable("메모")

        assertEquals("메모", memo?.value)
    }
}
