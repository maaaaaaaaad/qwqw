package com.mad.jellomarkserver.owner.adapter.driving.web

import com.mad.jellomarkserver.owner.adapter.driving.web.request.OwnerSignUpRequest
import com.mad.jellomarkserver.owner.core.domain.exception.DuplicateOwnerBusinessNumberException
import com.mad.jellomarkserver.owner.core.domain.exception.DuplicateOwnerPhoneNumberException
import com.mad.jellomarkserver.owner.core.domain.exception.InvalidOwnerBusinessNumberException
import com.mad.jellomarkserver.owner.core.domain.exception.InvalidOwnerPhoneNumberException
import com.mad.jellomarkserver.owner.core.domain.model.BusinessNumber
import com.mad.jellomarkserver.owner.core.domain.model.Owner
import com.mad.jellomarkserver.owner.core.domain.model.OwnerId
import com.mad.jellomarkserver.owner.core.domain.model.OwnerNickname
import com.mad.jellomarkserver.owner.core.domain.model.OwnerPhoneNumber
import com.mad.jellomarkserver.owner.port.driving.SignUpOwnerUseCase
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.Instant
import java.util.*
import kotlin.test.assertFailsWith

class OwnerSignUpControllerTest {

    @Test
    fun `should return owner response when owner signs up successfully`() {
        val ownerId = OwnerId.from(UUID.randomUUID())
        val createdAt = Instant.parse("2025-01-01T00:00:00Z")
        val owner = Owner.reconstruct(
            id = ownerId,
            businessNumber = BusinessNumber.of("123456789"),
            ownerPhoneNumber = OwnerPhoneNumber.of("010-1234-5678"),
            ownerNickname = OwnerNickname.of("shop"),
            createdAt = createdAt,
            updatedAt = createdAt
        )

        val useCase = SignUpOwnerUseCase { owner }
        val controller = OwnerSignUpController(useCase)

        val request = OwnerSignUpRequest(
            businessNumber = "123456789",
            phoneNumber = "010-1234-5678",
            nickname = "shop"
        )

        val response = controller.signUp(request)

        assertEquals(ownerId.value, response.id)
        assertEquals("123456789", response.businessNumber)
        assertEquals("010-1234-5678", response.phoneNumber)
        assertEquals(createdAt, response.createdAt)
        assertEquals(createdAt, response.updatedAt)
    }

    @Test
    fun `should throw InvalidOwnerBusinessNumberException when business number is invalid`() {
        val useCase = SignUpOwnerUseCase { throw InvalidOwnerBusinessNumberException("12345678") }
        val controller = OwnerSignUpController(useCase)

        val request = OwnerSignUpRequest(
            businessNumber = "12345678",
            phoneNumber = "010-1234-5678",
            nickname = "shop"
        )

        assertFailsWith<InvalidOwnerBusinessNumberException> {
            controller.signUp(request)
        }
    }

    @Test
    fun `should throw InvalidOwnerPhoneNumberException when phone number is invalid`() {
        val useCase = SignUpOwnerUseCase { throw InvalidOwnerPhoneNumberException("invalid-phone") }
        val controller = OwnerSignUpController(useCase)

        val request = OwnerSignUpRequest(
            businessNumber = "123456789",
            phoneNumber = "invalid-phone",
            nickname = "shop"
        )

        assertFailsWith<InvalidOwnerPhoneNumberException> {
            controller.signUp(request)
        }
    }

    @Test
    fun `should throw DuplicateOwnerBusinessNumberException when business number already exists`() {
        val useCase = SignUpOwnerUseCase { throw DuplicateOwnerBusinessNumberException("123456789") }
        val controller = OwnerSignUpController(useCase)

        val request = OwnerSignUpRequest(
            businessNumber = "123456789",
            phoneNumber = "010-1234-5678",
            nickname = "shop"
        )

        assertFailsWith<DuplicateOwnerBusinessNumberException> {
            controller.signUp(request)
        }
    }

    @Test
    fun `should throw DuplicateOwnerPhoneNumberException when phone number already exists`() {
        val useCase = SignUpOwnerUseCase { throw DuplicateOwnerPhoneNumberException("010-1234-5678") }
        val controller = OwnerSignUpController(useCase)

        val request = OwnerSignUpRequest(
            businessNumber = "123456789",
            phoneNumber = "010-1234-5678",
            nickname = "shop"
        )

        assertFailsWith<DuplicateOwnerPhoneNumberException> {
            controller.signUp(request)
        }
    }

    @Test
    fun `should throw RuntimeException when unexpected error occurs`() {
        val useCase = SignUpOwnerUseCase { throw RuntimeException("Unexpected error") }
        val controller = OwnerSignUpController(useCase)

        val request = OwnerSignUpRequest(
            businessNumber = "123456789",
            phoneNumber = "010-1234-5678",
            nickname = "shop"
        )

        assertFailsWith<RuntimeException> {
            controller.signUp(request)
        }
    }
}
