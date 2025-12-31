package com.mad.jellomarkserver.beautishop.adapter.driven.persistence.repository

import com.mad.jellomarkserver.beautishop.adapter.driven.persistence.entity.BeautishopJpaEntity
import com.mad.jellomarkserver.beautishop.adapter.driven.persistence.mapper.BeautishopMapper
import com.mad.jellomarkserver.beautishop.core.domain.exception.DuplicateShopRegNumException
import com.mad.jellomarkserver.beautishop.core.domain.model.*
import com.mad.jellomarkserver.common.persistence.ConstraintViolationTranslator
import com.mad.jellomarkserver.common.persistence.ConstraintViolationTranslatorImpl
import com.mad.jellomarkserver.owner.core.domain.model.OwnerId
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.dao.DataIntegrityViolationException
import java.time.Instant
import java.util.*

@ExtendWith(MockitoExtension::class)
class BeautishopPersistenceAdapterTest {

    @Mock
    private lateinit var jpaRepository: BeautishopJpaRepository

    @Mock
    private lateinit var mapper: BeautishopMapper

    private val constraintTranslator: ConstraintViolationTranslator = ConstraintViolationTranslatorImpl()

    private lateinit var adapter: BeautishopPersistenceAdapter

    @BeforeEach
    fun setup() {
        adapter = BeautishopPersistenceAdapter(jpaRepository, mapper, constraintTranslator)
    }

    @Test
    fun `should save beautishop successfully`() {
        val beautishop = createBeautishop()
        val ownerId = OwnerId.new()
        val entity = createEntity()

        `when`(mapper.toEntity(beautishop, ownerId)).thenReturn(entity)
        `when`(jpaRepository.saveAndFlush(entity)).thenReturn(entity)
        `when`(mapper.toDomain(entity)).thenReturn(beautishop)

        val result = adapter.save(beautishop, ownerId)

        assertEquals(beautishop, result)
        verify(mapper).toEntity(beautishop, ownerId)
        verify(jpaRepository).saveAndFlush(entity)
        verify(mapper).toDomain(entity)
    }

    @Test
    fun `should throw DuplicateShopRegNumException when shopRegNum constraint is violated`() {
        val beautishop = createBeautishop()
        val ownerId = OwnerId.new()
        val entity = createEntity()

        val exception = DataIntegrityViolationException("uk_beautishops_shop_reg_num")

        `when`(mapper.toEntity(beautishop, ownerId)).thenReturn(entity)
        `when`(jpaRepository.saveAndFlush(entity)).thenThrow(exception)

        val thrownException = assertThrows(DuplicateShopRegNumException::class.java) {
            adapter.save(beautishop, ownerId)
        }

        assertTrue(thrownException.message!!.contains("123-45-67890"))
        verify(mapper).toEntity(beautishop, ownerId)
        verify(jpaRepository).saveAndFlush(entity)
    }

    @Test
    fun `should find beautishop by id`() {
        val shopId = ShopId.new()
        val beautishop = createBeautishop()
        val entity = createEntity()

        `when`(jpaRepository.findById(shopId.value)).thenReturn(Optional.of(entity))
        `when`(mapper.toDomain(entity)).thenReturn(beautishop)

        val result = adapter.findById(shopId)

        assertEquals(beautishop, result)
        verify(jpaRepository).findById(shopId.value)
        verify(mapper).toDomain(entity)
    }

    @Test
    fun `should return null when beautishop not found by id`() {
        val shopId = ShopId.new()

        `when`(jpaRepository.findById(shopId.value)).thenReturn(Optional.empty())

        val result = adapter.findById(shopId)

        assertNull(result)
        verify(jpaRepository).findById(shopId.value)
    }

    @Test
    fun `should find beautishops by ownerId`() {
        val ownerId = OwnerId.new()
        val entity1 = createEntity()
        val entity2 = createEntity()
        val beautishop1 = createBeautishop()
        val beautishop2 = createBeautishop()

        `when`(jpaRepository.findByOwnerId(ownerId.value)).thenReturn(listOf(entity1, entity2))
        `when`(mapper.toDomain(entity1)).thenReturn(beautishop1)
        `when`(mapper.toDomain(entity2)).thenReturn(beautishop2)

        val result = adapter.findByOwnerId(ownerId)

        assertEquals(2, result.size)
        assertEquals(beautishop1, result[0])
        assertEquals(beautishop2, result[1])
        verify(jpaRepository).findByOwnerId(ownerId.value)
    }

    @Test
    fun `should return empty list when no beautishops found for ownerId`() {
        val ownerId = OwnerId.new()

        `when`(jpaRepository.findByOwnerId(ownerId.value)).thenReturn(emptyList())

        val result = adapter.findByOwnerId(ownerId)

        assertTrue(result.isEmpty())
        verify(jpaRepository).findByOwnerId(ownerId.value)
    }

    @Test
    fun `should find beautishop by shopRegNum`() {
        val regNum = ShopRegNum.of("123-45-67890")
        val beautishop = createBeautishop()
        val entity = createEntity()

        `when`(jpaRepository.findByShopRegNum("123-45-67890")).thenReturn(entity)
        `when`(mapper.toDomain(entity)).thenReturn(beautishop)

        val result = adapter.findByShopRegNum(regNum)

        assertEquals(beautishop, result)
        verify(jpaRepository).findByShopRegNum("123-45-67890")
        verify(mapper).toDomain(entity)
    }

    @Test
    fun `should return null when beautishop not found by shopRegNum`() {
        val regNum = ShopRegNum.of("123-45-67890")

        `when`(jpaRepository.findByShopRegNum("123-45-67890")).thenReturn(null)

        val result = adapter.findByShopRegNum(regNum)

        assertNull(result)
        verify(jpaRepository).findByShopRegNum("123-45-67890")
    }

    @Test
    fun `should delete beautishop by id`() {
        val shopId = ShopId.new()

        adapter.delete(shopId)

        verify(jpaRepository).deleteById(shopId.value)
    }

    private fun createBeautishop(): Beautishop {
        return Beautishop.create(
            name = ShopName.of("Beautiful Salon"),
            regNum = ShopRegNum.of("123-45-67890"),
            phoneNumber = ShopPhoneNumber.of("010-1234-5678"),
            address = ShopAddress.of("서울특별시 강남구 테헤란로 123"),
            gps = ShopGPS.of(37.5665, 126.9780),
            operatingTime = OperatingTime.of(mapOf("monday" to "09:00-18:00")),
            description = ShopDescription.of("아름다운 네일샵입니다"),
            image = ShopImage.of("https://example.com/image.jpg")
        )
    }

    private fun createEntity(): BeautishopJpaEntity {
        return BeautishopJpaEntity(
            id = UUID.randomUUID(),
            ownerId = UUID.randomUUID(),
            name = "Beautiful Salon",
            shopRegNum = "123-45-67890",
            phoneNumber = "010-1234-5678",
            address = "서울특별시 강남구 테헤란로 123",
            latitude = 37.5665,
            longitude = 126.9780,
            operatingTime = "monday:09:00-18:00",
            description = "아름다운 네일샵입니다",
            image = "https://example.com/image.jpg",
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
    }
}
