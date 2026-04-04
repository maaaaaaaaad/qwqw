package com.mad.jellomarkserver.verification.adapter.driven.persistence

import com.mad.jellomarkserver.verification.adapter.driven.persistence.entity.VerificationCodeJpaEntity
import com.mad.jellomarkserver.verification.adapter.driven.persistence.entity.VerificationTypeJpa
import com.mad.jellomarkserver.verification.adapter.driven.persistence.repository.VerificationCodeJpaRepository
import com.mad.jellomarkserver.verification.core.domain.model.VerificationCode
import com.mad.jellomarkserver.verification.core.domain.model.VerificationType
import com.mad.jellomarkserver.verification.port.driven.VerificationCodePort
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class VerificationCodePersistenceAdapter(
    private val repository: VerificationCodeJpaRepository
) : VerificationCodePort {

    override fun save(verificationCode: VerificationCode): VerificationCode {
        val entity = toEntity(verificationCode)
        val saved = repository.save(entity)
        return toDomain(saved)
    }

    override fun findLatestByTargetAndType(target: String, type: VerificationType): VerificationCode? {
        return repository.findFirstByTargetAndTypeOrderByCreatedAtDesc(target, toJpaType(type))
            ?.let { toDomain(it) }
    }

    override fun countRecentByTarget(target: String, withinMinutes: Long): Long {
        val after = Instant.now().minusSeconds(withinMinutes * 60)
        return repository.countByTargetAndCreatedAtAfter(target, after)
    }

    private fun toEntity(domain: VerificationCode) = VerificationCodeJpaEntity(
        id = domain.id,
        target = domain.target,
        code = domain.code,
        type = toJpaType(domain.type),
        expiresAt = domain.expiresAt,
        verified = domain.verified,
        createdAt = domain.createdAt
    )

    private fun toDomain(entity: VerificationCodeJpaEntity) = VerificationCode.reconstitute(
        id = entity.id,
        target = entity.target,
        code = entity.code,
        type = toDomainType(entity.type),
        expiresAt = entity.expiresAt,
        verified = entity.verified,
        createdAt = entity.createdAt
    )

    private fun toJpaType(type: VerificationType) = VerificationTypeJpa.valueOf(type.name)

    private fun toDomainType(type: VerificationTypeJpa) = VerificationType.valueOf(type.name)
}
