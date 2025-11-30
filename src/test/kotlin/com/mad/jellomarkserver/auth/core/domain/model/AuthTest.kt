package com.mad.jellomarkserver.auth.core.domain.model

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import java.util.*

class AuthTest {

    @Test
    fun `should create Auth for MEMBER with valid email and password`() {
        val email = AuthEmail.of("test@example.com")
        val rawPassword = RawPassword.of("MyP@ssw0rd!")

        val auth = Auth.create(email, rawPassword, UserType.MEMBER)

        assertNotNull(auth.id)
        assertEquals(email, auth.email)
        assertTrue(auth.hashedPassword.matches(rawPassword))
        assertEquals(UserType.MEMBER, auth.userType)
        assertNotNull(auth.createdAt)
        assertNotNull(auth.updatedAt)
        assertEquals(auth.createdAt, auth.updatedAt)
    }

    @Test
    fun `should create Auth for OWNER with valid email and password`() {
        val email = AuthEmail.of("owner@example.com")
        val rawPassword = RawPassword.of("0wnerP@ss!")

        val auth = Auth.create(email, rawPassword, UserType.OWNER)

        assertNotNull(auth.id)
        assertEquals(email, auth.email)
        assertTrue(auth.hashedPassword.matches(rawPassword))
        assertEquals(UserType.OWNER, auth.userType)
        assertNotNull(auth.createdAt)
        assertNotNull(auth.updatedAt)
        assertEquals(auth.createdAt, auth.updatedAt)
    }

    @Test
    fun `should create Auth with minimum valid password`() {
        val email = AuthEmail.of("test@example.com")
        val rawPassword = RawPassword.of("Abcd123!")

        val auth = Auth.create(email, rawPassword, UserType.MEMBER)

        assertTrue(auth.hashedPassword.matches(rawPassword))
    }

    @Test
    fun `should create Auth with maximum length password`() {
        val email = AuthEmail.of("test@example.com")
        val rawPassword = RawPassword.of("A1!" + "a".repeat(69))

        val auth = Auth.create(email, rawPassword, UserType.MEMBER)

        assertTrue(auth.hashedPassword.matches(rawPassword))
    }

    @Test
    fun `should create Auth with special characters in email`() {
        val email = AuthEmail.of("user+tag@example.com")
        val rawPassword = RawPassword.of("MyP@ssw0rd!")

        val auth = Auth.create(email, rawPassword, UserType.MEMBER)

        assertEquals(email, auth.email)
    }

    @Test
    fun `should create Auth with subdomain email`() {
        val email = AuthEmail.of("user@mail.example.com")
        val rawPassword = RawPassword.of("MyP@ssw0rd!")

        val auth = Auth.create(email, rawPassword, UserType.OWNER)

        assertEquals(email, auth.email)
    }

    @Test
    fun `should create Auth with uppercase email`() {
        val email = AuthEmail.of("TEST@EXAMPLE.COM")
        val rawPassword = RawPassword.of("MyP@ssw0rd!")

        val auth = Auth.create(email, rawPassword, UserType.MEMBER)

        assertEquals(email, auth.email)
    }

    @Test
    fun `should create Auth with fixed clock`() {
        val email = AuthEmail.of("test@example.com")
        val rawPassword = RawPassword.of("MyP@ssw0rd!")
        val fixedInstant = Instant.parse("2025-01-01T00:00:00Z")
        val fixedClock = Clock.fixed(fixedInstant, ZoneId.of("UTC"))

        val auth = Auth.create(email, rawPassword, UserType.MEMBER, fixedClock)

        assertEquals(fixedInstant, auth.createdAt)
        assertEquals(fixedInstant, auth.updatedAt)
    }

    @Test
    fun `should create Auth with system clock by default`() {
        val email = AuthEmail.of("test@example.com")
        val rawPassword = RawPassword.of("MyP@ssw0rd!")
        val before = Instant.now()

        val auth = Auth.create(email, rawPassword, UserType.MEMBER)

        val after = Instant.now()
        assert(auth.createdAt in before..after)
        assert(auth.updatedAt in before..after)
    }

    @Test
    fun `should hash different passwords differently`() {
        val email = AuthEmail.of("test@example.com")
        val password1 = RawPassword.of("MyP@ssw0rd!")
        val password2 = RawPassword.of("Different1!")

        val auth1 = Auth.create(email, password1, UserType.MEMBER)
        val auth2 = Auth.create(email, password2, UserType.MEMBER)

        assertNotEquals(auth1.hashedPassword.value, auth2.hashedPassword.value)
    }

    @Test
    fun `should reconstruct Auth with all fields for MEMBER`() {
        val id = AuthId.from(UUID.randomUUID())
        val email = AuthEmail.of("test@example.com")
        val hashedPassword = HashedPassword.fromRaw(RawPassword.of("MyP@ssw0rd!"))
        val createdAt = Instant.parse("2025-01-01T00:00:00Z")
        val updatedAt = Instant.parse("2025-01-01T00:00:00Z")

        val auth = Auth.reconstruct(id, email, hashedPassword, UserType.MEMBER, createdAt, updatedAt)

        assertEquals(id, auth.id)
        assertEquals(email, auth.email)
        assertEquals(hashedPassword, auth.hashedPassword)
        assertEquals(UserType.MEMBER, auth.userType)
        assertEquals(createdAt, auth.createdAt)
        assertEquals(updatedAt, auth.updatedAt)
    }

    @Test
    fun `should reconstruct Auth with all fields for OWNER`() {
        val id = AuthId.from(UUID.randomUUID())
        val email = AuthEmail.of("owner@example.com")
        val hashedPassword = HashedPassword.fromRaw(RawPassword.of("0wnerP@ss!"))
        val createdAt = Instant.parse("2025-01-01T00:00:00Z")
        val updatedAt = Instant.parse("2025-01-01T00:00:00Z")

        val auth = Auth.reconstruct(id, email, hashedPassword, UserType.OWNER, createdAt, updatedAt)

        assertEquals(id, auth.id)
        assertEquals(email, auth.email)
        assertEquals(hashedPassword, auth.hashedPassword)
        assertEquals(UserType.OWNER, auth.userType)
        assertEquals(createdAt, auth.createdAt)
        assertEquals(updatedAt, auth.updatedAt)
    }

    @Test
    fun `should reconstruct Auth with different created and updated timestamps`() {
        val id = AuthId.from(UUID.randomUUID())
        val email = AuthEmail.of("test@example.com")
        val hashedPassword = HashedPassword.fromRaw(RawPassword.of("MyP@ssw0rd!"))
        val createdAt = Instant.parse("2025-01-01T00:00:00Z")
        val updatedAt = Instant.parse("2025-06-01T12:30:45Z")

        val auth = Auth.reconstruct(id, email, hashedPassword, UserType.MEMBER, createdAt, updatedAt)

        assertEquals(createdAt, auth.createdAt)
        assertEquals(updatedAt, auth.updatedAt)
        assertNotEquals(auth.createdAt, auth.updatedAt)
    }

    @Test
    fun `should reconstruct Auth with high precision timestamp`() {
        val id = AuthId.from(UUID.randomUUID())
        val email = AuthEmail.of("test@example.com")
        val hashedPassword = HashedPassword.fromRaw(RawPassword.of("MyP@ssw0rd!"))
        val createdAt = Instant.parse("2025-01-01T12:34:56.123456789Z")
        val updatedAt = Instant.parse("2025-01-01T12:34:56.987654321Z")

        val auth = Auth.reconstruct(id, email, hashedPassword, UserType.MEMBER, createdAt, updatedAt)

        assertEquals(createdAt, auth.createdAt)
        assertEquals(updatedAt, auth.updatedAt)
    }

    @Test
    fun `should reconstruct Auth with all zeros UUID`() {
        val id = AuthId.from(UUID(0, 0))
        val email = AuthEmail.of("test@example.com")
        val hashedPassword = HashedPassword.fromRaw(RawPassword.of("MyP@ssw0rd!"))
        val createdAt = Instant.now()
        val updatedAt = Instant.now()

        val auth = Auth.reconstruct(id, email, hashedPassword, UserType.MEMBER, createdAt, updatedAt)

        assertEquals(id, auth.id)
    }

    @Test
    fun `should have equality based on id`() {
        val id = AuthId.from(UUID.randomUUID())
        val email1 = AuthEmail.of("user1@example.com")
        val email2 = AuthEmail.of("user2@example.com")
        val hashedPassword1 = HashedPassword.fromRaw(RawPassword.of("Pass1234!"))
        val hashedPassword2 = HashedPassword.fromRaw(RawPassword.of("Pass5678!"))
        val createdAt = Instant.parse("2025-01-01T00:00:00Z")
        val updatedAt = Instant.parse("2025-01-01T00:00:00Z")

        val auth1 = Auth.reconstruct(id, email1, hashedPassword1, UserType.MEMBER, createdAt, updatedAt)
        val auth2 = Auth.reconstruct(id, email2, hashedPassword2, UserType.OWNER, createdAt, updatedAt)

        assertEquals(auth1, auth2)
    }

    @Test
    fun `should not be equal when ids are different`() {
        val id1 = AuthId.from(UUID.randomUUID())
        val id2 = AuthId.from(UUID.randomUUID())
        val email = AuthEmail.of("test@example.com")
        val hashedPassword = HashedPassword.fromRaw(RawPassword.of("MyP@ssw0rd!"))
        val createdAt = Instant.parse("2025-01-01T00:00:00Z")
        val updatedAt = Instant.parse("2025-01-01T00:00:00Z")

        val auth1 = Auth.reconstruct(id1, email, hashedPassword, UserType.MEMBER, createdAt, updatedAt)
        val auth2 = Auth.reconstruct(id2, email, hashedPassword, UserType.MEMBER, createdAt, updatedAt)

        assertNotEquals(auth1, auth2)
    }

    @Test
    fun `should be equal to itself`() {
        val auth = Auth.create(AuthEmail.of("test@example.com"), RawPassword.of("MyP@ssw0rd!"), UserType.MEMBER)

        assertEquals(auth, auth)
    }

    @Test
    fun `should not be equal to null`() {
        val auth = Auth.create(AuthEmail.of("test@example.com"), RawPassword.of("MyP@ssw0rd!"), UserType.MEMBER)

        assertNotEquals(auth, null)
    }

    @Test
    fun `should not be equal to different type`() {
        val auth = Auth.create(AuthEmail.of("test@example.com"), RawPassword.of("MyP@ssw0rd!"), UserType.MEMBER)

        assertNotEquals(auth, "string")
    }

    @Test
    fun `should have same hashCode when ids are equal`() {
        val id = AuthId.from(UUID.randomUUID())
        val email1 = AuthEmail.of("user1@example.com")
        val email2 = AuthEmail.of("user2@example.com")
        val hashedPassword1 = HashedPassword.fromRaw(RawPassword.of("Pass1234!"))
        val hashedPassword2 = HashedPassword.fromRaw(RawPassword.of("Pass5678!"))
        val createdAt = Instant.parse("2025-01-01T00:00:00Z")
        val updatedAt = Instant.parse("2025-01-01T00:00:00Z")

        val auth1 = Auth.reconstruct(id, email1, hashedPassword1, UserType.MEMBER, createdAt, updatedAt)
        val auth2 = Auth.reconstruct(id, email2, hashedPassword2, UserType.OWNER, createdAt, updatedAt)

        assertEquals(auth1.hashCode(), auth2.hashCode())
    }

    @Test
    fun `should have different hashCode when ids are different`() {
        val id1 = AuthId.from(UUID.randomUUID())
        val id2 = AuthId.from(UUID.randomUUID())
        val email = AuthEmail.of("test@example.com")
        val hashedPassword = HashedPassword.fromRaw(RawPassword.of("MyP@ssw0rd!"))
        val createdAt = Instant.parse("2025-01-01T00:00:00Z")
        val updatedAt = Instant.parse("2025-01-01T00:00:00Z")

        val auth1 = Auth.reconstruct(id1, email, hashedPassword, UserType.MEMBER, createdAt, updatedAt)
        val auth2 = Auth.reconstruct(id2, email, hashedPassword, UserType.MEMBER, createdAt, updatedAt)

        assertNotEquals(auth1.hashCode(), auth2.hashCode())
    }

    @Test
    fun `should generate unique ids for different auths created`() {
        val email = AuthEmail.of("test@example.com")
        val rawPassword = RawPassword.of("MyP@ssw0rd!")

        val auth1 = Auth.create(email, rawPassword, UserType.MEMBER)
        val auth2 = Auth.create(email, rawPassword, UserType.MEMBER)

        assertNotEquals(auth1.id, auth2.id)
        assertNotEquals(auth1, auth2)
    }

    @Test
    fun `should create Auth with epoch timestamp using fixed clock`() {
        val email = AuthEmail.of("test@example.com")
        val rawPassword = RawPassword.of("MyP@ssw0rd!")
        val fixedClock = Clock.fixed(Instant.EPOCH, ZoneId.of("UTC"))

        val auth = Auth.create(email, rawPassword, UserType.MEMBER, fixedClock)

        assertEquals(Instant.EPOCH, auth.createdAt)
        assertEquals(Instant.EPOCH, auth.updatedAt)
    }

    @Test
    fun `should create Auth with far future timestamp using fixed clock`() {
        val email = AuthEmail.of("test@example.com")
        val rawPassword = RawPassword.of("MyP@ssw0rd!")
        val futureInstant = Instant.parse("2099-12-31T23:59:59Z")
        val fixedClock = Clock.fixed(futureInstant, ZoneId.of("UTC"))

        val auth = Auth.create(email, rawPassword, UserType.MEMBER, fixedClock)

        assertEquals(futureInstant, auth.createdAt)
        assertEquals(futureInstant, auth.updatedAt)
    }

    @Test
    fun `should create multiple auths with different user types`() {
        val memberAuth = Auth.create(AuthEmail.of("member@example.com"), RawPassword.of("Member123!"), UserType.MEMBER)
        val ownerAuth = Auth.create(AuthEmail.of("owner@example.com"), RawPassword.of("Owner456!"), UserType.OWNER)

        assertNotEquals(memberAuth.id, ownerAuth.id)
        assertNotEquals(memberAuth.email, ownerAuth.email)
        assertNotEquals(memberAuth.userType, ownerAuth.userType)
    }

    @Test
    fun `should maintain id consistency across multiple operations`() {
        val id = AuthId.from(UUID.randomUUID())
        val email = AuthEmail.of("test@example.com")
        val hashedPassword = HashedPassword.fromRaw(RawPassword.of("MyP@ssw0rd!"))
        val createdAt = Instant.parse("2025-01-01T00:00:00Z")
        val updatedAt = Instant.parse("2025-01-01T00:00:00Z")

        val auth = Auth.reconstruct(id, email, hashedPassword, UserType.MEMBER, createdAt, updatedAt)

        assertEquals(id, auth.id)
        assertEquals(id.hashCode(), auth.id.hashCode())
    }

    @Test
    fun `should verify password after creation`() {
        val email = AuthEmail.of("test@example.com")
        val rawPassword = RawPassword.of("MyP@ssw0rd!")

        val auth = Auth.create(email, rawPassword, UserType.MEMBER)

        assertTrue(auth.hashedPassword.matches(rawPassword))
    }

    @Test
    fun `should verify password after reconstruction`() {
        val rawPassword = RawPassword.of("MyP@ssw0rd!")
        val id = AuthId.from(UUID.randomUUID())
        val email = AuthEmail.of("test@example.com")
        val hashedPassword = HashedPassword.fromRaw(rawPassword)
        val createdAt = Instant.now()
        val updatedAt = Instant.now()

        val auth = Auth.reconstruct(id, email, hashedPassword, UserType.MEMBER, createdAt, updatedAt)

        assertTrue(auth.hashedPassword.matches(rawPassword))
    }
}
