package com.mad.jellomarkserver.member.adapter.driven.persistence.repository

import com.mad.jellomarkserver.common.persistence.ConstraintViolationTranslator
import com.mad.jellomarkserver.member.adapter.driven.persistence.mapper.MemberMapper
import com.mad.jellomarkserver.member.core.domain.exception.DuplicateMemberNicknameException
import com.mad.jellomarkserver.member.core.domain.exception.DuplicateSocialAccountException
import com.mad.jellomarkserver.member.core.domain.model.Member
import com.mad.jellomarkserver.member.core.domain.model.SocialId
import com.mad.jellomarkserver.member.core.domain.model.SocialProvider
import com.mad.jellomarkserver.member.port.driven.MemberPort
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Component

@Component
class MemberPersistenceAdapter(
    private val jpaRepository: MemberJpaRepository,
    private val mapper: MemberMapper,
    private val constraintTranslator: ConstraintViolationTranslator
) : MemberPort {
    override fun save(member: Member): Member {
        try {
            val entity = mapper.toEntity(member)
            val saved = jpaRepository.saveAndFlush(entity)
            return mapper.toDomain(saved)
        } catch (e: DataIntegrityViolationException) {
            constraintTranslator.translateAndThrow(
                e, mapOf(
                    "uk_members_social" to {
                        DuplicateSocialAccountException(
                            member.socialProvider.name,
                            member.socialId.value
                        )
                    },
                    "uk_members_nickname" to { DuplicateMemberNicknameException(member.memberNickname.value) },
                )
            )
        }
    }

    override fun findBySocial(provider: SocialProvider, socialId: SocialId): Member? {
        return jpaRepository.findBySocialProviderAndSocialId(provider.name, socialId.value)
            ?.let { mapper.toDomain(it) }
    }
}
