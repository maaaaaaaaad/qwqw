package com.mad.jellomarkserver.reservation.adapter.driven.persistence.entity

import jakarta.persistence.*
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.util.*

@Entity
@Table(
    name = "reservations",
    indexes = [
        Index(name = "idx_reservations_shop_id", columnList = "shop_id"),
        Index(name = "idx_reservations_member_id", columnList = "member_id"),
        Index(name = "idx_reservations_shop_date", columnList = "shop_id, reservation_date"),
    ]
)
class ReservationJpaEntity(
    @Id
    @Column(name = "id", nullable = false)
    var id: UUID,

    @Column(name = "shop_id", nullable = false)
    var shopId: UUID,

    @Column(name = "member_id", nullable = false)
    var memberId: UUID,

    @Column(name = "treatment_id", nullable = false)
    var treatmentId: UUID,

    @Column(name = "reservation_date", nullable = false)
    var reservationDate: LocalDate,

    @Column(name = "start_time", nullable = false)
    var startTime: LocalTime,

    @Column(name = "end_time", nullable = false)
    var endTime: LocalTime,

    @Column(name = "status", nullable = false, length = 20)
    var status: String,

    @Column(name = "memo", nullable = true, length = 200)
    var memo: String?,

    @Column(name = "rejection_reason", nullable = true, length = 200)
    var rejectionReason: String?,

    @Column(name = "created_at", nullable = false)
    var createdAt: Instant,

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant
)
