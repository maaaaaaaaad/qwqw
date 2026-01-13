package com.mad.jellomarkserver.owner.core.application

import com.mad.jellomarkserver.owner.core.domain.exception.OwnerNotFoundException
import com.mad.jellomarkserver.owner.core.domain.model.*
import com.mad.jellomarkserver.owner.port.driven.OwnerPort
import com.mad.jellomarkserver.owner.port.driving.GetCurrentOwnerCommand
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever

@ExtendWith(MockitoExtension::class)
class GetCurrentOwnerUseCaseImplTest {

    @Mock
    private lateinit var ownerPort: OwnerPort

    private lateinit var useCase: GetCurrentOwnerUseCaseImpl

    @BeforeEach
    fun setup() {
        useCase = GetCurrentOwnerUseCaseImpl(ownerPort)
    }

    @Test
    fun `should return owner when found by email`() {
        val owner = Owner.create(
            businessNumber = BusinessNumber.of("123456789"),
            ownerPhoneNumber = OwnerPhoneNumber.of("010-1234-5678"),
            ownerNickname = OwnerNickname.of("test"),
            ownerEmail = OwnerEmail.of("test@example.com")
        )
        whenever(ownerPort.findByEmail(any())).thenReturn(owner)

        val command = GetCurrentOwnerCommand(email = "test@example.com")
        val result = useCase.execute(command)

        assertEquals(owner.id, result.id)
        assertEquals("test@example.com", result.ownerEmail.value)
    }

    @Test
    fun `should throw OwnerNotFoundException when owner not found`() {
        whenever(ownerPort.findByEmail(any())).thenReturn(null)

        val command = GetCurrentOwnerCommand(email = "nonexistent@example.com")

        val exception = assertThrows(OwnerNotFoundException::class.java) {
            useCase.execute(command)
        }
        assertTrue(exception.message!!.contains("nonexistent@example.com"))
    }
}
