package com.mad.jellomarkserver.reservation.core.domain.model

enum class ReservationStatus {
    PENDING,
    CONFIRMED,
    REJECTED,
    CANCELLED,
    COMPLETED,
    NO_SHOW;

    fun canTransitionTo(target: ReservationStatus): Boolean {
        return when (this) {
            PENDING -> target in setOf(CONFIRMED, REJECTED, CANCELLED)
            CONFIRMED -> target in setOf(COMPLETED, NO_SHOW, CANCELLED)
            REJECTED -> false
            CANCELLED -> false
            COMPLETED -> false
            NO_SHOW -> false
        }
    }
}
