package com.mad.jellomarkserver.member.core.application

import com.mad.jellomarkserver.member.core.domain.exception.MemberNotFoundException
import com.mad.jellomarkserver.member.core.domain.model.*
import com.mad.jellomarkserver.member.port.driven.MemberPort
import com.mad.jellomarkserver.member.port.driving.GetCurrentMemberCommand
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever

@ExtendWith(MockitoExtension::class)
class GetCurrentMemberUseCaseImplTest {

    @Mock
    private lateinit var memberPort: MemberPort

    private lateinit var useCase: GetCurrentMemberUseCaseImpl

    @BeforeEach
    fun setup() {
        useCase = GetCurrentMemberUseCaseImpl(memberPort)
    }

    @Test
    fun `should return member when found by social provider and id`() {
        val member = Member.create(
            socialProvider = SocialProvider.KAKAO,
            socialId = SocialId("12345"),
            memberNickname = MemberNickname.of("testuser"),
            displayName = MemberDisplayName.of("테스트유저")
        )
        whenever(memberPort.findBySocial(any(), any())).thenReturn(member)

        val command = GetCurrentMemberCommand(socialProvider = "KAKAO", socialId = "12345")
        val result = useCase.execute(command)

        assertEquals(member.id, result.id)
        assertEquals(SocialProvider.KAKAO, result.socialProvider)
        assertEquals("12345", result.socialId.value)
    }

    @Test
    fun `should throw MemberNotFoundException when member not found`() {
        whenever(memberPort.findBySocial(any(), any())).thenReturn(null)

        val command = GetCurrentMemberCommand(socialProvider = "KAKAO", socialId = "nonexistent")

        val exception = assertThrows(MemberNotFoundException::class.java) {
            useCase.execute(command)
        }
        assertTrue(exception.message!!.contains("KAKAO:nonexistent"))
    }
}
