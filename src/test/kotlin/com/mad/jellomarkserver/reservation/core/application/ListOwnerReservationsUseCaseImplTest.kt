package com.mad.jellomarkserver.reservation.core.application

import com.mad.jellomarkserver.beautishop.core.domain.model.*
import com.mad.jellomarkserver.beautishop.port.driven.BeautishopPort
import com.mad.jellomarkserver.member.core.domain.model.MemberId
import com.mad.jellomarkserver.owner.core.domain.exception.OwnerNotFoundException
import com.mad.jellomarkserver.owner.core.domain.model.*
import com.mad.jellomarkserver.owner.port.driven.OwnerPort
import com.mad.jellomarkserver.reservation.core.domain.model.Reservation
import com.mad.jellomarkserver.reservation.port.driven.ReservationPort
import com.mad.jellomarkserver.reservation.port.driving.ListOwnerReservationsCommand
import com.mad.jellomarkserver.treatment.core.domain.model.TreatmentId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime

@ExtendWith(MockitoExtension::class)
class ListOwnerReservationsUseCaseImplTest {

    @Mock
    private lateinit var reservationPort: ReservationPort

    @Mock
    private lateinit var beautishopPort: BeautishopPort

    @Mock
    private lateinit var ownerPort: OwnerPort

    private lateinit var useCase: ListOwnerReservationsUseCaseImpl

    @BeforeEach
    fun setup() {
        useCase = ListOwnerReservationsUseCaseImpl(reservationPort, beautishopPort, ownerPort)
    }

    @Test
    fun `should return reservations across all owner shops`() {
        val ownerId = OwnerId.new()
        val ownerEmail = OwnerEmail.of("owner@test.com")
        val owner = createTestOwner(ownerId, ownerEmail)
        val shopId1 = ShopId.new()
        val shopId2 = ShopId.new()
        val reservations = listOf(
            createTestReservation(shopId = shopId1),
            createTestReservation(shopId = shopId1),
            createTestReservation(shopId = shopId2)
        )

        whenever(ownerPort.findByEmail(ownerEmail)).thenReturn(owner)
        whenever(beautishopPort.findByOwnerId(ownerId)).thenReturn(
            listOf(createTestShop(shopId1), createTestShop(shopId2))
        )
        whenever(reservationPort.findByShopIds(listOf(shopId1, shopId2))).thenReturn(reservations)

        val command = ListOwnerReservationsCommand(ownerEmail.value)
        val result = useCase.execute(command)

        assertEquals(3, result.size)
    }

    @Test
    fun `should return empty list when owner has no shops`() {
        val ownerId = OwnerId.new()
        val ownerEmail = OwnerEmail.of("owner@test.com")
        val owner = createTestOwner(ownerId, ownerEmail)

        whenever(ownerPort.findByEmail(ownerEmail)).thenReturn(owner)
        whenever(beautishopPort.findByOwnerId(ownerId)).thenReturn(emptyList())

        val command = ListOwnerReservationsCommand(ownerEmail.value)
        val result = useCase.execute(command)

        assertTrue(result.isEmpty())
    }

    @Test
    fun `should return empty list when shops have no reservations`() {
        val ownerId = OwnerId.new()
        val ownerEmail = OwnerEmail.of("owner@test.com")
        val owner = createTestOwner(ownerId, ownerEmail)
        val shopId = ShopId.new()

        whenever(ownerPort.findByEmail(ownerEmail)).thenReturn(owner)
        whenever(beautishopPort.findByOwnerId(ownerId)).thenReturn(listOf(createTestShop(shopId)))
        whenever(reservationPort.findByShopIds(listOf(shopId))).thenReturn(emptyList())

        val command = ListOwnerReservationsCommand(ownerEmail.value)
        val result = useCase.execute(command)

        assertTrue(result.isEmpty())
    }

    @Test
    fun `should throw when owner not found`() {
        val ownerEmail = OwnerEmail.of("unknown@test.com")

        whenever(ownerPort.findByEmail(ownerEmail)).thenReturn(null)

        val command = ListOwnerReservationsCommand(ownerEmail.value)

        assertThrows<OwnerNotFoundException> {
            useCase.execute(command)
        }
    }

    private fun createTestReservation(shopId: ShopId = ShopId.new()): Reservation {
        return Reservation.create(
            shopId = shopId,
            memberId = MemberId.new(),
            treatmentId = TreatmentId.new(),
            reservationDate = LocalDate.of(2025, 3, 15),
            startTime = LocalTime.of(14, 0),
            endTime = LocalTime.of(15, 0),
            memo = null
        )
    }

    private fun createTestOwner(ownerId: OwnerId, ownerEmail: OwnerEmail): Owner {
        return Owner.reconstruct(
            id = ownerId,
            businessNumber = BusinessNumber.of("123-45-67890"),
            ownerPhoneNumber = OwnerPhoneNumber.of("010-1234-5678"),
            ownerNickname = OwnerNickname.of("테스트사장"),
            ownerEmail = ownerEmail,
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
    }

    private fun createTestShop(shopId: ShopId): Beautishop {
        return Beautishop.reconstruct(
            id = shopId,
            name = ShopName.of("Test Shop"),
            regNum = ShopRegNum.of("123-45-67890"),
            phoneNumber = ShopPhoneNumber.of("010-1234-5678"),
            address = ShopAddress.of("서울특별시 강남구"),
            gps = ShopGPS.of(37.5, 127.0),
            operatingTime = OperatingTime.of(mapOf("monday" to "09:00-18:00")),
            description = null,
            images = ShopImages.empty(),
            averageRating = AverageRating.zero(),
            reviewCount = ReviewCount.zero(),
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
    }
}
