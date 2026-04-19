package com.mad.jellomarkserver.member.core.application

import com.mad.jellomarkserver.favorite.port.driven.FavoritePort
import com.mad.jellomarkserver.member.core.domain.exception.InvalidWithdrawalReasonException
import com.mad.jellomarkserver.member.core.domain.exception.MemberNotFoundException
import com.mad.jellomarkserver.member.core.domain.model.SocialId
import com.mad.jellomarkserver.member.core.domain.model.SocialProvider
import com.mad.jellomarkserver.member.port.driven.MemberPort
import com.mad.jellomarkserver.member.port.driving.WithdrawMemberCommand
import com.mad.jellomarkserver.member.port.driving.WithdrawMemberUseCase
import com.mad.jellomarkserver.notification.core.domain.model.UserRole
import com.mad.jellomarkserver.notification.port.driven.DeviceTokenPort
import com.mad.jellomarkserver.reservation.core.domain.model.ReservationStatus
import com.mad.jellomarkserver.reservation.port.driven.ReservationPort
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class WithdrawMemberUseCaseImpl(
    private val memberPort: MemberPort,
    private val favoritePort: FavoritePort,
    private val deviceTokenPort: DeviceTokenPort,
    private val reservationPort: ReservationPort
) : WithdrawMemberUseCase {

    private val log = LoggerFactory.getLogger(WithdrawMemberUseCaseImpl::class.java)

    @Transactional
    override fun withdraw(command: WithdrawMemberCommand) {
        val reason = command.reason.trim()
        if (reason.length < MIN_REASON_LENGTH) {
            throw InvalidWithdrawalReasonException(reason)
        }

        val provider = SocialProvider.valueOf(command.socialProvider)
        val socialId = SocialId(command.socialId)
        val member = memberPort.findBySocial(provider, socialId)
            ?: throw MemberNotFoundException(command.socialId)

        favoritePort.deleteAllByMemberId(member.id)
        deviceTokenPort.deleteAllByUserIdAndUserRole(member.id.value, UserRole.MEMBER)

        val reservations = reservationPort.findByMemberId(member.id)
        reservations.filter { it.status == ReservationStatus.PENDING || it.status == ReservationStatus.CONFIRMED }
            .forEach { reservation ->
                val cancelled = reservation.cancel()
                reservationPort.save(cancelled)
            }

        memberPort.softDelete(member.id)

        log.info("Member withdrawn: memberId={}, reason={}", member.id.value, reason)
    }

    companion object {
        private const val MIN_REASON_LENGTH = 2
    }
}
