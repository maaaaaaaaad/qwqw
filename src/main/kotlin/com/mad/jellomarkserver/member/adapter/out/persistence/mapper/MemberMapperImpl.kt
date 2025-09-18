package com.mad.jellomarkserver.member.adapter.out.persistence.mapper

import com.mad.jellomarkserver.member.adapter.out.persistence.entity.MemberJpaEntity
import com.mad.jellomarkserver.member.core.domain.model.BusinessRegistrationNumber
import com.mad.jellomarkserver.member.core.domain.model.Email
import com.mad.jellomarkserver.member.core.domain.model.Member
import com.mad.jellomarkserver.member.core.domain.model.MemberId
import com.mad.jellomarkserver.member.core.domain.model.MemberType
import com.mad.jellomarkserver.member.core.domain.model.Nickname
import org.springframework.stereotype.Component

@Component
class MemberMapperImpl : MemberMapper {
    override fun toEntity(domain: Member): MemberJpaEntity {
        return MemberJpaEntity(
            id = domain.id.value,
            nickname = domain.nickname.value,
            email = domain.email.value,
            memberType = domain.memberType.name,
            businessRegistrationNumber = domain.businessRegistrationNumber?.value,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt
        )
    }

    override fun toDomain(entity: MemberJpaEntity): Member {
        val id = MemberId.from(entity.id)
        val nickname = Nickname.of(entity.nickname)
        val email = Email.of(entity.email)
        val type = MemberType.valueOf(entity.memberType)
        val brn = entity.businessRegistrationNumber?.let { BusinessRegistrationNumber.of(it) }
        return Member.reconstruct(
            id = id,
            nickname = nickname,
            email = email,
            memberType = type,
            businessRegistrationNumber = brn,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }
}