package com.mad.jellomarkserver.member.adapter.driving.web

import com.mad.jellomarkserver.member.adapter.driving.web.request.MemberSignUpRequest
import com.mad.jellomarkserver.member.core.domain.exception.InvalidMemberEmailException
import com.mad.jellomarkserver.member.core.domain.model.Member
import com.mad.jellomarkserver.member.core.domain.model.MemberEmail
import com.mad.jellomarkserver.member.core.domain.model.MemberId
import com.mad.jellomarkserver.member.core.domain.model.MemberNickname
import com.mad.jellomarkserver.member.port.driving.SignUpMemberUseCase
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.Instant
import java.util.*
import kotlin.test.assertFailsWith

class MemberSignUpControllerTest {

    @Test
    fun `should return member response when member signs up successfully`() {
        val memberId = MemberId.from(UUID.randomUUID())
        val createdAt = Instant.parse("2025-01-01T00:00:00Z")
        val member = Member.reconstruct(
            id = memberId,
            memberNickname = MemberNickname.of("testuser"),
            memberEmail = MemberEmail.of("test@example.com"),
            createdAt = createdAt,
            updatedAt = createdAt
        )

        val useCase = SignUpMemberUseCase { member }
        val controller = MemberSignUpController(useCase)

        val request = MemberSignUpRequest(
            nickname = "testuser",
            email = "test@example.com"
        )

        val response = controller.signUp(request)

        assertEquals(memberId.value, response.id)
        assertEquals("testuser", response.nickname)
        assertEquals("test@example.com", response.email)
        assertEquals(createdAt, response.createdAt)
        assertEquals(createdAt, response.updatedAt)
    }

    @Test
    fun `should throw InvalidMemberEmailException when email is invalid`() {
        val useCase = SignUpMemberUseCase { throw InvalidMemberEmailException("invalid-email") }
        val controller = MemberSignUpController(useCase)

        val request = MemberSignUpRequest(
            nickname = "testuser",
            email = "invalid-email"
        )

        assertFailsWith<InvalidMemberEmailException> {
            controller.signUp(request)
        }
    }
}
