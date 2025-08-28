package com.mad.jellomarkserver.domain.model

import com.mad.jellomarkserver.domain.model.member.Email
import com.mad.jellomarkserver.domain.model.member.Member
import com.mad.jellomarkserver.domain.model.member.MemberId
import com.mad.jellomarkserver.domain.model.member.Nickname
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class MemberTest {
    @Test
    fun `On create, createdAt equals updatedAt and follows the provided Clock`() {
        val now = Instant.parse("2024-01-01T00:00:00Z")
        val clock = Clock.fixed(now, ZoneOffset.UTC)
        val m = Member.create(Nickname.of("m1"), Email.of("a@b.co"), clock)
        assertEquals(now, m.createdAt)
        assertEquals(now, m.updatedAt)
    }

    @Test
    fun `Changing nickname updates only updatedAt and keeps createdAt`() {
        val now = Instant.parse("2024-01-01T00:00:00Z")
        val later = now.plusSeconds(60)
        val clockNow = Clock.fixed(now, ZoneOffset.UTC)
        val clockLater = Clock.fixed(later, ZoneOffset.UTC)
        val m = Member.create(Nickname.of("m1"), Email.of("a@b.co"), clockNow)
        val updated = m.changeNickname(Nickname.of("m2"), clockLater)
        assertEquals(now, updated.createdAt)
        assertEquals(later, updated.updatedAt)
        assertEquals("m2", updated.nickname.value)
        assertEquals(m.email.value, updated.email.value)
    }

    @Test
    fun `Changing email updates only updatedAt and keeps createdAt`() {
        val now = Instant.parse("2024-01-01T00:00:00Z")
        val later = now.plusSeconds(120)
        val clockNow = Clock.fixed(now, ZoneOffset.UTC)
        val clockLater = Clock.fixed(later, ZoneOffset.UTC)
        val m = Member.create(Nickname.of("m1"), Email.of("a@b.co"), clockNow)
        val updated = m.changeEmail(Email.of("c@d.com"), clockLater)
        assertEquals(now, updated.createdAt)
        assertEquals(later, updated.updatedAt)
        assertEquals("c@d.com", updated.email.value)
        assertEquals(m.nickname.value, updated.nickname.value)
    }

    @Test
    fun `Members with the same ID are equal`() {
        val id = MemberId.from(UUID.fromString("00000000-0000-0000-0000-000000000001"))
        val now = Instant.parse("2024-01-01T00:00:00Z")
        val later = now.plusSeconds(1)
        val m1 = Member.reconstruct(id, Nickname.of("aa"), Email.of("x@y.zz"), now, now)
        val m2 = Member.reconstruct(id, Nickname.of("bb"), Email.of("p@q.rr"), now, later)
        assertEquals(m1, m2)
        assertEquals(m1.hashCode(), m2.hashCode())
        assertNotEquals(m1.nickname.value, m2.nickname.value)
    }
}
