package com.mad.jellomarkserver.reservation.core.domain.exception

class ReservationTimeConflictException(shopId: String, date: String, startTime: String, endTime: String) : RuntimeException(
    "Reservation time conflict at shop $shopId on $date between $startTime and $endTime"
)
