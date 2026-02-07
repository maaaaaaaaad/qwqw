package com.mad.jellomarkserver.reservation.core.domain.model

import com.mad.jellomarkserver.reservation.core.domain.exception.InvalidRejectionReasonException

@JvmInline
value class RejectionReason private constructor(val value: String) {
    companion object {
        private const val MAX_LENGTH = 200

        fun of(reason: String): RejectionReason {
            val trimmed = reason.trim()
            if (trimmed.isEmpty() || trimmed.length > MAX_LENGTH) {
                throw InvalidRejectionReasonException(trimmed)
            }
            return RejectionReason(trimmed)
        }
    }
}
