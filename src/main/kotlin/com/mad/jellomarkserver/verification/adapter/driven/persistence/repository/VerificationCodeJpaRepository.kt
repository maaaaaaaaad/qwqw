package com.mad.jellomarkserver.verification.adapter.driven.persistence.repository

import com.mad.jellomarkserver.verification.adapter.driven.persistence.entity.VerificationCodeJpaEntity
import com.mad.jellomarkserver.verification.adapter.driven.persistence.entity.VerificationTypeJpa
import org.springframework.data.jpa.repository.JpaRepository
import java.time.Instant
import java.util.UUID

interface VerificationCodeJpaRepository : JpaRepository<VerificationCodeJpaEntity, UUID> {

    fun findFirstByTargetAndTypeOrderByCreatedAtDesc(
        target: String,
        type: VerificationTypeJpa
    ): VerificationCodeJpaEntity?

    fun countByTargetAndCreatedAtAfter(
        target: String,
        after: Instant
    ): Long
}
