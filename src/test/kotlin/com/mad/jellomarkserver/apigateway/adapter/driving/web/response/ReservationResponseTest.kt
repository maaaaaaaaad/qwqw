package com.mad.jellomarkserver.apigateway.adapter.driving.web.response

import com.mad.jellomarkserver.beautishop.core.domain.model.ShopId
import com.mad.jellomarkserver.designer.core.domain.model.DesignerId
import com.mad.jellomarkserver.member.core.domain.model.MemberId
import com.mad.jellomarkserver.reservation.core.domain.model.Reservation
import com.mad.jellomarkserver.reservation.core.domain.model.ReservationMemo
import com.mad.jellomarkserver.treatment.core.domain.model.TreatmentId
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

class ReservationResponseTest {

    private val fixedClock = Clock.fixed(Instant.parse("2025-01-01T00:00:00Z"), ZoneId.of("UTC"))

    @Test
    fun `should include designerName when designer provided`() {
        val designerId = DesignerId.new()
        val reservation = Reservation.create(
            shopId = ShopId.new(),
            memberId = MemberId.new(),
            treatmentId = TreatmentId.new(),
            reservationDate = LocalDate.of(2025, 3, 15),
            startTime = LocalTime.of(14, 0),
            endTime = LocalTime.of(15, 0),
            memo = ReservationMemo.of("첫 방문입니다"),
            designerId = designerId,
            clock = fixedClock
        )

        val response = ReservationResponse.from(
            reservation = reservation,
            shopName = "젤로네일",
            treatmentName = "젤네일",
            treatmentPrice = 30000,
            treatmentDuration = 60,
            memberNickname = "홍길동",
            designerName = "김디자이너"
        )

        assertThat(response.designerId).isEqualTo(designerId.value.toString())
        assertThat(response.designerName).isEqualTo("김디자이너")
        assertThat(response.shopName).isEqualTo("젤로네일")
        assertThat(response.memberNickname).isEqualTo("홍길동")
    }

    @Test
    fun `should have null designerName when reservation has no designer`() {
        val reservation = Reservation.create(
            shopId = ShopId.new(),
            memberId = MemberId.new(),
            treatmentId = TreatmentId.new(),
            reservationDate = LocalDate.of(2025, 3, 15),
            startTime = LocalTime.of(14, 0),
            endTime = LocalTime.of(15, 0),
            memo = null,
            designerId = null,
            clock = fixedClock
        )

        val response = ReservationResponse.from(reservation = reservation)

        assertThat(response.designerId).isNull()
        assertThat(response.designerName).isNull()
    }

    @Test
    fun `should have null designerName even when designerId present but name not resolved`() {
        val designerId = DesignerId.new()
        val reservation = Reservation.create(
            shopId = ShopId.new(),
            memberId = MemberId.new(),
            treatmentId = TreatmentId.new(),
            reservationDate = LocalDate.of(2025, 3, 15),
            startTime = LocalTime.of(14, 0),
            endTime = LocalTime.of(15, 0),
            memo = null,
            designerId = designerId,
            clock = fixedClock
        )

        val response = ReservationResponse.from(
            reservation = reservation,
            designerName = null
        )

        assertThat(response.designerId).isEqualTo(designerId.value.toString())
        assertThat(response.designerName).isNull()
    }
}
