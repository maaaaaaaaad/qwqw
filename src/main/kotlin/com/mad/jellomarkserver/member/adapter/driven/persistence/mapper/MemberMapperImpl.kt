package com.mad.jellomarkserver.member.adapter.driven.persistence.mapper

import com.mad.jellomarkserver.member.adapter.driven.persistence.entity.MemberJpaEntity
import com.mad.jellomarkserver.member.core.domain.model.MemberEmail
import com.mad.jellomarkserver.member.core.domain.model.Member
import com.mad.jellomarkserver.member.core.domain.model.MemberId
import com.mad.jellomarkserver.member.core.domain.model.MemberNickname
import org.springframework.stereotype.Component

@Component
class MemberMapperImpl : MemberMapper {
    override fun toEntity(domain: Member): MemberJpaEntity {
        return MemberJpaEntity(
            id = domain.id.value,
            nickname = domain.memberNickname.value,
            email = domain.memberEmail.value,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt
        )
    }

    override fun toDomain(entity: MemberJpaEntity): Member {
        val id = MemberId.from(entity.id)
        val memberNickname = MemberNickname.of(entity.nickname)
        val memberEmail = MemberEmail.of(entity.email)
        return Member.reconstruct(
            id = id,
            memberNickname = memberNickname,
            memberEmail = memberEmail,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }
}