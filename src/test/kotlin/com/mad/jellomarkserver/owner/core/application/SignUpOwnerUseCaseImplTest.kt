package com.mad.jellomarkserver.owner.core.application

import com.mad.jellomarkserver.owner.core.domain.exception.*
import com.mad.jellomarkserver.owner.core.domain.model.Owner
import com.mad.jellomarkserver.owner.core.domain.model.OwnerEmail
import com.mad.jellomarkserver.owner.core.domain.model.OwnerNickname
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
            phoneNumber = "010-1234-5678",
            nickname = "test",
            email = "test@example.com"
        )

        assertFailsWith<InvalidOwnerBusinessNumberException> {
            useCase.signUp(command)
        }
    }

    @Test
    fun `should throw InvalidBusinessNumberException when business number is empty`() {
        val command = SignUpOwnerCommand(
            businessNumber = "",
            phoneNumber = "010-1234-5678",
            nickname = "test",
            email = "test@example.com"
        )

        assertFailsWith<InvalidOwnerBusinessNumberException> {
            useCase.signUp(command)
        }
    }

    @Test
    fun `should throw InvalidBusinessNumberException when business number is too short`() {
        val command = SignUpOwnerCommand(
            businessNumber = "12345678",
            phoneNumber = "010-1234-5678",
            nickname = "test",
            email = "test@example.com"
        )

        assertFailsWith<InvalidOwnerBusinessNumberException> {
            useCase.signUp(command)
        }
    }

    @Test
    fun `should throw InvalidBusinessNumberException when business number is too long`() {
        val command = SignUpOwnerCommand(
            businessNumber = "1234567890",
            phoneNumber = "010-1234-5678",
            nickname = "test",
            email = "test@example.com"
        )

        assertFailsWith<InvalidOwnerBusinessNumberException> {
            useCase.signUp(command)
        }
    }

    @Test
    fun `should throw InvalidPhoneNumberException when phone number is blank`() {
        val command = SignUpOwnerCommand(
            businessNumber = "123456789",
            phoneNumber = "   ",
            nickname = "test",
            email = "test@example.com"
        )

        assertFailsWith<InvalidOwnerPhoneNumberException> {
            useCase.signUp(command)
        }
    }

    @Test
    fun `should throw InvalidPhoneNumberException when phone number is empty`() {
        val command = SignUpOwnerCommand(
            businessNumber = "123456789",
            phoneNumber = "",
            nickname = "test",
            email = "test@example.com"
        )

        assertFailsWith<InvalidOwnerPhoneNumberException> {
            useCase.signUp(command)
        }
    }

    @Test
    fun `should throw InvalidPhoneNumberException when phone number format is invalid`() {
        val command = SignUpOwnerCommand(
            businessNumber = "123456789",
            phoneNumber = "01012345678",
            nickname = "test",
            email = "test@example.com"
        )

        assertFailsWith<InvalidOwnerPhoneNumberException> {
            useCase.signUp(command)
        }
    }

    @Test
    fun `should throw InvalidPhoneNumberException when phone number has no hyphens`() {
        val command = SignUpOwnerCommand(
            businessNumber = "123456789",
            phoneNumber = "010 1234 5678",
            nickname = "test",
            email = "test@example.com"
        )

        assertFailsWith<InvalidOwnerPhoneNumberException> {
            useCase.signUp(command)
        }
    }

    @Test
    fun `should throw InvalidPhoneNumberException when phone number starts with invalid prefix`() {
        val command = SignUpOwnerCommand(
            businessNumber = "123456789",
            phoneNumber = "090-1234-5678",
            nickname = "test",
            email = "test@example.com"
        )

        assertFailsWith<InvalidOwnerPhoneNumberException> {
            useCase.signUp(command)
        }
    }

    @Test
    fun `should throw DuplicateBusinessNumberException when business number already exists`() {
        val command = SignUpOwnerCommand(
            businessNumber = "123456789",
            phoneNumber = "010-1234-5678",
            nickname = "test",
            email = "test@example.com"
        )

        `when`(
            ownerPort.save(
                org.mockito.ArgumentMatchers.any() ?: Owner.create(
                    com.mad.jellomarkserver.owner.core.domain.model.BusinessNumber.of("123456789"),
                    com.mad.jellomarkserver.owner.core.domain.model.OwnerPhoneNumber.of("010-1234-5678"),
                    OwnerNickname.of("test"),
                    OwnerEmail.of("test@example.com")
                )
            )
        ).thenThrow(DuplicateOwnerBusinessNumberException("123456789"))

        val exception = assertFailsWith<DuplicateOwnerBusinessNumberException> {
            useCase.signUp(command)
        }

        assertEquals("Duplicate business number: 123456789", exception.message)
    }

    @Test
    fun `should throw DuplicatePhoneNumberException when phone number already exists`() {
        val command = SignUpOwnerCommand(
            businessNumber = "123456789",
            phoneNumber = "010-1234-5678",
            nickname = "test",
            email = "test@example.com"
        )

        `when`(
            ownerPort.save(
                org.mockito.ArgumentMatchers.any() ?: Owner.create(
                    com.mad.jellomarkserver.owner.core.domain.model.BusinessNumber.of("123456789"),
                    com.mad.jellomarkserver.owner.core.domain.model.OwnerPhoneNumber.of("010-1234-5678"),
                    OwnerNickname.of("test"),
                    OwnerEmail.of("test@example.com")
                )
            )
        ).thenThrow(DuplicateOwnerPhoneNumberException("010-1234-5678"))

        val exception = assertFailsWith<DuplicateOwnerPhoneNumberException> {
            useCase.signUp(command)
        }

        assertEquals("Duplicate phone number: 010-1234-5678", exception.message)
    }

    @Test
    fun `should validate and trim whitespace from business number`() {
        val command = SignUpOwnerCommand(
            businessNumber = "  123456789  ",
            phoneNumber = "010-1234-5678",
            nickname = "test",
            email = "test@example.com"
        )

        `when`(
            ownerPort.save(
                org.mockito.ArgumentMatchers.any() ?: Owner.create(
                    com.mad.jellomarkserver.owner.core.domain.model.BusinessNumber.of("123456789"),
                    com.mad.jellomarkserver.owner.core.domain.model.OwnerPhoneNumber.of("010-1234-5678"),
                    OwnerNickname.of("test"),
                    OwnerEmail.of("test@example.com")
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
            phoneNumber = "  010-1234-5678  ",
            nickname = "test",
            email = "test@example.com"
        )

        `when`(
            ownerPort.save(
                org.mockito.ArgumentMatchers.any() ?: Owner.create(
                    com.mad.jellomarkserver.owner.core.domain.model.BusinessNumber.of("123456789"),
                    com.mad.jellomarkserver.owner.core.domain.model.OwnerPhoneNumber.of("010-1234-5678"),
                    OwnerNickname.of("test"),
                    OwnerEmail.of("test@example.com")
                )
            )
        ).thenAnswer { invocation ->
            val owner = invocation.arguments[0] as Owner
            assertEquals("010-1234-5678", owner.ownerPhoneNumber.value)
            owner
        }

        val result = useCase.signUp(command)
        assertNotNull(result)
    }

    @Test
    fun `should accept alphanumeric business number`() {
        val command = SignUpOwnerCommand(
            businessNumber = "abc123xyz",
            phoneNumber = "010-1234-5678",
            nickname = "test",
            email = "test@example.com"
        )

        `when`(
            ownerPort.save(
                org.mockito.ArgumentMatchers.any() ?: Owner.create(
                    com.mad.jellomarkserver.owner.core.domain.model.BusinessNumber.of("abc123xyz"),
                    com.mad.jellomarkserver.owner.core.domain.model.OwnerPhoneNumber.of("010-1234-5678"),
                    OwnerNickname.of("test"),
                    OwnerEmail.of("test@example.com")
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
            phoneNumber = "010-1234-5678",
            nickname = "test",
            email = "test@example.com"
        )

        `when`(
            ownerPort.save(
                org.mockito.ArgumentMatchers.any() ?: Owner.create(
                    com.mad.jellomarkserver.owner.core.domain.model.BusinessNumber.of("12-34.567"),
                    com.mad.jellomarkserver.owner.core.domain.model.OwnerPhoneNumber.of("010-1234-5678"),
                    OwnerNickname.of("test"),
                    OwnerEmail.of("test@example.com")
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
            phoneNumber = "010-1234-5678",
            nickname = "test",
            email = "test@example.com"
        )

        `when`(
            ownerPort.save(
                org.mockito.ArgumentMatchers.any() ?: Owner.create(
                    com.mad.jellomarkserver.owner.core.domain.model.BusinessNumber.of("ABCDEFGHI"),
                    com.mad.jellomarkserver.owner.core.domain.model.OwnerPhoneNumber.of("010-1234-5678"),
                    OwnerNickname.of("test"),
                    OwnerEmail.of("test@example.com")
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
            phoneNumber = "02-1234-5678",
            nickname = "test",
            email = "test@example.com"
        )

        `when`(
            ownerPort.save(
                org.mockito.ArgumentMatchers.any() ?: Owner.create(
                    com.mad.jellomarkserver.owner.core.domain.model.BusinessNumber.of("123456789"),
                    com.mad.jellomarkserver.owner.core.domain.model.OwnerPhoneNumber.of("02-1234-5678"),
                    OwnerNickname.of("test"),
                    OwnerEmail.of("test@example.com")
                )
            )
        ).thenAnswer { invocation ->
            val owner = invocation.arguments[0] as Owner
            assertEquals("02-1234-5678", owner.ownerPhoneNumber.value)
            owner
        }

        val result = useCase.signUp(command)
        assertNotNull(result)
    }

    @Test
    fun `should accept regional phone number`() {
        val command = SignUpOwnerCommand(
            businessNumber = "123456789",
            phoneNumber = "031-123-4567",
            nickname = "test",
            email = "test@example.com"
        )

        `when`(
            ownerPort.save(
                org.mockito.ArgumentMatchers.any() ?: Owner.create(
                    com.mad.jellomarkserver.owner.core.domain.model.BusinessNumber.of("123456789"),
                    com.mad.jellomarkserver.owner.core.domain.model.OwnerPhoneNumber.of("031-123-4567"),
                    OwnerNickname.of("test"),
                    OwnerEmail.of("test@example.com")
                )
            )
        ).thenAnswer { invocation ->
            val owner = invocation.arguments[0] as Owner
            assertEquals("031-123-4567", owner.ownerPhoneNumber.value)
            owner
        }

        val result = useCase.signUp(command)
        assertNotNull(result)
    }

    @Test
    fun `should accept phone number starting with 011`() {
        val command = SignUpOwnerCommand(
            businessNumber = "123456789",
            phoneNumber = "011-123-4567",
            nickname = "test",
            email = "test@example.com"
        )

        `when`(
            ownerPort.save(
                org.mockito.ArgumentMatchers.any() ?: Owner.create(
                    com.mad.jellomarkserver.owner.core.domain.model.BusinessNumber.of("123456789"),
                    com.mad.jellomarkserver.owner.core.domain.model.OwnerPhoneNumber.of("011-123-4567"),
                    OwnerNickname.of("test"),
                    OwnerEmail.of("test@example.com")
                )
            )
        ).thenAnswer { invocation ->
            val owner = invocation.arguments[0] as Owner
            assertEquals("011-123-4567", owner.ownerPhoneNumber.value)
            owner
        }

        val result = useCase.signUp(command)
        assertNotNull(result)
    }

    @Test
    fun `should throw InvalidOwnerNicknameException when nickname is blank`() {
        val command = SignUpOwnerCommand(
            businessNumber = "123456789",
            phoneNumber = "010-1234-5678",
            nickname = "   ",
            email = "test@example.com"
        )

        assertFailsWith<InvalidOwnerNicknameException> {
            useCase.signUp(command)
        }
    }

    @Test
    fun `should throw InvalidOwnerNicknameException when nickname is empty`() {
        val command = SignUpOwnerCommand(
            businessNumber = "123456789",
            phoneNumber = "010-1234-5678",
            nickname = "",
            email = "test@example.com"
        )

        assertFailsWith<InvalidOwnerNicknameException> {
            useCase.signUp(command)
        }
    }

    @Test
    fun `should throw InvalidOwnerNicknameException when nickname is too short`() {
        val command = SignUpOwnerCommand(
            businessNumber = "123456789",
            phoneNumber = "010-1234-5678",
            nickname = "a",
            email = "test@example.com"
        )

        assertFailsWith<InvalidOwnerNicknameException> {
            useCase.signUp(command)
        }
    }

    @Test
    fun `should throw InvalidOwnerNicknameException when nickname is too long`() {
        val command = SignUpOwnerCommand(
            businessNumber = "123456789",
            phoneNumber = "010-1234-5678",
            nickname = "verylongnickname",
            email = "test@example.com"
        )

        assertFailsWith<InvalidOwnerNicknameException> {
            useCase.signUp(command)
        }
    }

    @Test
    fun `should throw InvalidOwnerNicknameException when nickname contains whitespace`() {
        val command = SignUpOwnerCommand(
            businessNumber = "123456789",
            phoneNumber = "010-1234-5678",
            nickname = "test user",
            email = "test@example.com"
        )

        assertFailsWith<InvalidOwnerNicknameException> {
            useCase.signUp(command)
        }
    }

    @Test
    fun `should sign up owner successfully with minimum length nickname`() {
        val command = SignUpOwnerCommand(
            businessNumber = "123456789",
            phoneNumber = "010-1234-5678",
            nickname = "ab",
            email = "test@example.com"
        )

        `when`(
            ownerPort.save(
                org.mockito.ArgumentMatchers.any() ?: Owner.create(
                    com.mad.jellomarkserver.owner.core.domain.model.BusinessNumber.of("123456789"),
                    com.mad.jellomarkserver.owner.core.domain.model.OwnerPhoneNumber.of("010-1234-5678"),
                    OwnerNickname.of("ab"),
                    OwnerEmail.of("test@example.com")
                )
            )
        ).thenAnswer { invocation ->
            invocation.arguments[0] as Owner
        }

        val result = useCase.signUp(command)

        assertNotNull(result)
        assertEquals("ab", result.ownerNickname.value)
    }

    @Test
    fun `should sign up owner successfully with maximum length nickname`() {
        val command = SignUpOwnerCommand(
            businessNumber = "123456789",
            phoneNumber = "010-1234-5678",
            nickname = "12345678",
            email = "test@example.com"
        )

        `when`(
            ownerPort.save(
                org.mockito.ArgumentMatchers.any() ?: Owner.create(
                    com.mad.jellomarkserver.owner.core.domain.model.BusinessNumber.of("123456789"),
                    com.mad.jellomarkserver.owner.core.domain.model.OwnerPhoneNumber.of("010-1234-5678"),
                    OwnerNickname.of("12345678"),
                    OwnerEmail.of("test@example.com")
                )
            )
        ).thenAnswer { invocation ->
            invocation.arguments[0] as Owner
        }

        val result = useCase.signUp(command)

        assertNotNull(result)
        assertEquals("12345678", result.ownerNickname.value)
    }

    @Test
    fun `should sign up owner successfully with special characters in nickname`() {
        val command = SignUpOwnerCommand(
            businessNumber = "123456789",
            phoneNumber = "010-1234-5678",
            nickname = "user_123",
            email = "test@example.com"
        )

        `when`(
            ownerPort.save(
                org.mockito.ArgumentMatchers.any() ?: Owner.create(
                    com.mad.jellomarkserver.owner.core.domain.model.BusinessNumber.of("123456789"),
                    com.mad.jellomarkserver.owner.core.domain.model.OwnerPhoneNumber.of("010-1234-5678"),
                    OwnerNickname.of("user_123"),
                    OwnerEmail.of("test@example.com")
                )
            )
        ).thenAnswer { invocation ->
            invocation.arguments[0] as Owner
        }

        val result = useCase.signUp(command)

        assertNotNull(result)
        assertEquals("user_123", result.ownerNickname.value)
    }

    @Test
    fun `should sign up owner successfully with numeric nickname`() {
        val command = SignUpOwnerCommand(
            businessNumber = "123456789",
            phoneNumber = "010-1234-5678",
            nickname = "12345678",
            email = "test@example.com"
        )

        `when`(
            ownerPort.save(
                org.mockito.ArgumentMatchers.any() ?: Owner.create(
                    com.mad.jellomarkserver.owner.core.domain.model.BusinessNumber.of("123456789"),
                    com.mad.jellomarkserver.owner.core.domain.model.OwnerPhoneNumber.of("010-1234-5678"),
                    OwnerNickname.of("12345678"),
                    OwnerEmail.of("test@example.com")
                )
            )
        ).thenAnswer { invocation ->
            invocation.arguments[0] as Owner
        }

        val result = useCase.signUp(command)

        assertNotNull(result)
        assertEquals("12345678", result.ownerNickname.value)
    }

    @Test
    fun `should sign up owner with hyphenated nickname`() {
        val command = SignUpOwnerCommand(
            businessNumber = "123456789",
            phoneNumber = "010-1234-5678",
            nickname = "user-123",
            email = "test@example.com"
        )

        `when`(
            ownerPort.save(
                org.mockito.ArgumentMatchers.any() ?: Owner.create(
                    com.mad.jellomarkserver.owner.core.domain.model.BusinessNumber.of("123456789"),
                    com.mad.jellomarkserver.owner.core.domain.model.OwnerPhoneNumber.of("010-1234-5678"),
                    OwnerNickname.of("user-123"),
                    OwnerEmail.of("test@example.com")
                )
            )
        ).thenAnswer { invocation ->
            invocation.arguments[0] as Owner
        }

        val result = useCase.signUp(command)

        assertNotNull(result)
        assertEquals("user-123", result.ownerNickname.value)
    }

    @Test
    fun `should trim whitespace from nickname before validation`() {
        val command = SignUpOwnerCommand(
            businessNumber = "123456789",
            phoneNumber = "010-1234-5678",
            nickname = "  testuser  ",
            email = "test@example.com"
        )

        `when`(
            ownerPort.save(
                org.mockito.ArgumentMatchers.any() ?: Owner.create(
                    com.mad.jellomarkserver.owner.core.domain.model.BusinessNumber.of("123456789"),
                    com.mad.jellomarkserver.owner.core.domain.model.OwnerPhoneNumber.of("010-1234-5678"),
                    OwnerNickname.of("testuser"),
                    OwnerEmail.of("test@example.com")
                )
            )
        ).thenAnswer { invocation ->
            invocation.arguments[0] as Owner
        }

        val result = useCase.signUp(command)

        assertNotNull(result)
        assertEquals("testuser", result.ownerNickname.value)
    }

    @Test
    fun `should throw DuplicateOwnerNicknameException when nickname already exists`() {
        val command = SignUpOwnerCommand(
            businessNumber = "123456789",
            phoneNumber = "010-1234-5678",
            nickname = "dupname",
            email = "test@example.com"
        )

        `when`(
            ownerPort.save(
                org.mockito.ArgumentMatchers.any() ?: Owner.create(
                    com.mad.jellomarkserver.owner.core.domain.model.BusinessNumber.of("123456789"),
                    com.mad.jellomarkserver.owner.core.domain.model.OwnerPhoneNumber.of("010-1234-5678"),
                    OwnerNickname.of("dupname"),
                    OwnerEmail.of("test@example.com")
                )
            )
        ).thenThrow(DuplicateOwnerNicknameException("dupname"))

        val exception = assertFailsWith<DuplicateOwnerNicknameException> {
            useCase.signUp(command)
        }

        assertEquals("Nickname already in use: dupname", exception.message)
    }

    @Test
    fun `should throw DuplicateOwnerNicknameException with correct nickname value`() {
        val command = SignUpOwnerCommand(
            businessNumber = "123456789",
            phoneNumber = "010-1234-5678",
            nickname = "admin123",
            email = "test@example.com"
        )

        `when`(
            ownerPort.save(
                org.mockito.ArgumentMatchers.any() ?: Owner.create(
                    com.mad.jellomarkserver.owner.core.domain.model.BusinessNumber.of("123456789"),
                    com.mad.jellomarkserver.owner.core.domain.model.OwnerPhoneNumber.of("010-1234-5678"),
                    OwnerNickname.of("admin123"),
                    OwnerEmail.of("test@example.com")
                )
            )
        ).thenThrow(DuplicateOwnerNicknameException("admin123"))

        val exception = assertFailsWith<DuplicateOwnerNicknameException> {
            useCase.signUp(command)
        }

        assertEquals("Nickname already in use: admin123", exception.message)
    }

    @Test
    fun `should create owner with non-null id`() {
        val command = SignUpOwnerCommand(
            businessNumber = "123456789",
            phoneNumber = "010-1234-5678",
            nickname = "testuser",
            email = "test@example.com"
        )

        `when`(
            ownerPort.save(
                org.mockito.ArgumentMatchers.any() ?: Owner.create(
                    com.mad.jellomarkserver.owner.core.domain.model.BusinessNumber.of("123456789"),
                    com.mad.jellomarkserver.owner.core.domain.model.OwnerPhoneNumber.of("010-1234-5678"),
                    OwnerNickname.of("testuser"),
                    OwnerEmail.of("test@example.com")
                )
            )
        ).thenAnswer { invocation ->
            invocation.arguments[0] as Owner
        }

        val result = useCase.signUp(command)

        assertNotNull(result.id)
        assertNotNull(result.id.value)
    }

    @Test
    fun `should create owner with non-null timestamps`() {
        val command = SignUpOwnerCommand(
            businessNumber = "123456789",
            phoneNumber = "010-1234-5678",
            nickname = "testuser",
            email = "test@example.com"
        )

        `when`(
            ownerPort.save(
                org.mockito.ArgumentMatchers.any() ?: Owner.create(
                    com.mad.jellomarkserver.owner.core.domain.model.BusinessNumber.of("123456789"),
                    com.mad.jellomarkserver.owner.core.domain.model.OwnerPhoneNumber.of("010-1234-5678"),
                    OwnerNickname.of("testuser"),
                    OwnerEmail.of("test@example.com")
                )
            )
        ).thenAnswer { invocation ->
            invocation.arguments[0] as Owner
        }

        val result = useCase.signUp(command)

        assertNotNull(result.createdAt)
        assertNotNull(result.updatedAt)
    }

    @Test
    fun `should throw InvalidBusinessNumberException when business number contains only whitespace characters`() {
        val command = SignUpOwnerCommand(
            businessNumber = " \t\n ",
            phoneNumber = "010-1234-5678",
            nickname = "test",
            email = "test@example.com"
        )

        assertFailsWith<InvalidOwnerBusinessNumberException> {
            useCase.signUp(command)
        }
    }

    @Test
    fun `should throw InvalidPhoneNumberException when phone number has invalid separators`() {
        val command = SignUpOwnerCommand(
            businessNumber = "123456789",
            phoneNumber = "010.1234.5678",
            nickname = "test",
            email = "test@example.com"
        )

        assertFailsWith<InvalidOwnerPhoneNumberException> {
            useCase.signUp(command)
        }
    }

    @Test
    fun `should accept business number with minimum length`() {
        val command = SignUpOwnerCommand(
            businessNumber = "123456789",
            phoneNumber = "010-1234-5678",
            nickname = "test",
            email = "test@example.com"
        )

        `when`(
            ownerPort.save(
                org.mockito.ArgumentMatchers.any() ?: Owner.create(
                    com.mad.jellomarkserver.owner.core.domain.model.BusinessNumber.of("123456789"),
                    com.mad.jellomarkserver.owner.core.domain.model.OwnerPhoneNumber.of("010-1234-5678"),
                    OwnerNickname.of("test"),
                    OwnerEmail.of("test@example.com")
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
    fun `should accept phone number with different valid formats`() {
        val command = SignUpOwnerCommand(
            businessNumber = "123456789",
            phoneNumber = "010-1234-5678",
            nickname = "test",
            email = "test@example.com"
        )

        `when`(
            ownerPort.save(
                org.mockito.ArgumentMatchers.any() ?: Owner.create(
                    com.mad.jellomarkserver.owner.core.domain.model.BusinessNumber.of("123456789"),
                    com.mad.jellomarkserver.owner.core.domain.model.OwnerPhoneNumber.of("010-1234-5678"),
                    OwnerNickname.of("test"),
                    OwnerEmail.of("test@example.com")
                )
            )
        ).thenAnswer { invocation ->
            val owner = invocation.arguments[0] as Owner
            assertEquals("010-1234-5678", owner.ownerPhoneNumber.value)
            owner
        }

        val result = useCase.signUp(command)
        assertNotNull(result)
    }
}
