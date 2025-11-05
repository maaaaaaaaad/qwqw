package com.mad.jellomarkserver.owner.core.application

import com.mad.jellomarkserver.owner.core.domain.exception.DuplicateBusinessNumberException
import com.mad.jellomarkserver.owner.core.domain.exception.DuplicatePhoneNumberException
import com.mad.jellomarkserver.owner.core.domain.exception.InvalidBusinessNumberException
import com.mad.jellomarkserver.owner.core.domain.exception.InvalidPhoneNumberException
import com.mad.jellomarkserver.owner.core.domain.model.Owner
import com.mad.jellomarkserver.owner.port.driven.OwnerPort
import com.mad.jellomarkserver.owner.port.driving.SignUpOwnerCommand
import com.mad.jellomarkserver.owner.port.driving.SignUpOwnerUseCase
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import kotlin.test.assertFailsWith

@ExtendWith(MockitoExtension::class)
class SignUpOwnerUseCaseImplTest {

    @Mock
    private lateinit var ownerPort: OwnerPort

    private lateinit var useCase: SignUpOwnerUseCase

    @BeforeEach
    fun setup() {
        useCase = SignUpOwnerUseCaseImpl(ownerPort)
    }

    @Test
    fun `should throw InvalidBusinessNumberException when business number is blank`() {
        val command = SignUpOwnerCommand(
            businessNumber = "   ",
            phoneNumber = "010-1234-5678"
        )

        assertFailsWith<InvalidBusinessNumberException> {
            useCase.signUp(command)
        }
    }

    @Test
    fun `should throw InvalidBusinessNumberException when business number is empty`() {
        val command = SignUpOwnerCommand(
            businessNumber = "",
            phoneNumber = "010-1234-5678"
        )

        assertFailsWith<InvalidBusinessNumberException> {
            useCase.signUp(command)
        }
    }

    @Test
    fun `should throw InvalidBusinessNumberException when business number is too short`() {
        val command = SignUpOwnerCommand(
            businessNumber = "12345678",
            phoneNumber = "010-1234-5678"
        )

        assertFailsWith<InvalidBusinessNumberException> {
            useCase.signUp(command)
        }
    }

    @Test
    fun `should throw InvalidBusinessNumberException when business number is too long`() {
        val command = SignUpOwnerCommand(
            businessNumber = "1234567890",
            phoneNumber = "010-1234-5678"
        )

        assertFailsWith<InvalidBusinessNumberException> {
            useCase.signUp(command)
        }
    }

    @Test
    fun `should throw InvalidPhoneNumberException when phone number is blank`() {
        val command = SignUpOwnerCommand(
            businessNumber = "123456789",
            phoneNumber = "   "
        )

        assertFailsWith<InvalidPhoneNumberException> {
            useCase.signUp(command)
        }
    }

    @Test
    fun `should throw InvalidPhoneNumberException when phone number is empty`() {
        val command = SignUpOwnerCommand(
            businessNumber = "123456789",
            phoneNumber = ""
        )

        assertFailsWith<InvalidPhoneNumberException> {
            useCase.signUp(command)
        }
    }

    @Test
    fun `should throw InvalidPhoneNumberException when phone number format is invalid`() {
        val command = SignUpOwnerCommand(
            businessNumber = "123456789",
            phoneNumber = "01012345678"
        )

        assertFailsWith<InvalidPhoneNumberException> {
            useCase.signUp(command)
        }
    }

    @Test
    fun `should throw InvalidPhoneNumberException when phone number has no hyphens`() {
        val command = SignUpOwnerCommand(
            businessNumber = "123456789",
            phoneNumber = "010 1234 5678"
        )

        assertFailsWith<InvalidPhoneNumberException> {
            useCase.signUp(command)
        }
    }

    @Test
    fun `should throw InvalidPhoneNumberException when phone number starts with invalid prefix`() {
        val command = SignUpOwnerCommand(
            businessNumber = "123456789",
            phoneNumber = "090-1234-5678"
        )

        assertFailsWith<InvalidPhoneNumberException> {
            useCase.signUp(command)
        }
    }

    @Test
    fun `should throw DuplicateBusinessNumberException when business number already exists`() {
        val command = SignUpOwnerCommand(
            businessNumber = "123456789",
            phoneNumber = "010-1234-5678"
        )

        `when`(
            ownerPort.save(
                org.mockito.ArgumentMatchers.any() ?: Owner.create(
                    com.mad.jellomarkserver.owner.core.domain.model.BusinessNumber.of("123456789"),
                    com.mad.jellomarkserver.owner.core.domain.model.PhoneNumber.of("010-1234-5678")
                )
            )
        ).thenThrow(DuplicateBusinessNumberException("123456789"))

        val exception = assertFailsWith<DuplicateBusinessNumberException> {
            useCase.signUp(command)
        }

        assertEquals("Duplicate business number: 123456789", exception.message)
    }

    @Test
    fun `should throw DuplicatePhoneNumberException when phone number already exists`() {
        val command = SignUpOwnerCommand(
            businessNumber = "123456789",
            phoneNumber = "010-1234-5678"
        )

        `when`(
            ownerPort.save(
                org.mockito.ArgumentMatchers.any() ?: Owner.create(
                    com.mad.jellomarkserver.owner.core.domain.model.BusinessNumber.of("123456789"),
                    com.mad.jellomarkserver.owner.core.domain.model.PhoneNumber.of("010-1234-5678")
                )
            )
        ).thenThrow(DuplicatePhoneNumberException("010-1234-5678"))

        val exception = assertFailsWith<DuplicatePhoneNumberException> {
            useCase.signUp(command)
        }

        assertEquals("Duplicate phone number: 010-1234-5678", exception.message)
    }

    @Test
    fun `should validate and trim whitespace from business number`() {
        val command = SignUpOwnerCommand(
            businessNumber = "  123456789  ",
            phoneNumber = "010-1234-5678"
        )

        `when`(
            ownerPort.save(
                org.mockito.ArgumentMatchers.any() ?: Owner.create(
                    com.mad.jellomarkserver.owner.core.domain.model.BusinessNumber.of("123456789"),
                    com.mad.jellomarkserver.owner.core.domain.model.PhoneNumber.of("010-1234-5678")
                )
            )
        ).thenAnswer { invocation ->
            val owner = invocation.arguments[0] as Owner
            assertEquals("123456789", owner.businessNumber.value)
            owner
        }

        val result = useCase.signUp(command)
        assertNotNull(result)
    }

    @Test
    fun `should validate and trim whitespace from phone number`() {
        val command = SignUpOwnerCommand(
            businessNumber = "123456789",
            phoneNumber = "  010-1234-5678  "
        )

        `when`(
            ownerPort.save(
                org.mockito.ArgumentMatchers.any() ?: Owner.create(
                    com.mad.jellomarkserver.owner.core.domain.model.BusinessNumber.of("123456789"),
                    com.mad.jellomarkserver.owner.core.domain.model.PhoneNumber.of("010-1234-5678")
                )
            )
        ).thenAnswer { invocation ->
            val owner = invocation.arguments[0] as Owner
            assertEquals("010-1234-5678", owner.phoneNumber.value)
            owner
        }

        val result = useCase.signUp(command)
        assertNotNull(result)
    }

    @Test
    fun `should accept alphanumeric business number`() {
        val command = SignUpOwnerCommand(
            businessNumber = "abc123xyz",
            phoneNumber = "010-1234-5678"
        )

        `when`(
            ownerPort.save(
                org.mockito.ArgumentMatchers.any() ?: Owner.create(
                    com.mad.jellomarkserver.owner.core.domain.model.BusinessNumber.of("abc123xyz"),
                    com.mad.jellomarkserver.owner.core.domain.model.PhoneNumber.of("010-1234-5678")
                )
            )
        ).thenAnswer { invocation ->
            val owner = invocation.arguments[0] as Owner
            assertEquals("abc123xyz", owner.businessNumber.value)
            owner
        }

        val result = useCase.signUp(command)
        assertNotNull(result)
    }

    @Test
    fun `should accept business number with special characters`() {
        val command = SignUpOwnerCommand(
            businessNumber = "12-34.567",
            phoneNumber = "010-1234-5678"
        )

        `when`(
            ownerPort.save(
                org.mockito.ArgumentMatchers.any() ?: Owner.create(
                    com.mad.jellomarkserver.owner.core.domain.model.BusinessNumber.of("12-34.567"),
                    com.mad.jellomarkserver.owner.core.domain.model.PhoneNumber.of("010-1234-5678")
                )
            )
        ).thenAnswer { invocation ->
            val owner = invocation.arguments[0] as Owner
            assertEquals("12-34.567", owner.businessNumber.value)
            owner
        }

        val result = useCase.signUp(command)
        assertNotNull(result)
    }

    @Test
    fun `should accept uppercase business number`() {
        val command = SignUpOwnerCommand(
            businessNumber = "ABCDEFGHI",
            phoneNumber = "010-1234-5678"
        )

        `when`(
            ownerPort.save(
                org.mockito.ArgumentMatchers.any() ?: Owner.create(
                    com.mad.jellomarkserver.owner.core.domain.model.BusinessNumber.of("ABCDEFGHI"),
                    com.mad.jellomarkserver.owner.core.domain.model.PhoneNumber.of("010-1234-5678")
                )
            )
        ).thenAnswer { invocation ->
            val owner = invocation.arguments[0] as Owner
            assertEquals("ABCDEFGHI", owner.businessNumber.value)
            owner
        }

        val result = useCase.signUp(command)
        assertNotNull(result)
    }

    @Test
    fun `should accept Seoul area phone number`() {
        val command = SignUpOwnerCommand(
            businessNumber = "123456789",
            phoneNumber = "02-1234-5678"
        )

        `when`(
            ownerPort.save(
                org.mockito.ArgumentMatchers.any() ?: Owner.create(
                    com.mad.jellomarkserver.owner.core.domain.model.BusinessNumber.of("123456789"),
                    com.mad.jellomarkserver.owner.core.domain.model.PhoneNumber.of("02-1234-5678")
                )
            )
        ).thenAnswer { invocation ->
            val owner = invocation.arguments[0] as Owner
            assertEquals("02-1234-5678", owner.phoneNumber.value)
            owner
        }

        val result = useCase.signUp(command)
        assertNotNull(result)
    }

    @Test
    fun `should accept regional phone number`() {
        val command = SignUpOwnerCommand(
            businessNumber = "123456789",
            phoneNumber = "031-123-4567"
        )

        `when`(
            ownerPort.save(
                org.mockito.ArgumentMatchers.any() ?: Owner.create(
                    com.mad.jellomarkserver.owner.core.domain.model.BusinessNumber.of("123456789"),
                    com.mad.jellomarkserver.owner.core.domain.model.PhoneNumber.of("031-123-4567")
                )
            )
        ).thenAnswer { invocation ->
            val owner = invocation.arguments[0] as Owner
            assertEquals("031-123-4567", owner.phoneNumber.value)
            owner
        }

        val result = useCase.signUp(command)
        assertNotNull(result)
    }

    @Test
    fun `should accept phone number starting with 011`() {
        val command = SignUpOwnerCommand(
            businessNumber = "123456789",
            phoneNumber = "011-123-4567"
        )

        `when`(
            ownerPort.save(
                org.mockito.ArgumentMatchers.any() ?: Owner.create(
                    com.mad.jellomarkserver.owner.core.domain.model.BusinessNumber.of("123456789"),
                    com.mad.jellomarkserver.owner.core.domain.model.PhoneNumber.of("011-123-4567")
                )
            )
        ).thenAnswer { invocation ->
            val owner = invocation.arguments[0] as Owner
            assertEquals("011-123-4567", owner.phoneNumber.value)
            owner
        }

        val result = useCase.signUp(command)
        assertNotNull(result)
    }
}
