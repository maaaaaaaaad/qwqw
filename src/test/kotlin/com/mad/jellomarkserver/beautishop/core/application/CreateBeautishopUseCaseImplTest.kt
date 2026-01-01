package com.mad.jellomarkserver.beautishop.core.application

import com.mad.jellomarkserver.beautishop.core.domain.exception.*
import com.mad.jellomarkserver.beautishop.core.domain.model.*
import com.mad.jellomarkserver.beautishop.port.driven.BeautishopPort
import com.mad.jellomarkserver.beautishop.port.driving.CreateBeautishopCommand
import com.mad.jellomarkserver.beautishop.port.driving.CreateBeautishopUseCase
import com.mad.jellomarkserver.owner.core.domain.model.OwnerId
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import kotlin.test.assertFailsWith

@ExtendWith(MockitoExtension::class)
class CreateBeautishopUseCaseImplTest {

    @Mock
    private lateinit var beautishopPort: BeautishopPort

    private lateinit var useCase: CreateBeautishopUseCase

    @BeforeEach
    fun setup() {
        useCase = CreateBeautishopUseCaseImpl(beautishopPort)
    }

    @Test
    fun `should create beautishop successfully with all fields`() {
        val ownerId = OwnerId.new()
        val command = CreateBeautishopCommand(
            ownerId = ownerId.value.toString(),
            shopName = "Beautiful Salon",
            shopRegNum = "123-45-67890",
            shopPhoneNumber = "010-1234-5678",
            shopAddress = "서울특별시 강남구 테헤란로 123",
            latitude = 37.5665,
            longitude = 126.9780,
            operatingTime = mapOf("monday" to "09:00-18:00"),
            shopDescription = "아름다운 네일샵입니다",
            shopImage = "https://example.com/image.jpg"
        )

        `when`(
            beautishopPort.save(
                ArgumentMatchers.any() ?: Beautishop.create(
                    name = ShopName.of("Beautiful Salon"),
                    regNum = ShopRegNum.of("123-45-67890"),
                    phoneNumber = ShopPhoneNumber.of("010-1234-5678"),
                    address = ShopAddress.of("서울특별시 강남구 테헤란로 123"),
                    gps = ShopGPS.of(37.5665, 126.9780),
                    operatingTime = OperatingTime.of(mapOf("monday" to "09:00-18:00")),
                    description = ShopDescription.of("아름다운 네일샵입니다"),
                    image = ShopImage.of("https://example.com/image.jpg")
                ),
                ArgumentMatchers.any() ?: ownerId
            )
        ).thenAnswer { invocation ->
            invocation.arguments[0] as Beautishop
        }

        val result = useCase.create(command)

        assertNotNull(result)
        assertNotNull(result.id)
        assertEquals("Beautiful Salon", result.name.value)
        assertEquals("123-45-67890", result.regNum.value)
        assertEquals("010-1234-5678", result.phoneNumber.value)
        assertEquals("서울특별시 강남구 테헤란로 123", result.address.value)
        assertEquals(37.5665, result.gps.latitude)
        assertEquals(126.9780, result.gps.longitude)
        assertEquals(mapOf("monday" to "09:00-18:00"), result.operatingTime.schedule)
        assertEquals("아름다운 네일샵입니다", result.description?.value)
        assertEquals("https://example.com/image.jpg", result.image?.value)
        assertNotNull(result.createdAt)
        assertNotNull(result.updatedAt)
    }

    @Test
    fun `should create beautishop successfully without optional fields`() {
        val ownerId = OwnerId.new()
        val command = CreateBeautishopCommand(
            ownerId = ownerId.value.toString(),
            shopName = "Beautiful Salon",
            shopRegNum = "123-45-67890",
            shopPhoneNumber = "010-1234-5678",
            shopAddress = "서울특별시 강남구 테헤란로 123",
            latitude = 37.5665,
            longitude = 126.9780,
            operatingTime = mapOf("monday" to "09:00-18:00"),
            shopDescription = null,
            shopImage = null
        )

        `when`(
            beautishopPort.save(
                ArgumentMatchers.any() ?: Beautishop.create(
                    name = ShopName.of("Beautiful Salon"),
                    regNum = ShopRegNum.of("123-45-67890"),
                    phoneNumber = ShopPhoneNumber.of("010-1234-5678"),
                    address = ShopAddress.of("서울특별시 강남구 테헤란로 123"),
                    gps = ShopGPS.of(37.5665, 126.9780),
                    operatingTime = OperatingTime.of(mapOf("monday" to "09:00-18:00")),
                    description = null,
                    image = null
                ),
                ArgumentMatchers.any() ?: ownerId
            )
        ).thenAnswer { invocation ->
            invocation.arguments[0] as Beautishop
        }

        val result = useCase.create(command)

        assertNotNull(result)
        assertNull(result.description)
        assertNull(result.image)
    }

    @Test
    fun `should throw InvalidShopNameException when shop name is blank`() {
        val command = CreateBeautishopCommand(
            ownerId = OwnerId.new().value.toString(),
            shopName = "   ",
            shopRegNum = "123-45-67890",
            shopPhoneNumber = "010-1234-5678",
            shopAddress = "서울특별시 강남구 테헤란로 123",
            latitude = 37.5665,
            longitude = 126.9780,
            operatingTime = mapOf("monday" to "09:00-18:00"),
            shopDescription = null,
            shopImage = null
        )

        assertFailsWith<InvalidShopNameException> {
            useCase.create(command)
        }
    }

    @Test
    fun `should throw InvalidShopRegNumException when shop reg num is invalid`() {
        val command = CreateBeautishopCommand(
            ownerId = OwnerId.new().value.toString(),
            shopName = "Beautiful Salon",
            shopRegNum = "12345",
            shopPhoneNumber = "010-1234-5678",
            shopAddress = "서울특별시 강남구 테헤란로 123",
            latitude = 37.5665,
            longitude = 126.9780,
            operatingTime = mapOf("monday" to "09:00-18:00"),
            shopDescription = null,
            shopImage = null
        )

        assertFailsWith<InvalidShopRegNumException> {
            useCase.create(command)
        }
    }

    @Test
    fun `should throw InvalidShopPhoneNumberException when phone number is invalid`() {
        val command = CreateBeautishopCommand(
            ownerId = OwnerId.new().value.toString(),
            shopName = "Beautiful Salon",
            shopRegNum = "123-45-67890",
            shopPhoneNumber = "invalid",
            shopAddress = "서울특별시 강남구 테헤란로 123",
            latitude = 37.5665,
            longitude = 126.9780,
            operatingTime = mapOf("monday" to "09:00-18:00"),
            shopDescription = null,
            shopImage = null
        )

        assertFailsWith<InvalidShopPhoneNumberException> {
            useCase.create(command)
        }
    }

    @Test
    fun `should throw InvalidShopAddressException when address is too short`() {
        val command = CreateBeautishopCommand(
            ownerId = OwnerId.new().value.toString(),
            shopName = "Beautiful Salon",
            shopRegNum = "123-45-67890",
            shopPhoneNumber = "010-1234-5678",
            shopAddress = "서울",
            latitude = 37.5665,
            longitude = 126.9780,
            operatingTime = mapOf("monday" to "09:00-18:00"),
            shopDescription = null,
            shopImage = null
        )

        assertFailsWith<InvalidShopAddressException> {
            useCase.create(command)
        }
    }

    @Test
    fun `should throw InvalidShopGPSException when latitude is out of range`() {
        val command = CreateBeautishopCommand(
            ownerId = OwnerId.new().value.toString(),
            shopName = "Beautiful Salon",
            shopRegNum = "123-45-67890",
            shopPhoneNumber = "010-1234-5678",
            shopAddress = "서울특별시 강남구 테헤란로 123",
            latitude = 91.0,
            longitude = 126.9780,
            operatingTime = mapOf("monday" to "09:00-18:00"),
            shopDescription = null,
            shopImage = null
        )

        assertFailsWith<InvalidShopGPSException> {
            useCase.create(command)
        }
    }

    @Test
    fun `should throw InvalidOperatingTimeException when operating time is empty`() {
        val command = CreateBeautishopCommand(
            ownerId = OwnerId.new().value.toString(),
            shopName = "Beautiful Salon",
            shopRegNum = "123-45-67890",
            shopPhoneNumber = "010-1234-5678",
            shopAddress = "서울특별시 강남구 테헤란로 123",
            latitude = 37.5665,
            longitude = 126.9780,
            operatingTime = emptyMap(),
            shopDescription = null,
            shopImage = null
        )

        assertFailsWith<InvalidOperatingTimeException> {
            useCase.create(command)
        }
    }

    @Test
    fun `should throw DuplicateShopRegNumException when shop reg num already exists`() {
        val ownerId = OwnerId.new()
        val command = CreateBeautishopCommand(
            ownerId = ownerId.value.toString(),
            shopName = "Beautiful Salon",
            shopRegNum = "123-45-67890",
            shopPhoneNumber = "010-1234-5678",
            shopAddress = "서울특별시 강남구 테헤란로 123",
            latitude = 37.5665,
            longitude = 126.9780,
            operatingTime = mapOf("monday" to "09:00-18:00"),
            shopDescription = null,
            shopImage = null
        )

        `when`(
            beautishopPort.save(
                ArgumentMatchers.any() ?: Beautishop.create(
                    name = ShopName.of("Beautiful Salon"),
                    regNum = ShopRegNum.of("123-45-67890"),
                    phoneNumber = ShopPhoneNumber.of("010-1234-5678"),
                    address = ShopAddress.of("서울특별시 강남구 테헤란로 123"),
                    gps = ShopGPS.of(37.5665, 126.9780),
                    operatingTime = OperatingTime.of(mapOf("monday" to "09:00-18:00")),
                    description = null,
                    image = null
                ),
                ArgumentMatchers.any() ?: ownerId
            )
        ).thenThrow(DuplicateShopRegNumException("123-45-67890"))

        val exception = assertFailsWith<DuplicateShopRegNumException> {
            useCase.create(command)
        }

        assertTrue(exception.message!!.contains("123-45-67890"))
    }

    @Test
    fun `should trim whitespace from input values`() {
        val ownerId = OwnerId.new()
        val command = CreateBeautishopCommand(
            ownerId = ownerId.value.toString(),
            shopName = "  Beautiful Salon  ",
            shopRegNum = "  123-45-67890  ",
            shopPhoneNumber = "  010-1234-5678  ",
            shopAddress = "  서울특별시 강남구 테헤란로 123  ",
            latitude = 37.5665,
            longitude = 126.9780,
            operatingTime = mapOf("monday" to "09:00-18:00"),
            shopDescription = "  아름다운 네일샵입니다  ",
            shopImage = "  https://example.com/image.jpg  "
        )

        `when`(
            beautishopPort.save(
                ArgumentMatchers.any() ?: Beautishop.create(
                    name = ShopName.of("Beautiful Salon"),
                    regNum = ShopRegNum.of("123-45-67890"),
                    phoneNumber = ShopPhoneNumber.of("010-1234-5678"),
                    address = ShopAddress.of("서울특별시 강남구 테헤란로 123"),
                    gps = ShopGPS.of(37.5665, 126.9780),
                    operatingTime = OperatingTime.of(mapOf("monday" to "09:00-18:00")),
                    description = ShopDescription.of("아름다운 네일샵입니다"),
                    image = ShopImage.of("https://example.com/image.jpg")
                ),
                ArgumentMatchers.any() ?: ownerId
            )
        ).thenAnswer { invocation ->
            invocation.arguments[0] as Beautishop
        }

        val result = useCase.create(command)

        assertEquals("Beautiful Salon", result.name.value)
        assertEquals("123-45-67890", result.regNum.value)
        assertEquals("010-1234-5678", result.phoneNumber.value)
        assertEquals("서울특별시 강남구 테헤란로 123", result.address.value)
        assertEquals("아름다운 네일샵입니다", result.description?.value)
        assertEquals("https://example.com/image.jpg", result.image?.value)
    }
}
