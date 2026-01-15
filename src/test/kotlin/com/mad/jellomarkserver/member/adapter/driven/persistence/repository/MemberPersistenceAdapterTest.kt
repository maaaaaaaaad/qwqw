package com.mad.jellomarkserver.member.adapter.driven.persistence.repository

import com.mad.jellomarkserver.common.persistence.ConstraintViolationTranslator
import com.mad.jellomarkserver.common.persistence.ConstraintViolationTranslatorImpl
import com.mad.jellomarkserver.member.adapter.driven.persistence.entity.MemberJpaEntity
import com.mad.jellomarkserver.member.adapter.driven.persistence.mapper.MemberMapper
import com.mad.jellomarkserver.member.core.domain.exception.DuplicateMemberNicknameException
import com.mad.jellomarkserver.member.core.domain.exception.DuplicateSocialAccountException
import com.mad.jellomarkserver.member.core.domain.model.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.dao.DataIntegrityViolationException
import java.time.Instant
import java.util.*
import kotlin.test.assertFailsWith

@ExtendWith(MockitoExtension::class)
class MemberPersistenceAdapterTest {

    @Mock
    private lateinit var jpaRepository: MemberJpaRepository

    @Mock
    private lateinit var mapper: MemberMapper

    private val constraintTranslator: ConstraintViolationTranslator = ConstraintViolationTranslatorImpl()

    private lateinit var adapter: MemberPersistenceAdapter

    @org.junit.jupiter.api.BeforeEach
    fun setup() {
        adapter = MemberPersistenceAdapter(jpaRepository, mapper, constraintTranslator)
    }

    @Test
    fun `should save member successfully`() {
        val id = MemberId.from(UUID.randomUUID())
        val socialProvider = SocialProvider.KAKAO
        val socialId = SocialId("123456789")
        val memberNickname = MemberNickname.of("testuser")
        val displayName = MemberDisplayName.of("테스트유저")
        val createdAt = Instant.parse("2025-01-01T00:00:00Z")
        val updatedAt = Instant.parse("2025-01-01T00:00:00Z")
        val member = Member.reconstruct(id, socialProvider, socialId, memberNickname, displayName, createdAt, updatedAt)

        val entity = MemberJpaEntity(
            id = id.value,
            socialProvider = socialProvider.name,
            socialId = socialId.value,
            nickname = memberNickname.value,
            displayName = displayName.value,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        `when`(mapper.toEntity(member)).thenReturn(entity)
        `when`(jpaRepository.saveAndFlush(entity)).thenReturn(entity)
        `when`(mapper.toDomain(entity)).thenReturn(member)

        val result = adapter.save(member)

        assertEquals(member, result)
        verify(mapper).toEntity(member)
        verify(jpaRepository).saveAndFlush(entity)
        verify(mapper).toDomain(entity)
    }

    @Test
    fun `should save member with minimum valid values`() {
        val id = MemberId.from(UUID.randomUUID())
        val socialProvider = SocialProvider.KAKAO
        val socialId = SocialId("1")
        val memberNickname = MemberNickname.of("ab")
        val displayName = MemberDisplayName.of("ab")
        val createdAt = Instant.EPOCH
        val updatedAt = Instant.EPOCH
        val member = Member.reconstruct(id, socialProvider, socialId, memberNickname, displayName, createdAt, updatedAt)

        val entity = MemberJpaEntity(
            id = id.value,
            socialProvider = socialProvider.name,
            socialId = socialId.value,
            nickname = memberNickname.value,
            displayName = displayName.value,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        `when`(mapper.toEntity(member)).thenReturn(entity)
        `when`(jpaRepository.saveAndFlush(entity)).thenReturn(entity)
        `when`(mapper.toDomain(entity)).thenReturn(member)

        val result = adapter.save(member)

        assertEquals(member, result)
    }

    @Test
    fun `should save member with maximum length values`() {
        val id = MemberId.from(UUID.randomUUID())
        val socialProvider = SocialProvider.NAVER
        val socialId = SocialId("a".repeat(255))
        val memberNickname = MemberNickname.of("12345678")
        val displayName = MemberDisplayName.of("12345678")
        val createdAt = Instant.parse("2099-12-31T23:59:59Z")
        val updatedAt = Instant.parse("2099-12-31T23:59:59Z")
        val member = Member.reconstruct(id, socialProvider, socialId, memberNickname, displayName, createdAt, updatedAt)

        val entity = MemberJpaEntity(
            id = id.value,
            socialProvider = socialProvider.name,
            socialId = socialId.value,
            nickname = memberNickname.value,
            displayName = displayName.value,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        `when`(mapper.toEntity(member)).thenReturn(entity)
        `when`(jpaRepository.saveAndFlush(entity)).thenReturn(entity)
        `when`(mapper.toDomain(entity)).thenReturn(member)

        val result = adapter.save(member)

        assertEquals(member, result)
    }

    @Test
    fun `should save member with different social providers`() {
        val providers = listOf(SocialProvider.KAKAO, SocialProvider.NAVER, SocialProvider.GOOGLE)

        for (provider in providers) {
            val id = MemberId.from(UUID.randomUUID())
            val socialId = SocialId("user-${provider.name}")
            val memberNickname = MemberNickname.of("testuser")
            val displayName = MemberDisplayName.of("테스트유저")
            val createdAt = Instant.now()
            val updatedAt = Instant.now()
            val member = Member.reconstruct(id, provider, socialId, memberNickname, displayName, createdAt, updatedAt)

            val entity = MemberJpaEntity(
                id = id.value,
                socialProvider = provider.name,
                socialId = socialId.value,
                nickname = memberNickname.value,
                displayName = displayName.value,
                createdAt = createdAt,
                updatedAt = updatedAt
            )

            `when`(mapper.toEntity(member)).thenReturn(entity)
            `when`(jpaRepository.saveAndFlush(entity)).thenReturn(entity)
            `when`(mapper.toDomain(entity)).thenReturn(member)

            val result = adapter.save(member)

            assertEquals(provider, result.socialProvider)
        }
    }

    @Test
    fun `should save member with Korean nickname`() {
        val id = MemberId.from(UUID.randomUUID())
        val socialProvider = SocialProvider.KAKAO
        val socialId = SocialId("123456789")
        val memberNickname = MemberNickname.of("한글닉네임")
        val displayName = MemberDisplayName.of("한글닉네임")
        val createdAt = Instant.now()
        val updatedAt = Instant.now()
        val member = Member.reconstruct(id, socialProvider, socialId, memberNickname, displayName, createdAt, updatedAt)

        val entity = MemberJpaEntity(
            id = id.value,
            socialProvider = socialProvider.name,
            socialId = socialId.value,
            nickname = memberNickname.value,
            displayName = displayName.value,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        `when`(mapper.toEntity(member)).thenReturn(entity)
        `when`(jpaRepository.saveAndFlush(entity)).thenReturn(entity)
        `when`(mapper.toDomain(entity)).thenReturn(member)

        val result = adapter.save(member)

        assertEquals(member, result)
    }

    @Test
    fun `should save member with different created and updated timestamps`() {
        val id = MemberId.from(UUID.randomUUID())
        val socialProvider = SocialProvider.KAKAO
        val socialId = SocialId("123456789")
        val memberNickname = MemberNickname.of("testuser")
        val displayName = MemberDisplayName.of("테스트유저")
        val createdAt = Instant.parse("2025-01-01T00:00:00Z")
        val updatedAt = Instant.parse("2025-06-01T12:30:45Z")
        val member = Member.reconstruct(id, socialProvider, socialId, memberNickname, displayName, createdAt, updatedAt)

        val entity = MemberJpaEntity(
            id = id.value,
            socialProvider = socialProvider.name,
            socialId = socialId.value,
            nickname = memberNickname.value,
            displayName = displayName.value,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        `when`(mapper.toEntity(member)).thenReturn(entity)
        `when`(jpaRepository.saveAndFlush(entity)).thenReturn(entity)
        `when`(mapper.toDomain(entity)).thenReturn(member)

        val result = adapter.save(member)

        assertEquals(member, result)
    }

    @Test
    fun `should correctly handle round-trip with mapper`() {
        val id = MemberId.from(UUID.randomUUID())
        val socialProvider = SocialProvider.KAKAO
        val socialId = SocialId("rtripmap-123")
        val memberNickname = MemberNickname.of("rtripmap")
        val displayName = MemberDisplayName.of("rtripmap")
        val createdAt = Instant.parse("2025-01-01T00:00:00Z")
        val updatedAt = Instant.parse("2025-01-01T00:00:00Z")
        val originalMember =
            Member.reconstruct(id, socialProvider, socialId, memberNickname, displayName, createdAt, updatedAt)

        val entity = MemberJpaEntity(
            id = id.value,
            socialProvider = socialProvider.name,
            socialId = socialId.value,
            nickname = memberNickname.value,
            displayName = displayName.value,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        val reconstructedMember = Member.reconstruct(
            MemberId.from(entity.id),
            SocialProvider.valueOf(entity.socialProvider),
            SocialId(entity.socialId),
            MemberNickname.of(entity.nickname),
            MemberDisplayName.of(entity.nickname),
            entity.createdAt,
            entity.updatedAt
        )

        `when`(mapper.toEntity(originalMember)).thenReturn(entity)
        `when`(jpaRepository.saveAndFlush(entity)).thenReturn(entity)
        `when`(mapper.toDomain(entity)).thenReturn(reconstructedMember)

        val result = adapter.save(originalMember)

        assertEquals(originalMember.id, result.id)
        assertEquals(originalMember.socialProvider, result.socialProvider)
        assertEquals(originalMember.socialId, result.socialId)
        assertEquals(originalMember.memberNickname, result.memberNickname)
        assertEquals(originalMember.createdAt, result.createdAt)
        assertEquals(originalMember.updatedAt, result.updatedAt)
    }

    @Test
    fun `should save multiple members with different values`() {
        val member1 = Member.reconstruct(
            MemberId.from(UUID.randomUUID()),
            SocialProvider.KAKAO,
            SocialId("kakao-user-1"),
            MemberNickname.of("user1"),
            MemberDisplayName.of("유저1"),
            Instant.parse("2025-01-01T00:00:00Z"),
            Instant.parse("2025-01-01T00:00:00Z")
        )

        val member2 = Member.reconstruct(
            MemberId.from(UUID.randomUUID()),
            SocialProvider.NAVER,
            SocialId("naver-user-2"),
            MemberNickname.of("user2"),
            MemberDisplayName.of("유저2"),
            Instant.parse("2025-02-01T00:00:00Z"),
            Instant.parse("2025-02-01T00:00:00Z")
        )

        val entity1 = MemberJpaEntity(
            id = member1.id.value,
            socialProvider = member1.socialProvider.name,
            socialId = member1.socialId.value,
            nickname = member1.memberNickname.value,
            displayName = member1.displayName.value,
            createdAt = member1.createdAt,
            updatedAt = member1.updatedAt
        )

        val entity2 = MemberJpaEntity(
            id = member2.id.value,
            socialProvider = member2.socialProvider.name,
            socialId = member2.socialId.value,
            nickname = member2.memberNickname.value,
            displayName = member2.displayName.value,
            createdAt = member2.createdAt,
            updatedAt = member2.updatedAt
        )

        `when`(mapper.toEntity(member1)).thenReturn(entity1)
        `when`(jpaRepository.saveAndFlush(entity1)).thenReturn(entity1)
        `when`(mapper.toDomain(entity1)).thenReturn(member1)

        `when`(mapper.toEntity(member2)).thenReturn(entity2)
        `when`(jpaRepository.saveAndFlush(entity2)).thenReturn(entity2)
        `when`(mapper.toDomain(entity2)).thenReturn(member2)

        val result1 = adapter.save(member1)
        val result2 = adapter.save(member2)

        assertEquals(member1, result1)
        assertEquals(member2, result2)
    }

    @Test
    fun `should save member with all zeros UUID`() {
        val id = MemberId.from(UUID(0, 0))
        val socialProvider = SocialProvider.KAKAO
        val socialId = SocialId("123456789")
        val memberNickname = MemberNickname.of("testuser")
        val displayName = MemberDisplayName.of("테스트유저")
        val createdAt = Instant.now()
        val updatedAt = Instant.now()
        val member = Member.reconstruct(id, socialProvider, socialId, memberNickname, displayName, createdAt, updatedAt)

        val entity = MemberJpaEntity(
            id = id.value,
            socialProvider = socialProvider.name,
            socialId = socialId.value,
            nickname = memberNickname.value,
            displayName = displayName.value,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        `when`(mapper.toEntity(member)).thenReturn(entity)
        `when`(jpaRepository.saveAndFlush(entity)).thenReturn(entity)
        `when`(mapper.toDomain(entity)).thenReturn(member)

        val result = adapter.save(member)

        assertEquals(member, result)
    }

    @Test
    fun `should save member with high precision timestamp`() {
        val id = MemberId.from(UUID.randomUUID())
        val socialProvider = SocialProvider.KAKAO
        val socialId = SocialId("123456789")
        val memberNickname = MemberNickname.of("testuser")
        val displayName = MemberDisplayName.of("테스트유저")
        val createdAt = Instant.parse("2025-01-01T12:34:56.123456789Z")
        val updatedAt = Instant.parse("2025-01-01T12:34:56.987654321Z")
        val member = Member.reconstruct(id, socialProvider, socialId, memberNickname, displayName, createdAt, updatedAt)

        val entity = MemberJpaEntity(
            id = id.value,
            socialProvider = socialProvider.name,
            socialId = socialId.value,
            nickname = memberNickname.value,
            displayName = displayName.value,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        `when`(mapper.toEntity(member)).thenReturn(entity)
        `when`(jpaRepository.saveAndFlush(entity)).thenReturn(entity)
        `when`(mapper.toDomain(entity)).thenReturn(member)

        val result = adapter.save(member)

        assertEquals(member, result)
    }

    @Test
    fun `should throw DuplicateSocialAccountException when social constraint is violated`() {
        val id = MemberId.from(UUID.randomUUID())
        val socialProvider = SocialProvider.KAKAO
        val socialId = SocialId("duplicate-social-id")
        val memberNickname = MemberNickname.of("testuser")
        val displayName = MemberDisplayName.of("테스트유저")
        val createdAt = Instant.now()
        val updatedAt = Instant.now()
        val member = Member.reconstruct(id, socialProvider, socialId, memberNickname, displayName, createdAt, updatedAt)

        val entity = MemberJpaEntity(
            id = id.value,
            socialProvider = socialProvider.name,
            socialId = socialId.value,
            nickname = memberNickname.value,
            displayName = displayName.value,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        val exception = DataIntegrityViolationException("uk_members_social")

        `when`(mapper.toEntity(member)).thenReturn(entity)
        `when`(jpaRepository.saveAndFlush(entity)).thenThrow(exception)

        val thrownException = assertFailsWith<DuplicateSocialAccountException> {
            adapter.save(member)
        }

        assertEquals("Social account already exists: KAKAO:duplicate-social-id", thrownException.message)
        verify(mapper).toEntity(member)
        verify(jpaRepository).saveAndFlush(entity)
    }

    @Test
    fun `should throw DuplicateNicknameException when nickname constraint is violated`() {
        val id = MemberId.from(UUID.randomUUID())
        val socialProvider = SocialProvider.KAKAO
        val socialId = SocialId("123456789")
        val memberNickname = MemberNickname.of("dupname")
        val displayName = MemberDisplayName.of("dupname")
        val createdAt = Instant.now()
        val updatedAt = Instant.now()
        val member = Member.reconstruct(id, socialProvider, socialId, memberNickname, displayName, createdAt, updatedAt)

        val entity = MemberJpaEntity(
            id = id.value,
            socialProvider = socialProvider.name,
            socialId = socialId.value,
            nickname = memberNickname.value,
            displayName = displayName.value,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        val exception = DataIntegrityViolationException("uk_members_nickname")

        `when`(mapper.toEntity(member)).thenReturn(entity)
        `when`(jpaRepository.saveAndFlush(entity)).thenThrow(exception)

        val thrownException = assertFailsWith<DuplicateMemberNicknameException> {
            adapter.save(member)
        }

        assertEquals("Nickname already in use: dupname", thrownException.message)
        verify(mapper).toEntity(member)
        verify(jpaRepository).saveAndFlush(entity)
    }

    @Test
    fun `should throw DuplicateNicknameException with correct nickname value`() {
        val id = MemberId.from(UUID.randomUUID())
        val socialProvider = SocialProvider.KAKAO
        val socialId = SocialId("123456789")
        val memberNickname = MemberNickname.of("admin123")
        val displayName = MemberDisplayName.of("admin123")
        val createdAt = Instant.EPOCH
        val updatedAt = Instant.EPOCH
        val member = Member.reconstruct(id, socialProvider, socialId, memberNickname, displayName, createdAt, updatedAt)

        val entity = MemberJpaEntity(
            id = id.value,
            socialProvider = socialProvider.name,
            socialId = socialId.value,
            nickname = memberNickname.value,
            displayName = displayName.value,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        val exception = DataIntegrityViolationException("uk_members_nickname")

        `when`(mapper.toEntity(member)).thenReturn(entity)
        `when`(jpaRepository.saveAndFlush(entity)).thenThrow(exception)

        val thrownException = assertFailsWith<DuplicateMemberNicknameException> {
            adapter.save(member)
        }

        assertEquals("Nickname already in use: admin123", thrownException.message)
    }

    @Test
    fun `should throw DuplicateSocialAccountException with correct social account value`() {
        val id = MemberId.from(UUID.randomUUID())
        val socialProvider = SocialProvider.NAVER
        val socialId = SocialId("naver-user-123")
        val memberNickname = MemberNickname.of("testuser")
        val displayName = MemberDisplayName.of("테스트유저")
        val createdAt = Instant.EPOCH
        val updatedAt = Instant.EPOCH
        val member = Member.reconstruct(id, socialProvider, socialId, memberNickname, displayName, createdAt, updatedAt)

        val entity = MemberJpaEntity(
            id = id.value,
            socialProvider = socialProvider.name,
            socialId = socialId.value,
            nickname = memberNickname.value,
            displayName = displayName.value,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        val exception = DataIntegrityViolationException("uk_members_social")

        `when`(mapper.toEntity(member)).thenReturn(entity)
        `when`(jpaRepository.saveAndFlush(entity)).thenThrow(exception)

        val thrownException = assertFailsWith<DuplicateSocialAccountException> {
            adapter.save(member)
        }

        assertEquals("Social account already exists: NAVER:naver-user-123", thrownException.message)
    }

    @Test
    fun `should call constraintTranslator when DataIntegrityViolationException occurs`() {
        val id = MemberId.from(UUID.randomUUID())
        val socialProvider = SocialProvider.KAKAO
        val socialId = SocialId("123456789")
        val memberNickname = MemberNickname.of("testuser")
        val displayName = MemberDisplayName.of("테스트유저")
        val createdAt = Instant.now()
        val updatedAt = Instant.now()
        val member = Member.reconstruct(id, socialProvider, socialId, memberNickname, displayName, createdAt, updatedAt)

        val entity = MemberJpaEntity(
            id = id.value,
            socialProvider = socialProvider.name,
            socialId = socialId.value,
            nickname = memberNickname.value,
            displayName = displayName.value,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        val exception = DataIntegrityViolationException("uk_members_social")

        `when`(mapper.toEntity(member)).thenReturn(entity)
        `when`(jpaRepository.saveAndFlush(entity)).thenThrow(exception)

        assertFailsWith<DuplicateSocialAccountException> {
            adapter.save(member)
        }
    }

    @Test
    fun `should find member by social provider and social id`() {
        val id = MemberId.from(UUID.randomUUID())
        val socialProvider = SocialProvider.KAKAO
        val socialId = SocialId("123456789")
        val memberNickname = MemberNickname.of("testuser")
        val displayName = MemberDisplayName.of("테스트유저")
        val createdAt = Instant.now()
        val updatedAt = Instant.now()
        val member = Member.reconstruct(id, socialProvider, socialId, memberNickname, displayName, createdAt, updatedAt)

        val entity = MemberJpaEntity(
            id = id.value,
            socialProvider = socialProvider.name,
            socialId = socialId.value,
            nickname = memberNickname.value,
            displayName = displayName.value,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        `when`(jpaRepository.findBySocialProviderAndSocialId(socialProvider.name, socialId.value)).thenReturn(entity)
        `when`(mapper.toDomain(entity)).thenReturn(member)

        val result = adapter.findBySocial(socialProvider, socialId)

        assertEquals(member, result)
        verify(jpaRepository).findBySocialProviderAndSocialId(socialProvider.name, socialId.value)
        verify(mapper).toDomain(entity)
    }

    @Test
    fun `should return null when member not found by social`() {
        val socialProvider = SocialProvider.KAKAO
        val socialId = SocialId("nonexistent-id")

        `when`(jpaRepository.findBySocialProviderAndSocialId(socialProvider.name, socialId.value)).thenReturn(null)

        val result = adapter.findBySocial(socialProvider, socialId)

        assertNull(result)
        verify(jpaRepository).findBySocialProviderAndSocialId(socialProvider.name, socialId.value)
    }

    @Test
    fun `should find members by ids`() {
        val id1 = MemberId.from(UUID.randomUUID())
        val id2 = MemberId.from(UUID.randomUUID())
        val createdAt = Instant.now()

        val member1 = Member.reconstruct(
            id1, SocialProvider.KAKAO, SocialId("user1"),
            MemberNickname.of("닉네임1"), MemberDisplayName.of("닉네임1"), createdAt, createdAt
        )
        val member2 = Member.reconstruct(
            id2, SocialProvider.NAVER, SocialId("user2"),
            MemberNickname.of("닉네임2"), MemberDisplayName.of("닉네임2"), createdAt, createdAt
        )

        val entity1 = MemberJpaEntity(
            id = id1.value, socialProvider = "KAKAO", socialId = "user1",
            nickname = "닉네임1", displayName = "닉네임1", createdAt = createdAt, updatedAt = createdAt
        )
        val entity2 = MemberJpaEntity(
            id = id2.value, socialProvider = "NAVER", socialId = "user2",
            nickname = "닉네임2", displayName = "닉네임2", createdAt = createdAt, updatedAt = createdAt
        )

        `when`(jpaRepository.findAllById(listOf(id1.value, id2.value))).thenReturn(listOf(entity1, entity2))
        `when`(mapper.toDomain(entity1)).thenReturn(member1)
        `when`(mapper.toDomain(entity2)).thenReturn(member2)

        val result = adapter.findByIds(listOf(id1, id2))

        assertEquals(2, result.size)
        assertEquals(member1, result[0])
        assertEquals(member2, result[1])
    }

    @Test
    fun `should return empty list when no ids provided`() {
        val result = adapter.findByIds(emptyList())

        assertEquals(0, result.size)
    }

    @Test
    fun `should return only found members when some ids do not exist`() {
        val existingId = MemberId.from(UUID.randomUUID())
        val nonExistingId = MemberId.from(UUID.randomUUID())
        val createdAt = Instant.now()

        val member = Member.reconstruct(
            existingId, SocialProvider.KAKAO, SocialId("user1"),
            MemberNickname.of("닉네임"), MemberDisplayName.of("닉네임"), createdAt, createdAt
        )

        val entity = MemberJpaEntity(
            id = existingId.value, socialProvider = "KAKAO", socialId = "user1",
            nickname = "닉네임", displayName = "닉네임", createdAt = createdAt, updatedAt = createdAt
        )

        `when`(jpaRepository.findAllById(listOf(existingId.value, nonExistingId.value))).thenReturn(listOf(entity))
        `when`(mapper.toDomain(entity)).thenReturn(member)

        val result = adapter.findByIds(listOf(existingId, nonExistingId))

        assertEquals(1, result.size)
        assertEquals(member, result[0])
    }
}
