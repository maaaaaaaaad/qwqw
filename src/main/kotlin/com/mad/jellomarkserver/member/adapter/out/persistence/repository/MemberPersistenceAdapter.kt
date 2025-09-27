package com.mad.jellomarkserver.member.adapter.out.persistence.repository

import com.mad.jellomarkserver.common.persistence.ConstraintViolationTranslator
import com.mad.jellomarkserver.member.adapter.out.persistence.mapper.MemberMapper
import com.mad.jellomarkserver.member.core.domain.exception.DuplicateEmailException
import com.mad.jellomarkserver.member.core.domain.exception.DuplicateNicknameException
import com.mad.jellomarkserver.member.core.domain.model.Member
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
                    "uk_members_email" to { DuplicateEmailException(member.email.value) },
                    "uk_members_nickname" to { DuplicateNicknameException(member.nickname.value) },
                )
            )
        }
    }
}