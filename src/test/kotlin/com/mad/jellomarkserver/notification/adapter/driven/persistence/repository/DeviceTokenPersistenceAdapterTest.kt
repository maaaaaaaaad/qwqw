package com.mad.jellomarkserver.notification.adapter.driven.persistence.repository

import com.mad.jellomarkserver.notification.adapter.driven.persistence.entity.DeviceTokenJpaEntity
import com.mad.jellomarkserver.notification.adapter.driven.persistence.mapper.DeviceTokenMapper
import com.mad.jellomarkserver.notification.core.domain.model.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.Instant
import java.util.*

@ExtendWith(MockitoExtension::class)
class DeviceTokenPersistenceAdapterTest {

    @Mock
    private lateinit var repository: DeviceTokenJpaRepository

    @Mock
    private lateinit var mapper: DeviceTokenMapper

    private lateinit var adapter: DeviceTokenPersistenceAdapter

    @BeforeEach
    fun setup() {
        adapter = DeviceTokenPersistenceAdapter(repository, mapper)
    }

    @Test
    fun `should save device token`() {
        val domain = createDomain()
        val entity = createEntity()

        whenever(mapper.toEntity(domain)).thenReturn(entity)
        whenever(repository.save(entity)).thenReturn(entity)
        whenever(mapper.toDomain(entity)).thenReturn(domain)

        val result = adapter.save(domain)

        assertEquals(domain, result)
        verify(repository).save(entity)
    }

    @Test
    fun `should delete by token`() {
        adapter.deleteByToken("fcm-token")

        verify(repository).deleteByToken("fcm-token")
    }

    @Test
    fun `should find by userId and userRole`() {
        val userId = UUID.randomUUID()
        val entity = createEntity(userId = userId)
        val domain = createDomain(userId = userId)

        whenever(repository.findByUserIdAndUserRole(userId, "MEMBER")).thenReturn(listOf(entity))
        whenever(mapper.toDomain(entity)).thenReturn(domain)

        val result = adapter.findByUserIdAndUserRole(userId, UserRole.MEMBER)

        assertEquals(1, result.size)
        assertEquals(domain, result[0])
    }

    @Test
    fun `should find by token`() {
        val entity = createEntity()
        val domain = createDomain()

        whenever(repository.findByToken("fcm-token")).thenReturn(entity)
        whenever(mapper.toDomain(entity)).thenReturn(domain)

        val result = adapter.findByToken("fcm-token")

        assertNotNull(result)
        assertEquals(domain, result)
    }

    @Test
    fun `should return null when token not found`() {
        whenever(repository.findByToken("nonexistent")).thenReturn(null)

        val result = adapter.findByToken("nonexistent")

        assertNull(result)
    }

    private fun createDomain(userId: UUID = UUID.randomUUID()): DeviceToken {
        return DeviceToken.reconstruct(
            id = DeviceTokenId.new(),
            userId = userId,
            userRole = UserRole.MEMBER,
            token = "fcm-token",
            platform = DevicePlatform.IOS,
            createdAt = Instant.now()
        )
    }

    private fun createEntity(userId: UUID = UUID.randomUUID()): DeviceTokenJpaEntity {
        return DeviceTokenJpaEntity(
            id = UUID.randomUUID(),
            userId = userId,
            userRole = "MEMBER",
            token = "fcm-token",
            platform = "IOS",
            createdAt = Instant.now()
        )
    }
}
