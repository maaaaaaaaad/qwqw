package com.mad.jellomarkserver.apigateway.adapter.driving.web.response

import com.mad.jellomarkserver.owner.core.domain.model.BusinessNumber
import com.mad.jellomarkserver.owner.core.domain.model.Owner
import com.mad.jellomarkserver.owner.core.domain.model.OwnerNickname
import com.mad.jellomarkserver.owner.core.domain.model.OwnerPhoneNumber
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import java.util.*

class SignUpResponseTest {

    private val fixedClock = Clock.fixed(Instant.parse("2025-01-01T00:00:00Z"), ZoneId.of("UTC"))

    @Test
    fun `should create SignUpResponse from Owner`() {
        val owner = Owner.create(
            businessNumber = BusinessNumber.of("123456789"),
            ownerPhoneNumber = OwnerPhoneNumber.of("010-1234-5678"),
            ownerNickname = OwnerNickname.of("owner"),
            ownerEmail = com.mad.jellomarkserver.owner.core.domain.model.OwnerEmail.of("owner@example.com"),
            clock = fixedClock
        )

        val response = SignUpResponse.fromOwner(owner, "accessToken", "refreshToken")

        assertThat(response.id).isEqualTo(owner.id.value)
        assertThat(response.userType).isEqualTo("OWNER")
        assertThat(response.businessNumber).isEqualTo("123456789")
        assertThat(response.phoneNumber).isEqualTo("010-1234-5678")
        assertThat(response.nickname).isEqualTo("owner")
        assertThat(response.email).isEqualTo("owner@example.com")
        assertThat(response.createdAt).isEqualTo(owner.createdAt)
        assertThat(response.updatedAt).isEqualTo(owner.updatedAt)
        assertThat(response.accessToken).isEqualTo("accessToken")
        assertThat(response.refreshToken).isEqualTo("refreshToken")
    }

    @Test
    fun `should verify data class equality`() {
        val id = UUID.randomUUID()
        val now = Instant.now(fixedClock)

        val response1 = SignUpResponse(
            id = id,
            userType = "OWNER",
            nickname = "testowner",
            email = null,
            businessNumber = "123456789",
            phoneNumber = "010-1234-5678",
            createdAt = now,
            updatedAt = now,
            accessToken = "accessToken",
            refreshToken = "refreshToken"
        )

        val response2 = SignUpResponse(
            id = id,
            userType = "OWNER",
            nickname = "testowner",
            email = null,
            businessNumber = "123456789",
            phoneNumber = "010-1234-5678",
            createdAt = now,
            updatedAt = now,
            accessToken = "accessToken",
            refreshToken = "refreshToken"
        )

        assertThat(response1).isEqualTo(response2)
        assertThat(response1.hashCode()).isEqualTo(response2.hashCode())
    }

    @Test
    fun `should verify data class copy`() {
        val id = UUID.randomUUID()
        val now = Instant.now(fixedClock)

        val original = SignUpResponse(
            id = id,
            userType = "OWNER",
            nickname = "testowner",
            email = null,
            businessNumber = "123456789",
            phoneNumber = "010-1234-5678",
            createdAt = now,
            updatedAt = now,
            accessToken = "accessToken",
            refreshToken = "refreshToken"
        )

        val copied = original.copy(nickname = "newowner")

        assertThat(copied.id).isEqualTo(original.id)
        assertThat(copied.nickname).isEqualTo("newowner")
        assertThat(copied.businessNumber).isEqualTo(original.businessNumber)
    }
}
