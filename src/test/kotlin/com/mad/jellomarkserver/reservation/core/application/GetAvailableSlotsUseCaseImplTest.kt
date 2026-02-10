package com.mad.jellomarkserver.reservation.core.application

import com.mad.jellomarkserver.beautishop.core.domain.model.*
import com.mad.jellomarkserver.beautishop.core.domain.exception.BeautishopNotFoundException
import com.mad.jellomarkserver.beautishop.port.driven.BeautishopPort
import com.mad.jellomarkserver.member.core.domain.model.MemberId
import com.mad.jellomarkserver.reservation.core.domain.model.Reservation
import com.mad.jellomarkserver.reservation.core.domain.model.ReservationMemo
import com.mad.jellomarkserver.reservation.port.driven.ReservationPort
import com.mad.jellomarkserver.reservation.port.driving.GetAvailableSlotsQuery
import com.mad.jellomarkserver.treatment.core.domain.exception.TreatmentNotFoundException
import com.mad.jellomarkserver.treatment.core.domain.model.*
import com.mad.jellomarkserver.treatment.port.driven.TreatmentPort
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import java.time.*
import kotlin.test.assertFailsWith

@ExtendWith(MockitoExtension::class)
class GetAvailableSlotsUseCaseImplTest {

    @Mock
    private lateinit var beautishopPort: BeautishopPort

    @Mock
    private lateinit var treatmentPort: TreatmentPort

    @Mock
    private lateinit var reservationPort: ReservationPort

    private lateinit var useCase: GetAvailableSlotsUseCaseImpl

    private val fixedClock = Clock.fixed(Instant.parse("2025-06-01T00:00:00Z"), ZoneId.of("UTC"))

    @BeforeEach
    fun setup() {
        useCase = GetAvailableSlotsUseCaseImpl(beautishopPort, treatmentPort, reservationPort, fixedClock)
    }

    private fun createShop(shopId: ShopId, schedule: Map<String, String>): Beautishop {
        return Beautishop.reconstruct(
            id = shopId,
            name = ShopName.of("테스트샵"),
            regNum = ShopRegNum.of("123-45-67890"),
            phoneNumber = ShopPhoneNumber.of("010-1234-5678"),
            address = ShopAddress.of("서울시 강남구"),
            gps = ShopGPS.of(37.5, 127.0),
            operatingTime = OperatingTime.of(schedule),
            description = null,
            images = ShopImages.of(emptyList()),
            averageRating = AverageRating.zero(),
            reviewCount = ReviewCount.zero(),
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
    }

    private fun createTreatment(shopId: ShopId, durationMinutes: Int = 60): Treatment {
        return Treatment.reconstruct(
            id = TreatmentId.new(),
            shopId = shopId,
            name = TreatmentName.of("젤네일"),
            price = TreatmentPrice.of(50000),
            duration = TreatmentDuration.of(durationMinutes),
            description = null,
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
    }

    private fun createReservation(
        shopId: ShopId,
        date: LocalDate,
        startTime: LocalTime,
        endTime: LocalTime
    ): Reservation {
        return Reservation.create(
            shopId = shopId,
            memberId = MemberId.new(),
            treatmentId = TreatmentId.new(),
            reservationDate = date,
            startTime = startTime,
            endTime = endTime,
            memo = ReservationMemo.ofNullable("test"),
            clock = fixedClock
        )
    }

    @Test
    fun `should return all slots as available when no existing reservations`() {
        val shopId = ShopId.new()
        val schedule = mapOf("sunday" to "10:00-14:00")
        val shop = createShop(shopId, schedule)
        val treatment = createTreatment(shopId, durationMinutes = 60)

        whenever(beautishopPort.findById(shopId)).thenReturn(shop)
        whenever(treatmentPort.findById(treatment.id)).thenReturn(treatment)
        whenever(reservationPort.findByShopIdAndDate(shopId, LocalDate.of(2025, 6, 15)))
            .thenReturn(emptyList())

        val query = GetAvailableSlotsQuery(
            shopId = shopId.value.toString(),
            treatmentId = treatment.id.value.toString(),
            date = "2025-06-15"
        )

        val result = useCase.execute(query)

        assertEquals(LocalDate.of(2025, 6, 15), result.date)
        assertEquals(LocalTime.of(10, 0), result.openTime)
        assertEquals(LocalTime.of(14, 0), result.closeTime)
        assertEquals(7, result.slots.size)
        assertTrue(result.slots.all { it.available })
        assertEquals(LocalTime.of(10, 0), result.slots.first().startTime)
        assertEquals(LocalTime.of(13, 0), result.slots.last().startTime)
    }

    @Test
    fun `should mark overlapping slots as unavailable`() {
        val shopId = ShopId.new()
        val schedule = mapOf("sunday" to "10:00-16:00")
        val shop = createShop(shopId, schedule)
        val treatment = createTreatment(shopId, durationMinutes = 60)
        val date = LocalDate.of(2025, 6, 15)

        val existingReservation = createReservation(
            shopId = shopId,
            date = date,
            startTime = LocalTime.of(11, 0),
            endTime = LocalTime.of(12, 0)
        )

        whenever(beautishopPort.findById(shopId)).thenReturn(shop)
        whenever(treatmentPort.findById(treatment.id)).thenReturn(treatment)
        whenever(reservationPort.findByShopIdAndDate(shopId, date))
            .thenReturn(listOf(existingReservation))

        val query = GetAvailableSlotsQuery(
            shopId = shopId.value.toString(),
            treatmentId = treatment.id.value.toString(),
            date = "2025-06-15"
        )

        val result = useCase.execute(query)

        val slot1100 = result.slots.find { it.startTime == LocalTime.of(11, 0) }
        assertNotNull(slot1100)
        assertFalse(slot1100!!.available)

        val slot1030 = result.slots.find { it.startTime == LocalTime.of(10, 30) }
        assertNotNull(slot1030)
        assertFalse(slot1030!!.available)

        val slot1000 = result.slots.find { it.startTime == LocalTime.of(10, 0) }
        assertNotNull(slot1000)
        assertTrue(slot1000!!.available)

        val slot1200 = result.slots.find { it.startTime == LocalTime.of(12, 0) }
        assertNotNull(slot1200)
        assertTrue(slot1200!!.available)
    }

    @Test
    fun `should return empty slots when shop is closed on the day`() {
        val shopId = ShopId.new()
        val schedule = mapOf("sunday" to "closed")
        val shop = createShop(shopId, schedule)
        val treatment = createTreatment(shopId, durationMinutes = 60)

        whenever(beautishopPort.findById(shopId)).thenReturn(shop)
        whenever(treatmentPort.findById(treatment.id)).thenReturn(treatment)

        val query = GetAvailableSlotsQuery(
            shopId = shopId.value.toString(),
            treatmentId = treatment.id.value.toString(),
            date = "2025-06-15"
        )

        val result = useCase.execute(query)

        assertTrue(result.slots.isEmpty())
    }

    @Test
    fun `should return empty slots when day has no schedule entry`() {
        val shopId = ShopId.new()
        val schedule = mapOf("monday" to "10:00-18:00")
        val shop = createShop(shopId, schedule)
        val treatment = createTreatment(shopId, durationMinutes = 60)

        whenever(beautishopPort.findById(shopId)).thenReturn(shop)
        whenever(treatmentPort.findById(treatment.id)).thenReturn(treatment)

        val query = GetAvailableSlotsQuery(
            shopId = shopId.value.toString(),
            treatmentId = treatment.id.value.toString(),
            date = "2025-06-15"
        )

        val result = useCase.execute(query)

        assertTrue(result.slots.isEmpty())
    }

    @Test
    fun `should throw when shop not found`() {
        val shopId = ShopId.new()
        val treatmentId = TreatmentId.new()

        whenever(beautishopPort.findById(shopId)).thenReturn(null)

        val query = GetAvailableSlotsQuery(
            shopId = shopId.value.toString(),
            treatmentId = treatmentId.value.toString(),
            date = "2025-06-15"
        )

        assertFailsWith<BeautishopNotFoundException> {
            useCase.execute(query)
        }
    }

    @Test
    fun `should throw when treatment not found`() {
        val shopId = ShopId.new()
        val treatmentId = TreatmentId.new()
        val schedule = mapOf("sunday" to "10:00-18:00")
        val shop = createShop(shopId, schedule)

        whenever(beautishopPort.findById(shopId)).thenReturn(shop)
        whenever(treatmentPort.findById(treatmentId)).thenReturn(null)

        val query = GetAvailableSlotsQuery(
            shopId = shopId.value.toString(),
            treatmentId = treatmentId.value.toString(),
            date = "2025-06-15"
        )

        assertFailsWith<TreatmentNotFoundException> {
            useCase.execute(query)
        }
    }

    @Test
    fun `should generate 30-minute interval slots`() {
        val shopId = ShopId.new()
        val schedule = mapOf("sunday" to "10:00-12:00")
        val shop = createShop(shopId, schedule)
        val treatment = createTreatment(shopId, durationMinutes = 30)

        whenever(beautishopPort.findById(shopId)).thenReturn(shop)
        whenever(treatmentPort.findById(treatment.id)).thenReturn(treatment)
        whenever(reservationPort.findByShopIdAndDate(shopId, LocalDate.of(2025, 6, 15)))
            .thenReturn(emptyList())

        val query = GetAvailableSlotsQuery(
            shopId = shopId.value.toString(),
            treatmentId = treatment.id.value.toString(),
            date = "2025-06-15"
        )

        val result = useCase.execute(query)

        assertEquals(4, result.slots.size)
        assertEquals(LocalTime.of(10, 0), result.slots[0].startTime)
        assertEquals(LocalTime.of(10, 30), result.slots[1].startTime)
        assertEquals(LocalTime.of(11, 0), result.slots[2].startTime)
        assertEquals(LocalTime.of(11, 30), result.slots[3].startTime)
    }

    @Test
    fun `should only consider PENDING and CONFIRMED reservations for overlap`() {
        val shopId = ShopId.new()
        val schedule = mapOf("sunday" to "10:00-14:00")
        val shop = createShop(shopId, schedule)
        val treatment = createTreatment(shopId, durationMinutes = 60)
        val date = LocalDate.of(2025, 6, 15)

        val confirmedReservation = createReservation(
            shopId = shopId,
            date = date,
            startTime = LocalTime.of(11, 0),
            endTime = LocalTime.of(12, 0)
        ).confirm(fixedClock)

        val cancelledReservation = createReservation(
            shopId = shopId,
            date = date,
            startTime = LocalTime.of(12, 0),
            endTime = LocalTime.of(13, 0)
        ).cancel(fixedClock)

        whenever(beautishopPort.findById(shopId)).thenReturn(shop)
        whenever(treatmentPort.findById(treatment.id)).thenReturn(treatment)
        whenever(reservationPort.findByShopIdAndDate(shopId, date))
            .thenReturn(listOf(confirmedReservation, cancelledReservation))

        val query = GetAvailableSlotsQuery(
            shopId = shopId.value.toString(),
            treatmentId = treatment.id.value.toString(),
            date = "2025-06-15"
        )

        val result = useCase.execute(query)

        val slot1100 = result.slots.find { it.startTime == LocalTime.of(11, 0) }
        assertFalse(slot1100!!.available)

        val slot1200 = result.slots.find { it.startTime == LocalTime.of(12, 0) }
        assertTrue(slot1200!!.available)
    }
}
