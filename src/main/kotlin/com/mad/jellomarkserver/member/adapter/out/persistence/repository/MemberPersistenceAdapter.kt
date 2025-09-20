package com.mad.jellomarkserver.member.adapter.out.persistence.repository

import com.mad.jellomarkserver.member.adapter.out.persistence.mapper.MemberMapper
import com.mad.jellomarkserver.member.core.domain.model.BusinessRegistrationNumber
import com.mad.jellomarkserver.member.core.domain.model.Email
import com.mad.jellomarkserver.member.core.domain.model.Member
import com.mad.jellomarkserver.member.core.domain.model.Nickname
import com.mad.jellomarkserver.member.port.driven.MemberPort
import org.springframework.stereotype.Component

@Component
class MemberPersistenceAdapter(
    private val jpaRepository: MemberJpaRepository,
    private val mapper: MemberMapper
) : MemberPort {
    override fun existsByEmail(email: Email): Boolean {
        return jpaRepository.existsByEmail(email.value)
    }

    override fun existsByBusinessRegistrationNumber(businessRegistrationNumber: BusinessRegistrationNumber): Boolean {
        return jpaRepository.existsByBusinessRegistrationNumber(businessRegistrationNumber.value)
    }

    override fun existsByNickname(nickname: Nickname): Boolean {
        return jpaRepository.existsByNickname(nickname.value)
    }

    override fun save(member: Member): Member {
        val entity = mapper.toEntity(member)
        val saved = jpaRepository.save(entity)
        return mapper.toDomain(saved)
    }
}