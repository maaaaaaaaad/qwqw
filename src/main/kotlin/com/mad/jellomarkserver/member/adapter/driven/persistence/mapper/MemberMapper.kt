package com.mad.jellomarkserver.member.adapter.driven.persistence.mapper

import com.mad.jellomarkserver.member.adapter.driven.persistence.entity.MemberJpaEntity
import com.mad.jellomarkserver.member.core.domain.model.Member

interface MemberMapper {
    fun toEntity(domain: Member): MemberJpaEntity
    fun toDomain(entity: MemberJpaEntity): Member
}