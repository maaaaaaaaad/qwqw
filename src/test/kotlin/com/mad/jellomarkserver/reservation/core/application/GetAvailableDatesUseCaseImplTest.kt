package com.mad.jellomarkserver.reservation.core.application

import com.mad.jellomarkserver.beautishop.core.domain.model.*
import com.mad.jellomarkserver.beautishop.port.driven.BeautishopPort
import com.mad.jellomarkserver.member.core.domain.model.MemberId
import com.mad.jellomarkserver.reservation.core.domain.model.Reservation
import com.mad.jellomarkserver.reservation.port.driven.ReservationPort
import com.mad.jellomarkserver.reservation.port.driving.GetAvailableDatesQuery
import com.mad.jellomarkserver.treatment.core.domain.model.*
import com.mad.jellomarkserver.treatment.port.driven.TreatmentPort
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import java.time.*

@ExtendWith(MockitoExtension::class)
class GetAvailableDatesUseCaseImplTest {

    @Mock
    private lateinit var beautishopPort: BeautishopPort

    @Mock
    private lateinit var treatmentPort: TreatmentPort

    @Mock
    private lateinit var reservationPort: ReservationPort

    private lateinit var useCase: GetAvailableDatesUseCaseImpl

    private val fixedClock = Clock.fixed(Instant.parse("2025-06-01T00:00:00Z"), ZoneId.of("UTC"))

    @BeforeEach
    fun setup() {
        useCase = GetAvailableDatesUseCaseImpl(beautishopPort, treatmentPort, reservationPort, fixedClock)
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

    @Test
    fun `should return dates that have available slots`() {
        val shopId = ShopId.new()
        val schedule = mapOf(
            "monday" to "10:00-18:00",
            "tuesday" to "10:00-18:00",
            "wednesday" to "closed",
            "thursday" to "10:00-18:00",
            "friday" to "10:00-18:00",
            "saturday" to "10:00-14:00",
            "sunday" to "closed"
        )
        val shop = createShop(shopId, schedule)
        val treatment = createTreatment(shopId, durationMinutes = 60)

        whenever(beautishopPort.findById(shopId)).thenReturn(shop)
        whenever(treatmentPort.findById(treatment.id)).thenReturn(treatment)
        whenever(reservationPort.findByShopIdAndDate(any(), any()))
            .thenReturn(emptyList())

        val query = GetAvailableDatesQuery(
            shopId = shopId.value.toString(),
            treatmentId = treatment.id.value.toString(),
            yearMonth = "2025-06"
        )

        val result = useCase.execute(query)

        assertFalse(result.availableDates.isEmpty())
        result.availableDates.forEach { date ->
            val dayOfWeek = date.dayOfWeek.name.lowercase()
            val timeRange = schedule[dayOfWeek]
            assertNotNull(timeRange)
            assertNotEquals("closed", timeRange)
        }
    }

    @Test
    fun `should exclude past dates`() {
        val shopId = ShopId.new()
        val schedule = mapOf(
            "monday" to "10:00-18:00",
            "tuesday" to "10:00-18:00",
            "wednesday" to "10:00-18:00",
            "thursday" to "10:00-18:00",
            "friday" to "10:00-18:00",
            "saturday" to "10:00-18:00",
            "sunday" to "10:00-18:00"
        )
        val shop = createShop(shopId, schedule)
        val treatment = createTreatment(shopId, durationMinutes = 60)

        whenever(beautishopPort.findById(shopId)).thenReturn(shop)
        whenever(treatmentPort.findById(treatment.id)).thenReturn(treatment)
        whenever(reservationPort.findByShopIdAndDate(any(), any()))
            .thenReturn(emptyList())

        val query = GetAvailableDatesQuery(
            shopId = shopId.value.toString(),
            treatmentId = treatment.id.value.toString(),
            yearMonth = "2025-06"
        )

        val result = useCase.execute(query)

        val today = LocalDate.now(fixedClock)
        result.availableDates.forEach { date ->
            assertFalse(date.isBefore(today))
        }
    }

    @Test
    fun `should exclude closed days`() {
        val shopId = ShopId.new()
        val schedule = mapOf(
            "monday" to "10:00-18:00",
            "sunday" to "closed"
        )
        val shop = createShop(shopId, schedule)
        val treatment = createTreatment(shopId, durationMinutes = 60)

        whenever(beautishopPort.findById(shopId)).thenReturn(shop)
        whenever(treatmentPort.findById(treatment.id)).thenReturn(treatment)
        whenever(reservationPort.findByShopIdAndDate(any(), any()))
            .thenReturn(emptyList())

        val query = GetAvailableDatesQuery(
            shopId = shopId.value.toString(),
            treatmentId = treatment.id.value.toString(),
            yearMonth = "2025-06"
        )

        val result = useCase.execute(query)

        result.availableDates.forEach { date ->
            assertNotEquals(DayOfWeek.SUNDAY, date.dayOfWeek)
        }
    }

    @Test
    fun `should exclude fully booked dates`() {
        val shopId = ShopId.new()
        val schedule = mapOf("monday" to "10:00-11:30")
        val shop = createShop(shopId, schedule)
        val treatment = createTreatment(shopId, durationMinutes = 60)

        whenever(beautishopPort.findById(shopId)).thenReturn(shop)
        whenever(treatmentPort.findById(treatment.id)).thenReturn(treatment)

        val bookedDate = LocalDate.of(2025, 6, 2)
        val fullBookings = listOf(
            Reservation.create(
                shopId = shopId,
                memberId = MemberId.new(),
                treatmentId = treatment.id,
                reservationDate = bookedDate,
                startTime = LocalTime.of(10, 0),
                endTime = LocalTime.of(11, 0),
                memo = null,
                clock = fixedClock
            ),
            Reservation.create(
                shopId = shopId,
                memberId = MemberId.new(),
                treatmentId = treatment.id,
                reservationDate = bookedDate,
                startTime = LocalTime.of(10, 30),
                endTime = LocalTime.of(11, 30),
                memo = null,
                clock = fixedClock
            )
        )

        whenever(reservationPort.findByShopIdAndDate(any(), any()))
            .thenAnswer { invocation ->
                val date = invocation.getArgument<LocalDate>(1)
                if (date == bookedDate) fullBookings else emptyList()
            }

        val query = GetAvailableDatesQuery(
            shopId = shopId.value.toString(),
            treatmentId = treatment.id.value.toString(),
            yearMonth = "2025-06"
        )

        val result = useCase.execute(query)

        assertFalse(result.availableDates.contains(bookedDate))
    }
}
