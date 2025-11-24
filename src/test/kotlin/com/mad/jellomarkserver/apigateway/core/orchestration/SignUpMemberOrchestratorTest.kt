package com.mad.jellomarkserver.apigateway.core.orchestration

import com.mad.jellomarkserver.apigateway.adapter.driving.web.request.SignUpMemberRequest
import com.mad.jellomarkserver.apigateway.port.driving.SignUpMemberOrchestrator
import com.mad.jellomarkserver.member.core.domain.exception.DuplicateMemberEmailException
import com.mad.jellomarkserver.member.core.domain.exception.DuplicateMemberNicknameException
import com.mad.jellomarkserver.member.core.domain.exception.InvalidMemberEmailException
import com.mad.jellomarkserver.member.core.domain.exception.InvalidMemberNicknameException
import com.mad.jellomarkserver.member.core.domain.model.Member
import com.mad.jellomarkserver.member.core.domain.model.MemberEmail
import com.mad.jellomarkserver.member.core.domain.model.MemberNickname
import com.mad.jellomarkserver.member.port.driving.SignUpMemberCommand
import com.mad.jellomarkserver.member.port.driving.SignUpMemberUseCase
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import kotlin.test.assertFailsWith

@ExtendWith(MockitoExtension::class)
class SignUpMemberOrchestratorTest {

    @Mock
    private lateinit var signUpMemberUseCase: SignUpMemberUseCase

    private lateinit var orchestrator: SignUpMemberOrchestrator

    private val fixedClock = Clock.fixed(Instant.parse("2025-01-01T00:00:00Z"), ZoneId.of("UTC"))

    @BeforeEach
    fun setup() {
        orchestrator = SignUpMemberOrchestratorImpl(signUpMemberUseCase)
    }

    @Test
    fun `should orchestrate member sign up successfully`() {
        val request = SignUpMemberRequest(
            nickname = "testuser",
            email = "test@example.com"
        )

        val member = Member.create(
            memberNickname = MemberNickname.of("testuser"),
            memberEmail = MemberEmail.of("test@example.com"),
            clock = fixedClock
        )

        `when`(
            signUpMemberUseCase.signUp(
                ArgumentMatchers.any() ?: SignUpMemberCommand("testuser", "test@example.com")
            )
        ).thenReturn(member)

        val response = orchestrator.signUp(request)

        assertThat(response.id).isEqualTo(member.id.value)
        assertThat(response.userType).isEqualTo("MEMBER")
        assertThat(response.nickname).isEqualTo("testuser")
        assertThat(response.email).isEqualTo("test@example.com")
        assertThat(response.businessNumber).isNull()
        assertThat(response.phoneNumber).isNull()
    }

    @Test
    fun `should throw InvalidMemberEmailException when email is invalid`() {
        val request = SignUpMemberRequest(
            nickname = "testuser",
            email = "invalid-email"
        )

        `when`(
            signUpMemberUseCase.signUp(
                ArgumentMatchers.any() ?: SignUpMemberCommand("testuser", "invalid-email")
            )
        ).thenThrow(InvalidMemberEmailException("invalid-email"))

        val exception = assertFailsWith<InvalidMemberEmailException> {
            orchestrator.signUp(request)
        }
        assertThat(exception.message).contains("invalid-email")
    }

    @Test
    fun `should throw DuplicateMemberEmailException when email is duplicate`() {
        val request = SignUpMemberRequest(
            nickname = "testuser",
            email = "duplicate@example.com"
        )

        `when`(
            signUpMemberUseCase.signUp(
                ArgumentMatchers.any() ?: SignUpMemberCommand("testuser", "duplicate@example.com")
            )
        ).thenThrow(DuplicateMemberEmailException("duplicate@example.com"))

        val exception = assertFailsWith<DuplicateMemberEmailException> {
            orchestrator.signUp(request)
        }
        assertThat(exception.message).contains("duplicate@example.com")
    }

    @Test
    fun `should throw InvalidMemberNicknameException when nickname is invalid`() {
        val request = SignUpMemberRequest(
            nickname = "a",
            email = "test@example.com"
        )

        `when`(
            signUpMemberUseCase.signUp(
                ArgumentMatchers.any() ?: SignUpMemberCommand("a", "test@example.com")
            )
        ).thenThrow(InvalidMemberNicknameException("a"))

        val exception = assertFailsWith<InvalidMemberNicknameException> {
            orchestrator.signUp(request)
        }
        assertThat(exception.message).contains("a")
    }

    @Test
    fun `should throw DuplicateMemberNicknameException when nickname is duplicate`() {
        val request = SignUpMemberRequest(
            nickname = "duplicated",
            email = "test@example.com"
        )

        `when`(
            signUpMemberUseCase.signUp(
                ArgumentMatchers.any() ?: SignUpMemberCommand("duplicated", "test@example.com")
            )
        ).thenThrow(DuplicateMemberNicknameException("duplicated"))

        val exception = assertFailsWith<DuplicateMemberNicknameException> {
            orchestrator.signUp(request)
        }
        assertThat(exception.message).contains("duplicated")
    }
}
