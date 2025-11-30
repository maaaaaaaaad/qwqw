package com.mad.jellomarkserver.auth.core.application

import com.mad.jellomarkserver.auth.core.domain.exception.DuplicateAuthEmailException
import com.mad.jellomarkserver.auth.core.domain.exception.InvalidAuthEmailException
import com.mad.jellomarkserver.auth.core.domain.exception.InvalidRawPasswordException
import com.mad.jellomarkserver.auth.core.domain.model.Auth
import com.mad.jellomarkserver.auth.core.domain.model.AuthEmail
import com.mad.jellomarkserver.auth.core.domain.model.RawPassword
import com.mad.jellomarkserver.auth.core.domain.model.UserType
import com.mad.jellomarkserver.auth.port.driven.AuthPort
import com.mad.jellomarkserver.auth.port.driving.SignUpAuthCommand
import com.mad.jellomarkserver.auth.port.driving.SignUpAuthUseCase
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import kotlin.test.assertFailsWith

@ExtendWith(MockitoExtension::class)
class SignUpAuthUseCaseImplTest {

    @Mock
    private lateinit var authPort: AuthPort

    private lateinit var useCase: SignUpAuthUseCase

    @BeforeEach
    fun setup() {
        useCase = SignUpAuthUseCaseImpl(authPort)
    }

    @Test
    fun `should throw InvalidAuthEmailException when email is blank`() {
        val command = SignUpAuthCommand(
            email = "   ",
            password = "MyP@ssw0rd!",
            userType = "MEMBER"
        )

        assertFailsWith<InvalidAuthEmailException> {
            useCase.signUp(command)
        }
    }

    @Test
    fun `should throw InvalidAuthEmailException when email is empty`() {
        val command = SignUpAuthCommand(
            email = "",
            password = "MyP@ssw0rd!",
            userType = "MEMBER"
        )

        assertFailsWith<InvalidAuthEmailException> {
            useCase.signUp(command)
        }
    }

    @Test
    fun `should throw InvalidAuthEmailException when email has no at sign`() {
        val command = SignUpAuthCommand(
            email = "testexample.com",
            password = "MyP@ssw0rd!",
            userType = "MEMBER"
        )

        assertFailsWith<InvalidAuthEmailException> {
            useCase.signUp(command)
        }
    }

    @Test
    fun `should throw InvalidAuthEmailException when email has no domain`() {
        val command = SignUpAuthCommand(
            email = "test@",
            password = "MyP@ssw0rd!",
            userType = "MEMBER"
        )

        assertFailsWith<InvalidAuthEmailException> {
            useCase.signUp(command)
        }
    }

    @Test
    fun `should throw InvalidAuthEmailException when email has no TLD`() {
        val command = SignUpAuthCommand(
            email = "test@example",
            password = "MyP@ssw0rd!",
            userType = "MEMBER"
        )

        assertFailsWith<InvalidAuthEmailException> {
            useCase.signUp(command)
        }
    }

    @Test
    fun `should throw InvalidRawPasswordException when password is too short`() {
        val command = SignUpAuthCommand(
            email = "test@example.com",
            password = "Abc123!",
            userType = "MEMBER"
        )

        assertFailsWith<InvalidRawPasswordException> {
            useCase.signUp(command)
        }
    }

    @Test
    fun `should throw InvalidRawPasswordException when password is too long`() {
        val command = SignUpAuthCommand(
            email = "test@example.com",
            password = "A1!" + "a".repeat(70),
            userType = "MEMBER"
        )

        assertFailsWith<InvalidRawPasswordException> {
            useCase.signUp(command)
        }
    }

    @Test
    fun `should throw InvalidRawPasswordException when password has no uppercase`() {
        val command = SignUpAuthCommand(
            email = "test@example.com",
            password = "abcd1234!",
            userType = "MEMBER"
        )

        assertFailsWith<InvalidRawPasswordException> {
            useCase.signUp(command)
        }
    }

    @Test
    fun `should throw InvalidRawPasswordException when password has no lowercase`() {
        val command = SignUpAuthCommand(
            email = "test@example.com",
            password = "ABCD1234!",
            userType = "MEMBER"
        )

        assertFailsWith<InvalidRawPasswordException> {
            useCase.signUp(command)
        }
    }

    @Test
    fun `should throw InvalidRawPasswordException when password has no digit`() {
        val command = SignUpAuthCommand(
            email = "test@example.com",
            password = "Abcdefgh!",
            userType = "MEMBER"
        )

        assertFailsWith<InvalidRawPasswordException> {
            useCase.signUp(command)
        }
    }

    @Test
    fun `should throw InvalidRawPasswordException when password has no special character`() {
        val command = SignUpAuthCommand(
            email = "test@example.com",
            password = "Abcd1234",
            userType = "MEMBER"
        )

        assertFailsWith<InvalidRawPasswordException> {
            useCase.signUp(command)
        }
    }

    @Test
    fun `should sign up auth successfully with valid data for MEMBER`() {
        val command = SignUpAuthCommand(
            email = "test@example.com",
            password = "MyP@ssw0rd!",
            userType = "MEMBER"
        )

        `when`(
            authPort.save(
                org.mockito.ArgumentMatchers.any() ?: Auth.create(
                    AuthEmail.of("test@example.com"),
                    RawPassword.of("MyP@ssw0rd!"),
                    UserType.MEMBER
                )
            )
        ).thenAnswer { it.arguments[0] }

        val auth = useCase.signUp(command)

        assertNotNull(auth.id)
        assertEquals("test@example.com", auth.email.value)
        assertEquals(UserType.MEMBER, auth.userType)
        assertNotNull(auth.createdAt)
        assertNotNull(auth.updatedAt)
    }

    @Test
    fun `should sign up auth successfully with valid data for OWNER`() {
        val command = SignUpAuthCommand(
            email = "owner@example.com",
            password = "0wnerP@ss!",
            userType = "OWNER"
        )

        `when`(
            authPort.save(
                org.mockito.ArgumentMatchers.any() ?: Auth.create(
                    AuthEmail.of("test@example.com"),
                    RawPassword.of("MyP@ssw0rd!"),
                    UserType.MEMBER
                )
            )
        ).thenAnswer { it.arguments[0] }

        val auth = useCase.signUp(command)

        assertNotNull(auth.id)
        assertEquals("owner@example.com", auth.email.value)
        assertEquals(UserType.OWNER, auth.userType)
        assertNotNull(auth.createdAt)
        assertNotNull(auth.updatedAt)
    }

    @Test
    fun `should sign up auth successfully with special characters in email`() {
        val command = SignUpAuthCommand(
            email = "user+tag@example.com",
            password = "MyP@ssw0rd!",
            userType = "MEMBER"
        )

        `when`(
            authPort.save(
                org.mockito.ArgumentMatchers.any() ?: Auth.create(
                    AuthEmail.of("test@example.com"),
                    RawPassword.of("MyP@ssw0rd!"),
                    UserType.MEMBER
                )
            )
        ).thenAnswer { it.arguments[0] }

        val auth = useCase.signUp(command)

        assertEquals("user+tag@example.com", auth.email.value)
    }

    @Test
    fun `should sign up auth successfully with subdomain email`() {
        val command = SignUpAuthCommand(
            email = "user@mail.example.com",
            password = "MyP@ssw0rd!",
            userType = "MEMBER"
        )

        `when`(
            authPort.save(
                org.mockito.ArgumentMatchers.any() ?: Auth.create(
                    AuthEmail.of("test@example.com"),
                    RawPassword.of("MyP@ssw0rd!"),
                    UserType.MEMBER
                )
            )
        ).thenAnswer { it.arguments[0] }

        val auth = useCase.signUp(command)

        assertEquals("user@mail.example.com", auth.email.value)
    }

    @Test
    fun `should sign up auth successfully with uppercase email`() {
        val command = SignUpAuthCommand(
            email = "TEST@EXAMPLE.COM",
            password = "MyP@ssw0rd!",
            userType = "MEMBER"
        )

        `when`(
            authPort.save(
                org.mockito.ArgumentMatchers.any() ?: Auth.create(
                    AuthEmail.of("test@example.com"),
                    RawPassword.of("MyP@ssw0rd!"),
                    UserType.MEMBER
                )
            )
        ).thenAnswer { it.arguments[0] }

        val auth = useCase.signUp(command)

        assertEquals("TEST@EXAMPLE.COM", auth.email.value)
    }

    @Test
    fun `should trim whitespace from email before validation`() {
        val command = SignUpAuthCommand(
            email = "  test@example.com  ",
            password = "MyP@ssw0rd!",
            userType = "MEMBER"
        )

        `when`(
            authPort.save(
                org.mockito.ArgumentMatchers.any() ?: Auth.create(
                    AuthEmail.of("test@example.com"),
                    RawPassword.of("MyP@ssw0rd!"),
                    UserType.MEMBER
                )
            )
        ).thenAnswer { it.arguments[0] }

        val auth = useCase.signUp(command)

        assertEquals("test@example.com", auth.email.value)
    }

    @Test
    fun `should sign up auth successfully with minimum length password`() {
        val command = SignUpAuthCommand(
            email = "test@example.com",
            password = "Abcd123!",
            userType = "MEMBER"
        )

        `when`(
            authPort.save(
                org.mockito.ArgumentMatchers.any() ?: Auth.create(
                    AuthEmail.of("test@example.com"),
                    RawPassword.of("MyP@ssw0rd!"),
                    UserType.MEMBER
                )
            )
        ).thenAnswer { it.arguments[0] }

        val auth = useCase.signUp(command)

        assertTrue(auth.hashedPassword.matches(RawPassword.of("Abcd123!")))
    }

    @Test
    fun `should sign up auth successfully with maximum length password`() {
        val command = SignUpAuthCommand(
            email = "test@example.com",
            password = "A1!" + "a".repeat(69),
            userType = "MEMBER"
        )

        `when`(
            authPort.save(
                org.mockito.ArgumentMatchers.any() ?: Auth.create(
                    AuthEmail.of("test@example.com"),
                    RawPassword.of("MyP@ssw0rd!"),
                    UserType.MEMBER
                )
            )
        ).thenAnswer { it.arguments[0] }

        val auth = useCase.signUp(command)

        assertTrue(auth.hashedPassword.matches(RawPassword.of("A1!" + "a".repeat(69))))
    }

    @Test
    fun `should sign up auth successfully with various special characters`() {
        val command = SignUpAuthCommand(
            email = "test@example.com",
            password = "P@ss#w0rd\$",
            userType = "MEMBER"
        )

        `when`(
            authPort.save(
                org.mockito.ArgumentMatchers.any() ?: Auth.create(
                    AuthEmail.of("test@example.com"),
                    RawPassword.of("MyP@ssw0rd!"),
                    UserType.MEMBER
                )
            )
        ).thenAnswer { it.arguments[0] }

        val auth = useCase.signUp(command)

        assertTrue(auth.hashedPassword.matches(RawPassword.of("P@ss#w0rd\$")))
    }

    @Test
    fun `should throw DuplicateAuthEmailException when email already exists`() {
        val command = SignUpAuthCommand(
            email = "duplicate@example.com",
            password = "MyP@ssw0rd!",
            userType = "MEMBER"
        )

        `when`(
            authPort.save(
                org.mockito.ArgumentMatchers.any() ?: Auth.create(
                    AuthEmail.of("test@example.com"),
                    RawPassword.of("MyP@ssw0rd!"),
                    UserType.MEMBER
                )
            )
        ).thenThrow(DuplicateAuthEmailException("duplicate@example.com"))

        assertFailsWith<DuplicateAuthEmailException> {
            useCase.signUp(command)
        }
    }

    @Test
    fun `should throw DuplicateAuthEmailException with correct email value`() {
        val command = SignUpAuthCommand(
            email = "test@example.com",
            password = "MyP@ssw0rd!",
            userType = "MEMBER"
        )

        `when`(
            authPort.save(
                org.mockito.ArgumentMatchers.any() ?: Auth.create(
                    AuthEmail.of("test@example.com"),
                    RawPassword.of("MyP@ssw0rd!"),
                    UserType.MEMBER
                )
            )
        ).thenThrow(DuplicateAuthEmailException("test@example.com"))

        val exception = assertFailsWith<DuplicateAuthEmailException> {
            useCase.signUp(command)
        }

        assertTrue(exception.message!!.contains("test@example.com"))
    }

    @Test
    fun `should create auth with non-null id`() {
        val command = SignUpAuthCommand(
            email = "test@example.com",
            password = "MyP@ssw0rd!",
            userType = "MEMBER"
        )

        `when`(
            authPort.save(
                org.mockito.ArgumentMatchers.any() ?: Auth.create(
                    AuthEmail.of("test@example.com"),
                    RawPassword.of("MyP@ssw0rd!"),
                    UserType.MEMBER
                )
            )
        ).thenAnswer { it.arguments[0] }

        val auth = useCase.signUp(command)

        assertNotNull(auth.id)
        assertNotNull(auth.id.value)
    }

    @Test
    fun `should create auth with non-null timestamps`() {
        val command = SignUpAuthCommand(
            email = "test@example.com",
            password = "MyP@ssw0rd!",
            userType = "MEMBER"
        )

        `when`(
            authPort.save(
                org.mockito.ArgumentMatchers.any() ?: Auth.create(
                    AuthEmail.of("test@example.com"),
                    RawPassword.of("MyP@ssw0rd!"),
                    UserType.MEMBER
                )
            )
        ).thenAnswer { it.arguments[0] }

        val auth = useCase.signUp(command)

        assertNotNull(auth.createdAt)
        assertNotNull(auth.updatedAt)
        assertEquals(auth.createdAt, auth.updatedAt)
    }

    @Test
    fun `should hash password before saving`() {
        val command = SignUpAuthCommand(
            email = "test@example.com",
            password = "MyP@ssw0rd!",
            userType = "MEMBER"
        )

        `when`(
            authPort.save(
                org.mockito.ArgumentMatchers.any() ?: Auth.create(
                    AuthEmail.of("test@example.com"),
                    RawPassword.of("MyP@ssw0rd!"),
                    UserType.MEMBER
                )
            )
        ).thenAnswer { it.arguments[0] }

        val auth = useCase.signUp(command)

        assertTrue(auth.hashedPassword.value.startsWith("\$2a\$"))
        assertEquals(60, auth.hashedPassword.value.length)
    }

    @Test
    fun `should verify hashed password matches raw password`() {
        val command = SignUpAuthCommand(
            email = "test@example.com",
            password = "MyP@ssw0rd!",
            userType = "MEMBER"
        )

        `when`(
            authPort.save(
                org.mockito.ArgumentMatchers.any() ?: Auth.create(
                    AuthEmail.of("test@example.com"),
                    RawPassword.of("MyP@ssw0rd!"),
                    UserType.MEMBER
                )
            )
        ).thenAnswer { it.arguments[0] }

        val auth = useCase.signUp(command)

        assertTrue(auth.hashedPassword.matches(RawPassword.of("MyP@ssw0rd!")))
    }

    @Test
    fun `should not store raw password`() {
        val command = SignUpAuthCommand(
            email = "test@example.com",
            password = "MyP@ssw0rd!",
            userType = "MEMBER"
        )

        `when`(
            authPort.save(
                org.mockito.ArgumentMatchers.any() ?: Auth.create(
                    AuthEmail.of("test@example.com"),
                    RawPassword.of("MyP@ssw0rd!"),
                    UserType.MEMBER
                )
            )
        ).thenAnswer { it.arguments[0] }

        val auth = useCase.signUp(command)

        assertTrue(!auth.hashedPassword.value.contains("MyP@ssw0rd!"))
    }

    @Test
    fun `should create auth with email from command`() {
        val command = SignUpAuthCommand(
            email = "specific@example.com",
            password = "MyP@ssw0rd!",
            userType = "MEMBER"
        )

        `when`(
            authPort.save(
                org.mockito.ArgumentMatchers.any() ?: Auth.create(
                    AuthEmail.of("test@example.com"),
                    RawPassword.of("MyP@ssw0rd!"),
                    UserType.MEMBER
                )
            )
        ).thenAnswer { it.arguments[0] }

        val auth = useCase.signUp(command)

        assertEquals("specific@example.com", auth.email.value)
    }

    @Test
    fun `should create auth with userType from command`() {
        val command = SignUpAuthCommand(
            email = "test@example.com",
            password = "MyP@ssw0rd!",
            userType = "OWNER"
        )

        `when`(
            authPort.save(
                org.mockito.ArgumentMatchers.any() ?: Auth.create(
                    AuthEmail.of("test@example.com"),
                    RawPassword.of("MyP@ssw0rd!"),
                    UserType.MEMBER
                )
            )
        ).thenAnswer { it.arguments[0] }

        val auth = useCase.signUp(command)

        assertEquals(UserType.OWNER, auth.userType)
    }

    @Test
    fun `should handle multiple sign ups with different emails`() {
        val command1 = SignUpAuthCommand(
            email = "user1@example.com",
            password = "User1P@ss!",
            userType = "MEMBER"
        )
        val command2 = SignUpAuthCommand(
            email = "user2@example.com",
            password = "User2P@ss!",
            userType = "OWNER"
        )

        `when`(
            authPort.save(
                org.mockito.ArgumentMatchers.any() ?: Auth.create(
                    AuthEmail.of("test@example.com"),
                    RawPassword.of("MyP@ssw0rd!"),
                    UserType.MEMBER
                )
            )
        ).thenAnswer { it.arguments[0] }

        val auth1 = useCase.signUp(command1)
        val auth2 = useCase.signUp(command2)

        assertEquals("user1@example.com", auth1.email.value)
        assertEquals("user2@example.com", auth2.email.value)
        assertEquals(UserType.MEMBER, auth1.userType)
        assertEquals(UserType.OWNER, auth2.userType)
    }

    @Test
    fun `should generate different ids for different sign ups`() {
        val command = SignUpAuthCommand(
            email = "test@example.com",
            password = "MyP@ssw0rd!",
            userType = "MEMBER"
        )

        `when`(
            authPort.save(
                org.mockito.ArgumentMatchers.any() ?: Auth.create(
                    AuthEmail.of("test@example.com"),
                    RawPassword.of("MyP@ssw0rd!"),
                    UserType.MEMBER
                )
            )
        ).thenAnswer { it.arguments[0] }

        val auth1 = useCase.signUp(command)
        val auth2 = useCase.signUp(command)

        assertNotNull(auth1.id)
        assertNotNull(auth2.id)
        assertTrue(auth1.id != auth2.id)
    }
}
