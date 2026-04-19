package com.mad.jellomarkserver.owner.core.application

import com.mad.jellomarkserver.auth.core.domain.exception.AuthenticationFailedException
import com.mad.jellomarkserver.auth.core.domain.model.AuthEmail
import com.mad.jellomarkserver.auth.core.domain.model.RawPassword
import com.mad.jellomarkserver.auth.port.driven.AuthPort
import com.mad.jellomarkserver.beautishop.port.driven.BeautishopPort
import com.mad.jellomarkserver.favorite.port.driven.FavoritePort
import com.mad.jellomarkserver.notification.core.domain.model.UserRole
import com.mad.jellomarkserver.notification.port.driven.DeviceTokenPort
import com.mad.jellomarkserver.owner.core.domain.exception.InvalidWithdrawalReasonException
import com.mad.jellomarkserver.owner.core.domain.exception.OwnerNotFoundException
import com.mad.jellomarkserver.owner.core.domain.model.OwnerEmail
import com.mad.jellomarkserver.owner.port.driven.OwnerPort
import com.mad.jellomarkserver.owner.port.driving.WithdrawOwnerCommand
import com.mad.jellomarkserver.owner.port.driving.WithdrawOwnerUseCase
import com.mad.jellomarkserver.reservation.port.driven.ReservationPort
import com.mad.jellomarkserver.review.port.driven.ShopReviewPort
import com.mad.jellomarkserver.treatment.port.driven.TreatmentPort
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class WithdrawOwnerUseCaseImpl(
    private val ownerPort: OwnerPort,
    private val authPort: AuthPort,
    private val beautishopPort: BeautishopPort,
    private val treatmentPort: TreatmentPort,
    private val reservationPort: ReservationPort,
    private val reviewPort: ShopReviewPort,
    private val favoritePort: FavoritePort,
    private val deviceTokenPort: DeviceTokenPort
) : WithdrawOwnerUseCase {

    private val log = LoggerFactory.getLogger(WithdrawOwnerUseCaseImpl::class.java)

    @Transactional
    override fun withdraw(command: WithdrawOwnerCommand) {
        val reason = command.reason.trim()
        if (reason.length < MIN_REASON_LENGTH) {
            throw InvalidWithdrawalReasonException(reason)
        }

        val authEmail = AuthEmail.of(command.email)
        val auth = authPort.findByEmail(authEmail)
            ?: throw AuthenticationFailedException(command.email)

        val rawPassword = RawPassword.of(command.password)
        if (!auth.hashedPassword.matches(rawPassword)) {
            throw AuthenticationFailedException(command.email)
        }

        val ownerEmail = OwnerEmail.of(command.email)
        val owner = ownerPort.findByEmail(ownerEmail)
            ?: throw OwnerNotFoundException(command.email)

        val shops = beautishopPort.findByOwnerId(owner.id)
        shops.forEach { shop ->
            reviewPort.deleteAllByShopId(shop.id)
            favoritePort.deleteAllByShopId(shop.id)
            reservationPort.deleteAllByShopId(shop.id)
            treatmentPort.deleteAllByShopId(shop.id)
            beautishopPort.delete(shop.id)
        }

        deviceTokenPort.deleteAllByUserIdAndUserRole(owner.id.value, UserRole.OWNER)
        authPort.deleteByEmail(authEmail)
        ownerPort.delete(owner.id)

        log.info("Owner withdrawn: ownerId={}, shopCount={}, reason={}", owner.id.value, shops.size, reason)
    }

    companion object {
        private const val MIN_REASON_LENGTH = 2
    }
}
