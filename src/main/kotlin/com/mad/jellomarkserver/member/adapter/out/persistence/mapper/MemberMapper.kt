package com.mad.jellomarkserver.member.adapter.out.persistence.mapper

import com.mad.jellomarkserver.member.adapter.out.persistence.entity.MemberJpaEntity
import com.mad.jellomarkserver.member.domain.model.Member

interface MemberMapper {
    fun toEntity(domain: Member): MemberJpaEntity
    fun toDomain(entity: MemberJpaEntity): Member
}