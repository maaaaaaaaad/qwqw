package com.mad.jellomarkserver.reservation.core.application

import com.mad.jellomarkserver.member.core.domain.exception.MemberNotFoundException
import com.mad.jellomarkserver.member.core.domain.model.SocialId
import com.mad.jellomarkserver.member.port.driven.MemberPort
import com.mad.jellomarkserver.reservation.core.domain.model.Reservation
import com.mad.jellomarkserver.reservation.core.domain.model.ReservationStatus
import com.mad.jellomarkserver.reservation.port.driven.ReservationPort
import com.mad.jellomarkserver.reservation.port.driving.ListPendingReviewReservationsCommand
import com.mad.jellomarkserver.reservation.port.driving.ListPendingReviewReservationsUseCase
import com.mad.jellomarkserver.review.port.driven.ShopReviewPort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ListPendingReviewReservationsUseCaseImpl(
    private val reservationPort: ReservationPort,
    private val shopReviewPort: ShopReviewPort,
    private val memberPort: MemberPort
) : ListPendingReviewReservationsUseCase {

    @Transactional(readOnly = true)
    override fun execute(command: ListPendingReviewReservationsCommand): List<Reservation> {
        val member = memberPort.findBySocialId(SocialId(command.socialId))
            ?: throw MemberNotFoundException(command.socialId)

        val reservations = reservationPort.findByMemberId(member.id)
        val completedReservations = reservations.filter { it.status == ReservationStatus.COMPLETED }
        if (completedReservations.isEmpty()) return emptyList()

        val reviewedShopIds = shopReviewPort.findReviewedShopIdsByMemberId(member.id)
        return completedReservations.filter { it.shopId !in reviewedShopIds }
    }
}
