package com.mad.jellomarkserver.reservation.core.domain.model

import com.mad.jellomarkserver.reservation.core.domain.exception.InvalidReservationMemoException

@JvmInline
value class ReservationMemo private constructor(val value: String) {
    companion object {
        private const val MAX_LENGTH = 200

        fun of(memo: String): ReservationMemo {
            val trimmed = memo.trim()
            if (trimmed.isEmpty() || trimmed.length > MAX_LENGTH) {
                throw InvalidReservationMemoException(trimmed)
            }
            return ReservationMemo(trimmed)
        }

        fun ofNullable(memo: String?): ReservationMemo? {
            if (memo.isNullOrBlank()) return null
            return of(memo)
        }
    }
}
