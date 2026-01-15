package com.mad.jellomarkserver.member.adapter.driven.persistence.entity

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.Instant
import java.util.*

class MemberJpaEntityTest {

    @Test
    fun `should create MemberJpaEntity with valid values`() {
        val id = UUID.randomUUID()
        val socialProvider = "KAKAO"
        val socialId = "123456789"
        val nickname = "testuser"
        val createdAt = Instant.now()
        val updatedAt = Instant.now()

        val entity = MemberJpaEntity(
            id = id,
            socialProvider = socialProvider,
            socialId = socialId,
            nickname = nickname,
            displayName = "테스트유저",
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        assertEquals(id, entity.id)
        assertEquals(socialProvider, entity.socialProvider)
        assertEquals(socialId, entity.socialId)
        assertEquals(nickname, entity.nickname)
        assertEquals(createdAt, entity.createdAt)
        assertEquals(updatedAt, entity.updatedAt)
    }

    @Test
    fun `should create MemberJpaEntity with minimum length nickname`() {
        val id = UUID.randomUUID()
        val socialProvider = "KAKAO"
        val socialId = "123"
        val nickname = "ab"
        val createdAt = Instant.EPOCH
        val updatedAt = Instant.EPOCH

        val entity = MemberJpaEntity(
            id = id,
            socialProvider = socialProvider,
            socialId = socialId,
            nickname = nickname,
            displayName = "테스트유저",
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        assertEquals(id, entity.id)
        assertEquals(socialProvider, entity.socialProvider)
        assertEquals(socialId, entity.socialId)
        assertEquals(nickname, entity.nickname)
        assertEquals(createdAt, entity.createdAt)
        assertEquals(updatedAt, entity.updatedAt)
    }

    @Test
    fun `should create MemberJpaEntity with maximum length nickname`() {
        val id = UUID.randomUUID()
        val socialProvider = "KAKAO"
        val socialId = "123456789"
        val nickname = "a".repeat(100)
        val createdAt = Instant.EPOCH
        val updatedAt = Instant.EPOCH

        val entity = MemberJpaEntity(
            id = id,
            socialProvider = socialProvider,
            socialId = socialId,
            nickname = nickname,
            displayName = "테스트유저",
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        assertEquals(id, entity.id)
        assertEquals(socialProvider, entity.socialProvider)
        assertEquals(socialId, entity.socialId)
        assertEquals(nickname, entity.nickname)
        assertEquals(createdAt, entity.createdAt)
        assertEquals(updatedAt, entity.updatedAt)
    }

    @Test
    fun `should create MemberJpaEntity with different social providers`() {
        val providers = listOf("KAKAO", "NAVER", "GOOGLE")

        for (provider in providers) {
            val entity = MemberJpaEntity(
                id = UUID.randomUUID(),
                socialProvider = provider,
                socialId = "123456789",
                nickname = "testuser",
                displayName = "테스트유저",
                createdAt = Instant.now(),
                updatedAt = Instant.now()
            )

            assertEquals(provider, entity.socialProvider)
        }
    }

    @Test
    fun `should create MemberJpaEntity with special characters in nickname`() {
        val id = UUID.randomUUID()
        val socialProvider = "KAKAO"
        val socialId = "123456789"
        val nickname = "user_123-test.name"
        val createdAt = Instant.now()
        val updatedAt = Instant.now()

        val entity = MemberJpaEntity(
            id = id,
            socialProvider = socialProvider,
            socialId = socialId,
            nickname = nickname,
            displayName = "테스트유저",
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        assertEquals(id, entity.id)
        assertEquals(socialProvider, entity.socialProvider)
        assertEquals(socialId, entity.socialId)
        assertEquals(nickname, entity.nickname)
        assertEquals(createdAt, entity.createdAt)
        assertEquals(updatedAt, entity.updatedAt)
    }

    @Test
    fun `should create MemberJpaEntity with Korean characters in nickname`() {
        val id = UUID.randomUUID()
        val socialProvider = "KAKAO"
        val socialId = "123456789"
        val nickname = "한글닉네임"
        val createdAt = Instant.now()
        val updatedAt = Instant.now()

        val entity = MemberJpaEntity(
            id = id,
            socialProvider = socialProvider,
            socialId = socialId,
            nickname = nickname,
            displayName = "한글유저",
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        assertEquals(id, entity.id)
        assertEquals(socialProvider, entity.socialProvider)
        assertEquals(socialId, entity.socialId)
        assertEquals(nickname, entity.nickname)
        assertEquals(createdAt, entity.createdAt)
        assertEquals(updatedAt, entity.updatedAt)
    }

    @Test
    fun `should create MemberJpaEntity with epoch timestamps`() {
        val id = UUID.randomUUID()
        val socialProvider = "KAKAO"
        val socialId = "123456789"
        val nickname = "testuser"
        val createdAt = Instant.EPOCH
        val updatedAt = Instant.EPOCH

        val entity = MemberJpaEntity(
            id = id,
            socialProvider = socialProvider,
            socialId = socialId,
            nickname = nickname,
            displayName = "테스트유저",
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        assertEquals(id, entity.id)
        assertEquals(socialProvider, entity.socialProvider)
        assertEquals(socialId, entity.socialId)
        assertEquals(nickname, entity.nickname)
        assertEquals(createdAt, entity.createdAt)
        assertEquals(updatedAt, entity.updatedAt)
    }

    @Test
    fun `should create MemberJpaEntity with far future timestamps`() {
        val id = UUID.randomUUID()
        val socialProvider = "NAVER"
        val socialId = "987654321"
        val nickname = "testuser"
        val createdAt = Instant.parse("2099-12-31T23:59:59Z")
        val updatedAt = Instant.parse("2099-12-31T23:59:59Z")

        val entity = MemberJpaEntity(
            id = id,
            socialProvider = socialProvider,
            socialId = socialId,
            nickname = nickname,
            displayName = "테스트유저",
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        assertEquals(id, entity.id)
        assertEquals(socialProvider, entity.socialProvider)
        assertEquals(socialId, entity.socialId)
        assertEquals(nickname, entity.nickname)
        assertEquals(createdAt, entity.createdAt)
        assertEquals(updatedAt, entity.updatedAt)
    }

    @Test
    fun `should create MemberJpaEntity with different created and updated timestamps`() {
        val id = UUID.randomUUID()
        val socialProvider = "GOOGLE"
        val socialId = "google-user-123"
        val nickname = "testuser"
        val createdAt = Instant.parse("2024-01-01T00:00:00Z")
        val updatedAt = Instant.parse("2024-12-31T23:59:59Z")

        val entity = MemberJpaEntity(
            id = id,
            socialProvider = socialProvider,
            socialId = socialId,
            nickname = nickname,
            displayName = "테스트유저",
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        assertEquals(id, entity.id)
        assertEquals(socialProvider, entity.socialProvider)
        assertEquals(socialId, entity.socialId)
        assertEquals(nickname, entity.nickname)
        assertEquals(createdAt, entity.createdAt)
        assertEquals(updatedAt, entity.updatedAt)
    }

    @Test
    fun `should update nickname field`() {
        val entity = MemberJpaEntity(
            id = UUID.randomUUID(),
            socialProvider = "KAKAO",
            socialId = "123456789",
            nickname = "oldnickname",
            displayName = "테스트유저",
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )

        val newNickname = "newnickname"
        entity.nickname = newNickname

        assertEquals(newNickname, entity.nickname)
    }

    @Test
    fun `should update socialId field`() {
        val entity = MemberJpaEntity(
            id = UUID.randomUUID(),
            socialProvider = "KAKAO",
            socialId = "old-social-id",
            nickname = "testuser",
            displayName = "테스트유저",
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )

        val newSocialId = "new-social-id"
        entity.socialId = newSocialId

        assertEquals(newSocialId, entity.socialId)
    }

    @Test
    fun `should update socialProvider field`() {
        val entity = MemberJpaEntity(
            id = UUID.randomUUID(),
            socialProvider = "KAKAO",
            socialId = "123456789",
            nickname = "testuser",
            displayName = "테스트유저",
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )

        val newProvider = "NAVER"
        entity.socialProvider = newProvider

        assertEquals(newProvider, entity.socialProvider)
    }

    @Test
    fun `should update updatedAt field`() {
        val createdAt = Instant.parse("2024-01-01T00:00:00Z")
        val initialUpdatedAt = Instant.parse("2024-01-01T00:00:00Z")

        val entity = MemberJpaEntity(
            id = UUID.randomUUID(),
            socialProvider = "KAKAO",
            socialId = "123456789",
            nickname = "testuser",
            displayName = "테스트유저",
            createdAt = createdAt,
            updatedAt = initialUpdatedAt
        )

        val newUpdatedAt = Instant.parse("2024-12-31T23:59:59Z")
        entity.updatedAt = newUpdatedAt

        assertEquals(createdAt, entity.createdAt)
        assertEquals(newUpdatedAt, entity.updatedAt)
    }

    @Test
    fun `should update id field`() {
        val oldId = UUID.randomUUID()
        val entity = MemberJpaEntity(
            id = oldId,
            socialProvider = "KAKAO",
            socialId = "123456789",
            nickname = "testuser",
            displayName = "테스트유저",
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )

        val newId = UUID.randomUUID()
        entity.id = newId

        assertEquals(newId, entity.id)
    }

    @Test
    fun `should update createdAt field`() {
        val initialCreatedAt = Instant.parse("2024-01-01T00:00:00Z")
        val entity = MemberJpaEntity(
            id = UUID.randomUUID(),
            socialProvider = "KAKAO",
            socialId = "123456789",
            nickname = "testuser",
            displayName = "테스트유저",
            createdAt = initialCreatedAt,
            updatedAt = Instant.now()
        )

        val newCreatedAt = Instant.parse("2024-06-01T00:00:00Z")
        entity.createdAt = newCreatedAt

        assertEquals(newCreatedAt, entity.createdAt)
    }

    @Test
    fun `should create MemberJpaEntity with numeric nickname`() {
        val id = UUID.randomUUID()
        val socialProvider = "KAKAO"
        val socialId = "123456789"
        val nickname = "12345678"
        val createdAt = Instant.now()
        val updatedAt = Instant.now()

        val entity = MemberJpaEntity(
            id = id,
            socialProvider = socialProvider,
            socialId = socialId,
            nickname = nickname,
            displayName = "테스트유저",
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        assertEquals(id, entity.id)
        assertEquals(socialProvider, entity.socialProvider)
        assertEquals(socialId, entity.socialId)
        assertEquals(nickname, entity.nickname)
        assertEquals(createdAt, entity.createdAt)
        assertEquals(updatedAt, entity.updatedAt)
    }

    @Test
    fun `should create MemberJpaEntity with all zeros UUID`() {
        val id = UUID.fromString("00000000-0000-0000-0000-000000000000")
        val socialProvider = "KAKAO"
        val socialId = "123456789"
        val nickname = "testuser"
        val createdAt = Instant.now()
        val updatedAt = Instant.now()

        val entity = MemberJpaEntity(
            id = id,
            socialProvider = socialProvider,
            socialId = socialId,
            nickname = nickname,
            displayName = "테스트유저",
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        assertEquals(id, entity.id)
        assertEquals(socialProvider, entity.socialProvider)
        assertEquals(socialId, entity.socialId)
        assertEquals(nickname, entity.nickname)
        assertEquals(createdAt, entity.createdAt)
        assertEquals(updatedAt, entity.updatedAt)
    }

    @Test
    fun `should create multiple MemberJpaEntity instances with different values`() {
        val entity1 = MemberJpaEntity(
            id = UUID.randomUUID(),
            socialProvider = "KAKAO",
            socialId = "kakao-user-1",
            nickname = "user1",
            displayName = "유저1",
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )

        val entity2 = MemberJpaEntity(
            id = UUID.randomUUID(),
            socialProvider = "NAVER",
            socialId = "naver-user-2",
            nickname = "user2",
            displayName = "유저2",
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )

        assertEquals("user1", entity1.nickname)
        assertEquals("KAKAO", entity1.socialProvider)
        assertEquals("kakao-user-1", entity1.socialId)
        assertEquals("user2", entity2.nickname)
        assertEquals("NAVER", entity2.socialProvider)
        assertEquals("naver-user-2", entity2.socialId)
    }

    @Test
    fun `should update all mutable fields`() {
        val entity = MemberJpaEntity(
            id = UUID.randomUUID(),
            socialProvider = "KAKAO",
            socialId = "old-social-id",
            nickname = "oldnickname",
            displayName = "테스트유저",
            createdAt = Instant.parse("2024-01-01T00:00:00Z"),
            updatedAt = Instant.parse("2024-01-01T00:00:00Z")
        )

        val newId = UUID.randomUUID()
        val newSocialProvider = "NAVER"
        val newSocialId = "new-social-id"
        val newNickname = "newnickname"
        val newCreatedAt = Instant.parse("2024-06-01T00:00:00Z")
        val newUpdatedAt = Instant.parse("2024-12-31T23:59:59Z")

        entity.id = newId
        entity.socialProvider = newSocialProvider
        entity.socialId = newSocialId
        entity.nickname = newNickname
        entity.createdAt = newCreatedAt
        entity.updatedAt = newUpdatedAt

        assertEquals(newId, entity.id)
        assertEquals(newSocialProvider, entity.socialProvider)
        assertEquals(newSocialId, entity.socialId)
        assertEquals(newNickname, entity.nickname)
        assertEquals(newCreatedAt, entity.createdAt)
        assertEquals(newUpdatedAt, entity.updatedAt)
    }

    @Test
    fun `should create MemberJpaEntity with emoji in nickname`() {
        val id = UUID.randomUUID()
        val socialProvider = "KAKAO"
        val socialId = "123456789"
        val nickname = "user😀"
        val createdAt = Instant.now()
        val updatedAt = Instant.now()

        val entity = MemberJpaEntity(
            id = id,
            socialProvider = socialProvider,
            socialId = socialId,
            nickname = nickname,
            displayName = "테스트유저",
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        assertEquals(id, entity.id)
        assertEquals(socialProvider, entity.socialProvider)
        assertEquals(socialId, entity.socialId)
        assertEquals(nickname, entity.nickname)
        assertEquals(createdAt, entity.createdAt)
        assertEquals(updatedAt, entity.updatedAt)
    }

    @Test
    fun `should create MemberJpaEntity with whitespace in nickname`() {
        val id = UUID.randomUUID()
        val socialProvider = "KAKAO"
        val socialId = "123456789"
        val nickname = "user name"
        val createdAt = Instant.now()
        val updatedAt = Instant.now()

        val entity = MemberJpaEntity(
            id = id,
            socialProvider = socialProvider,
            socialId = socialId,
            nickname = nickname,
            displayName = "테스트유저",
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        assertEquals(id, entity.id)
        assertEquals(socialProvider, entity.socialProvider)
        assertEquals(socialId, entity.socialId)
        assertEquals(nickname, entity.nickname)
        assertEquals(createdAt, entity.createdAt)
        assertEquals(updatedAt, entity.updatedAt)
    }

    @Test
    fun `should create MemberJpaEntity with timestamp precision`() {
        val id = UUID.randomUUID()
        val socialProvider = "KAKAO"
        val socialId = "123456789"
        val nickname = "testuser"
        val createdAt = Instant.parse("2024-06-15T10:30:45.123456789Z")
        val updatedAt = Instant.parse("2024-06-15T10:30:45.987654321Z")

        val entity = MemberJpaEntity(
            id = id,
            socialProvider = socialProvider,
            socialId = socialId,
            nickname = nickname,
            displayName = "테스트유저",
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        assertEquals(id, entity.id)
        assertEquals(socialProvider, entity.socialProvider)
        assertEquals(socialId, entity.socialId)
        assertEquals(nickname, entity.nickname)
        assertEquals(createdAt, entity.createdAt)
        assertEquals(updatedAt, entity.updatedAt)
    }
}
