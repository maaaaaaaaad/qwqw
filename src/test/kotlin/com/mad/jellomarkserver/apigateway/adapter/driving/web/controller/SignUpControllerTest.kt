package com.mad.jellomarkserver.apigateway.adapter.driving.web.controller

import com.mad.jellomarkserver.apigateway.adapter.driving.web.request.SignUpMemberRequest
import com.mad.jellomarkserver.apigateway.adapter.driving.web.response.SignUpResponse
import com.mad.jellomarkserver.apigateway.port.driving.SignUpMemberOrchestrator
import com.mad.jellomarkserver.member.core.domain.exception.DuplicateMemberEmailException
import com.mad.jellomarkserver.member.core.domain.exception.DuplicateMemberNicknameException
import com.mad.jellomarkserver.member.core.domain.exception.InvalidMemberEmailException
import com.mad.jellomarkserver.member.core.domain.exception.InvalidMemberNicknameException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import java.time.Instant
import java.util.*
import kotlin.test.assertFailsWith

class SignUpControllerTest {

    @Test
    fun `should sign up member successfully and return 201 CREATED`() {
        val response = SignUpResponse(
            id = UUID.randomUUID(),
            userType = "MEMBER",
            nickname = "testuser",
            email = "test@example.com",
            businessNumber = null,
            phoneNumber = null,
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )

        val orchestrator = SignUpMemberOrchestrator { response }
        val controller = SignUpController(orchestrator)

        val request = SignUpMemberRequest(
            nickname = "testuser",
            email = "test@example.com",
            password = "Password123!",
        )

        val result = controller.signUpMember(request)

        assertNotNull(result)
        assertEquals("MEMBER", result.userType)
        assertEquals("testuser", result.nickname)
        assertEquals("test@example.com", result.email)
    }

    @Test
    fun `should throw InvalidMemberEmailException when email is invalid`() {
        val orchestrator = SignUpMemberOrchestrator {
            throw InvalidMemberEmailException("invalid-email")
        }
        val controller = SignUpController(orchestrator)

        val request = SignUpMemberRequest(
            nickname = "testuser",
            email = "invalid-email",
            password = "Password123!",
        )

        assertFailsWith<InvalidMemberEmailException> {
            controller.signUpMember(request)
        }
    }

    @Test
    fun `should throw DuplicateMemberEmailException when email is duplicate`() {
        val orchestrator = SignUpMemberOrchestrator {
            throw DuplicateMemberEmailException("duplicate@example.com")
        }
        val controller = SignUpController(orchestrator)

        val request = SignUpMemberRequest(
            nickname = "testuser",
            email = "duplicate@example.com",
            password = "Password123!",
        )

        assertFailsWith<DuplicateMemberEmailException> {
            controller.signUpMember(request)
        }
    }

    @Test
    fun `should throw InvalidMemberNicknameException when nickname is invalid`() {
        val orchestrator = SignUpMemberOrchestrator {
            throw InvalidMemberNicknameException("a")
        }
        val controller = SignUpController(orchestrator)

        val request = SignUpMemberRequest(
            nickname = "a",
            email = "test@example.com",
            password = "Password123!",
        )

        assertFailsWith<InvalidMemberNicknameException> {
            controller.signUpMember(request)
        }
    }

    @Test
    fun `should throw DuplicateMemberNicknameException when nickname is duplicate`() {
        val orchestrator = SignUpMemberOrchestrator {
            throw DuplicateMemberNicknameException("duplicated")
        }
        val controller = SignUpController(orchestrator)

        val request = SignUpMemberRequest(
            nickname = "duplicated",
            email = "test@example.com",
            password = "Password123!",
        )

        assertFailsWith<DuplicateMemberNicknameException> {
            controller.signUpMember(request)
        }
    }

    @Test
    fun `should throw RuntimeException when unexpected error occurs`() {
        val orchestrator = SignUpMemberOrchestrator {
            throw RuntimeException("Unexpected error")
        }
        val controller = SignUpController(orchestrator)

        val request = SignUpMemberRequest(
            nickname = "testuser",
            email = "test@example.com",
            password = "Password123!",
        )

        assertFailsWith<RuntimeException> {
            controller.signUpMember(request)
        }
    }
}
