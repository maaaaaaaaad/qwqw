package com.mad.jellomarkserver.reservation.core.domain.model

import com.mad.jellomarkserver.reservation.core.domain.exception.InvalidRejectionReasonException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class RejectionReasonTest {

    @Test
    fun `should create reason with valid content`() {
        val reason = RejectionReason.of("해당 시간에 예약이 불가합니다")

        assertEquals("해당 시간에 예약이 불가합니다", reason.value)
    }

    @Test
    fun `should trim whitespace`() {
        val reason = RejectionReason.of("  사유  ")

        assertEquals("사유", reason.value)
    }

    @Test
    fun `should throw when reason is empty`() {
        assertFailsWith<InvalidRejectionReasonException> {
            RejectionReason.of("")
        }
    }

    @Test
    fun `should throw when reason is blank`() {
        assertFailsWith<InvalidRejectionReasonException> {
            RejectionReason.of("   ")
        }
    }

    @Test
    fun `should throw when reason exceeds 200 characters`() {
        val longReason = "a".repeat(201)

        assertFailsWith<InvalidRejectionReasonException> {
            RejectionReason.of(longReason)
        }
    }

    @Test
    fun `should create reason with exactly 200 characters`() {
        val maxReason = "a".repeat(200)
        val reason = RejectionReason.of(maxReason)

        assertEquals(200, reason.value.length)
    }
}
