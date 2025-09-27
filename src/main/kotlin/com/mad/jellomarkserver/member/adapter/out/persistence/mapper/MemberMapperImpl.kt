package com.mad.jellomarkserver.member.adapter.out.persistence.mapper

import com.mad.jellomarkserver.member.adapter.out.persistence.entity.MemberJpaEntity
import com.mad.jellomarkserver.member.core.domain.model.BusinessRegistrationNumber
import com.mad.jellomarkserver.member.core.domain.model.Email
import com.mad.jellomarkserver.member.core.domain.model.Member
import com.mad.jellomarkserver.member.core.domain.model.MemberId
import com.mad.jellomarkserver.member.core.domain.model.Nickname
import org.springframework.stereotype.Component

@Component
class MemberMapperImpl : MemberMapper {
    override fun toEntity(domain: Member): MemberJpaEntity {
        return MemberJpaEntity(
            id = domain.id.value,
            nickname = domain.nickname.value,
            email = domain.email.value,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt
        )
    }

    override fun toDomain(entity: MemberJpaEntity): Member {
        val id = MemberId.from(entity.id)
        val nickname = Nickname.of(entity.nickname)
        val email = Email.of(entity.email)
        return Member.reconstruct(
            id = id,
            nickname = nickname,
            email = email,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }
}