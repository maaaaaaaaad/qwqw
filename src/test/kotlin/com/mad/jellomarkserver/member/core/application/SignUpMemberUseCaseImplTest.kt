package com.mad.jellomarkserver.member.core.application

import com.mad.jellomarkserver.member.core.domain.exception.DuplicateEmailException
import com.mad.jellomarkserver.member.core.domain.exception.DuplicateNicknameException
import com.mad.jellomarkserver.member.core.domain.exception.InvalidEmailException
import com.mad.jellomarkserver.member.core.domain.exception.InvalidNicknameException
import com.mad.jellomarkserver.member.core.domain.model.Email
import com.mad.jellomarkserver.member.core.domain.model.Member
import com.mad.jellomarkserver.member.core.domain.model.Nickname
import com.mad.jellomarkserver.member.port.driven.MemberPort
import com.mad.jellomarkserver.member.port.driving.SignUpMemberCommand
import com.mad.jellomarkserver.member.port.driving.SignUpMemberUseCase
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
class SignUpMemberUseCaseImplTest {

    @Mock
    private lateinit var memberPort: MemberPort

    private lateinit var useCase: SignUpMemberUseCase

    @BeforeEach
    fun setup() {
        useCase = SignUpMemberUseCaseImpl(memberPort)
    }

    @Test
    fun `should throw InvalidNicknameException when nickname is blank`() {
        val command = SignUpMemberCommand(
            nickname = "   ",
            email = "test@example.com"
        )

        assertFailsWith<InvalidNicknameException> {
            useCase.signUp(command)
        }
    }

    @Test
    fun `should throw InvalidNicknameException when nickname is empty`() {
        val command = SignUpMemberCommand(
            nickname = "",
            email = "test@example.com"
        )

        assertFailsWith<InvalidNicknameException> {
            useCase.signUp(command)
        }
    }

    @Test
    fun `should throw InvalidNicknameException when nickname is too short`() {
        val command = SignUpMemberCommand(
            nickname = "a",
            email = "test@example.com"
        )

        assertFailsWith<InvalidNicknameException> {
            useCase.signUp(command)
        }
    }

    @Test
    fun `should throw InvalidNicknameException when nickname is too long`() {
        val command = SignUpMemberCommand(
            nickname = "verylongnickname",
            email = "test@example.com"
        )

        assertFailsWith<InvalidNicknameException> {
            useCase.signUp(command)
        }
    }

    @Test
    fun `should throw InvalidNicknameException when nickname contains whitespace`() {
        val command = SignUpMemberCommand(
            nickname = "test user",
            email = "test@example.com"
        )

        assertFailsWith<InvalidNicknameException> {
            useCase.signUp(command)
        }
    }

    @Test
    fun `should throw InvalidEmailException when email is blank`() {
        val command = SignUpMemberCommand(
            nickname = "testuser",
            email = "   "
        )

        assertFailsWith<InvalidEmailException> {
            useCase.signUp(command)
        }
    }

    @Test
    fun `should throw InvalidEmailException when email is empty`() {
        val command = SignUpMemberCommand(
            nickname = "testuser",
            email = ""
        )

        assertFailsWith<InvalidEmailException> {
            useCase.signUp(command)
        }
    }

    @Test
    fun `should throw InvalidEmailException when email has no at sign`() {
        val command = SignUpMemberCommand(
            nickname = "testuser",
            email = "testexample.com"
        )

        assertFailsWith<InvalidEmailException> {
            useCase.signUp(command)
        }
    }

    @Test
    fun `should throw InvalidEmailException when email has no domain`() {
        val command = SignUpMemberCommand(
            nickname = "testuser",
            email = "test@"
        )

        assertFailsWith<InvalidEmailException> {
            useCase.signUp(command)
        }
    }

    @Test
    fun `should throw InvalidEmailException when email has no local part`() {
        val command = SignUpMemberCommand(
            nickname = "testuser",
            email = "@example.com"
        )

        assertFailsWith<InvalidEmailException> {
            useCase.signUp(command)
        }
    }

    @Test
    fun `should throw InvalidEmailException when email has no top-level domain`() {
        val command = SignUpMemberCommand(
            nickname = "testuser",
            email = "test@example"
        )

        assertFailsWith<InvalidEmailException> {
            useCase.signUp(command)
        }
    }

    @Test
    fun `should throw InvalidEmailException when email has invalid format`() {
        val command = SignUpMemberCommand(
            nickname = "testuser",
            email = "test@@example.com"
        )

        assertFailsWith<InvalidEmailException> {
            useCase.signUp(command)
        }
    }

    @Test
    fun `should sign up member successfully with valid values`() {
        val command = SignUpMemberCommand(
            nickname = "testuser",
            email = "test@example.com"
        )

        `when`(
            memberPort.save(
                org.mockito.ArgumentMatchers.any() ?: Member.create(
                    Nickname.of("testuser"),
                    Email.of("test@example.com")
                )
            )
        ).thenAnswer { invocation ->
            invocation.arguments[0] as Member
        }

        val result = useCase.signUp(command)

        assertNotNull(result)
        assertEquals("testuser", result.nickname.value)
        assertEquals("test@example.com", result.email.value)
    }

    @Test
    fun `should sign up member successfully with minimum length nickname`() {
        val command = SignUpMemberCommand(
            nickname = "ab",
            email = "test@example.com"
        )

        `when`(
            memberPort.save(
                org.mockito.ArgumentMatchers.any() ?: Member.create(
                    Nickname.of("ab"),
                    Email.of("test@example.com")
                )
            )
        ).thenAnswer { invocation ->
            invocation.arguments[0] as Member
        }

        val result = useCase.signUp(command)

        assertNotNull(result)
        assertEquals("ab", result.nickname.value)
        assertEquals("test@example.com", result.email.value)
    }

    @Test
    fun `should sign up member successfully with maximum length nickname`() {
        val command = SignUpMemberCommand(
            nickname = "12345678",
            email = "test@example.com"
        )

        `when`(
            memberPort.save(
                org.mockito.ArgumentMatchers.any() ?: Member.create(
                    Nickname.of("12345678"),
                    Email.of("test@example.com")
                )
            )
        ).thenAnswer { invocation ->
            invocation.arguments[0] as Member
        }

        val result = useCase.signUp(command)

        assertNotNull(result)
        assertEquals("12345678", result.nickname.value)
        assertEquals("test@example.com", result.email.value)
    }

    @Test
    fun `should sign up member successfully with special characters in nickname`() {
        val command = SignUpMemberCommand(
            nickname = "user_123",
            email = "test@example.com"
        )

        `when`(
            memberPort.save(
                org.mockito.ArgumentMatchers.any() ?: Member.create(
                    Nickname.of("user_123"),
                    Email.of("test@example.com")
                )
            )
        ).thenAnswer { invocation ->
            invocation.arguments[0] as Member
        }

        val result = useCase.signUp(command)

        assertNotNull(result)
        assertEquals("user_123", result.nickname.value)
    }

    @Test
    fun `should sign up member successfully with special characters in email`() {
        val command = SignUpMemberCommand(
            nickname = "testuser",
            email = "test+tag@example.com"
        )

        `when`(
            memberPort.save(
                org.mockito.ArgumentMatchers.any() ?: Member.create(
                    Nickname.of("testuser"),
                    Email.of("test+tag@example.com")
                )
            )
        ).thenAnswer { invocation ->
            invocation.arguments[0] as Member
        }

        val result = useCase.signUp(command)

        assertNotNull(result)
        assertEquals("test+tag@example.com", result.email.value)
    }

    @Test
    fun `should sign up member successfully with subdomain email`() {
        val command = SignUpMemberCommand(
            nickname = "testuser",
            email = "test@mail.example.com"
        )

        `when`(
            memberPort.save(
                org.mockito.ArgumentMatchers.any() ?: Member.create(
                    Nickname.of("testuser"),
                    Email.of("test@mail.example.com")
                )
            )
        ).thenAnswer { invocation ->
            invocation.arguments[0] as Member
        }

        val result = useCase.signUp(command)

        assertNotNull(result)
        assertEquals("test@mail.example.com", result.email.value)
    }

    @Test
    fun `should sign up member successfully with uppercase email`() {
        val command = SignUpMemberCommand(
            nickname = "testuser",
            email = "TEST@EXAMPLE.COM"
        )

        `when`(
            memberPort.save(
                org.mockito.ArgumentMatchers.any() ?: Member.create(
                    Nickname.of("testuser"),
                    Email.of("TEST@EXAMPLE.COM")
                )
            )
        ).thenAnswer { invocation ->
            invocation.arguments[0] as Member
        }

        val result = useCase.signUp(command)

        assertNotNull(result)
        assertEquals("TEST@EXAMPLE.COM", result.email.value)
    }

    @Test
    fun `should sign up member successfully with numeric nickname`() {
        val command = SignUpMemberCommand(
            nickname = "12345678",
            email = "test@example.com"
        )

        `when`(
            memberPort.save(
                org.mockito.ArgumentMatchers.any() ?: Member.create(
                    Nickname.of("12345678"),
                    Email.of("test@example.com")
                )
            )
        ).thenAnswer { invocation ->
            invocation.arguments[0] as Member
        }

        val result = useCase.signUp(command)

        assertNotNull(result)
        assertEquals("12345678", result.nickname.value)
    }

    @Test
    fun `should trim whitespace from nickname before validation`() {
        val command = SignUpMemberCommand(
            nickname = "  testuser  ",
            email = "test@example.com"
        )

        `when`(
            memberPort.save(
                org.mockito.ArgumentMatchers.any() ?: Member.create(
                    Nickname.of("testuser"),
                    Email.of("test@example.com")
                )
            )
        ).thenAnswer { invocation ->
            invocation.arguments[0] as Member
        }

        val result = useCase.signUp(command)

        assertNotNull(result)
        assertEquals("testuser", result.nickname.value)
    }

    @Test
    fun `should trim whitespace from email before validation`() {
        val command = SignUpMemberCommand(
            nickname = "testuser",
            email = "  test@example.com  "
        )

        `when`(
            memberPort.save(
                org.mockito.ArgumentMatchers.any() ?: Member.create(
                    Nickname.of("testuser"),
                    Email.of("test@example.com")
                )
            )
        ).thenAnswer { invocation ->
            invocation.arguments[0] as Member
        }

        val result = useCase.signUp(command)

        assertNotNull(result)
        assertEquals("test@example.com", result.email.value)
    }

    @Test
    fun `should throw DuplicateEmailException when email already exists`() {
        val command = SignUpMemberCommand(
            nickname = "testuser",
            email = "duplicate@example.com"
        )

        `when`(
            memberPort.save(
                org.mockito.ArgumentMatchers.any() ?: Member.create(
                    Nickname.of("testuser"),
                    Email.of("duplicate@example.com")
                )
            )
        ).thenThrow(DuplicateEmailException("duplicate@example.com"))

        val exception = assertFailsWith<DuplicateEmailException> {
            useCase.signUp(command)
        }

        assertEquals("Email already in use: duplicate@example.com", exception.message)
    }

    @Test
    fun `should throw DuplicateNicknameException when nickname already exists`() {
        val command = SignUpMemberCommand(
            nickname = "dupname",
            email = "test@example.com"
        )

        `when`(
            memberPort.save(
                org.mockito.ArgumentMatchers.any() ?: Member.create(
                    Nickname.of("dupname"),
                    Email.of("test@example.com")
                )
            )
        ).thenThrow(DuplicateNicknameException("dupname"))

        val exception = assertFailsWith<DuplicateNicknameException> {
            useCase.signUp(command)
        }

        assertEquals("Nickname already in use: dupname", exception.message)
    }

    @Test
    fun `should throw DuplicateEmailException with correct email value`() {
        val command = SignUpMemberCommand(
            nickname = "testuser",
            email = "admin@test.com"
        )

        `when`(
            memberPort.save(
                org.mockito.ArgumentMatchers.any() ?: Member.create(
                    Nickname.of("testuser"),
                    Email.of("admin@test.com")
                )
            )
        ).thenThrow(DuplicateEmailException("admin@test.com"))

        val exception = assertFailsWith<DuplicateEmailException> {
            useCase.signUp(command)
        }

        assertEquals("Email already in use: admin@test.com", exception.message)
    }

    @Test
    fun `should throw DuplicateNicknameException with correct nickname value`() {
        val command = SignUpMemberCommand(
            nickname = "admin123",
            email = "test@example.com"
        )

        `when`(
            memberPort.save(
                org.mockito.ArgumentMatchers.any() ?: Member.create(
                    Nickname.of("admin123"),
                    Email.of("test@example.com")
                )
            )
        ).thenThrow(DuplicateNicknameException("admin123"))

        val exception = assertFailsWith<DuplicateNicknameException> {
            useCase.signUp(command)
        }

        assertEquals("Nickname already in use: admin123", exception.message)
    }

    @Test
    fun `should create member with non-null id`() {
        val command = SignUpMemberCommand(
            nickname = "testuser",
            email = "test@example.com"
        )

        `when`(
            memberPort.save(
                org.mockito.ArgumentMatchers.any() ?: Member.create(
                    Nickname.of("testuser"),
                    Email.of("test@example.com")
                )
            )
        ).thenAnswer { invocation ->
            invocation.arguments[0] as Member
        }

        val result = useCase.signUp(command)

        assertNotNull(result.id)
        assertNotNull(result.id.value)
    }

    @Test
    fun `should create member with non-null timestamps`() {
        val command = SignUpMemberCommand(
            nickname = "testuser",
            email = "test@example.com"
        )

        `when`(
            memberPort.save(
                org.mockito.ArgumentMatchers.any() ?: Member.create(
                    Nickname.of("testuser"),
                    Email.of("test@example.com")
                )
            )
        ).thenAnswer { invocation ->
            invocation.arguments[0] as Member
        }

        val result = useCase.signUp(command)

        assertNotNull(result.createdAt)
        assertNotNull(result.updatedAt)
    }

    @Test
    fun `should sign up member with hyphenated nickname`() {
        val command = SignUpMemberCommand(
            nickname = "user-123",
            email = "test@example.com"
        )

        `when`(
            memberPort.save(
                org.mockito.ArgumentMatchers.any() ?: Member.create(
                    Nickname.of("user-123"),
                    Email.of("test@example.com")
                )
            )
        ).thenAnswer { invocation ->
            invocation.arguments[0] as Member
        }

        val result = useCase.signUp(command)

        assertNotNull(result)
        assertEquals("user-123", result.nickname.value)
    }

    @Test
    fun `should sign up member with dotted email`() {
        val command = SignUpMemberCommand(
            nickname = "testuser",
            email = "test.user@example.com"
        )

        `when`(
            memberPort.save(
                org.mockito.ArgumentMatchers.any() ?: Member.create(
                    Nickname.of("testuser"),
                    Email.of("test.user@example.com")
                )
            )
        ).thenAnswer { invocation ->
            invocation.arguments[0] as Member
        }

        val result = useCase.signUp(command)

        assertNotNull(result)
        assertEquals("test.user@example.com", result.email.value)
    }

    @Test
    fun `should throw InvalidEmailException when email has multiple at signs`() {
        val command = SignUpMemberCommand(
            nickname = "testuser",
            email = "test@test@example.com"
        )

        assertFailsWith<InvalidEmailException> {
            useCase.signUp(command)
        }
    }
}
