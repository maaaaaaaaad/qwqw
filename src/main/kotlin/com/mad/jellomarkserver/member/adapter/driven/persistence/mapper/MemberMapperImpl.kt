package com.mad.jellomarkserver.member.adapter.driven.persistence.mapper

import com.mad.jellomarkserver.member.adapter.driven.persistence.entity.MemberJpaEntity
import com.mad.jellomarkserver.member.core.domain.model.*
import org.springframework.stereotype.Component

@Component
class MemberMapperImpl : MemberMapper {
    override fun toEntity(domain: Member): MemberJpaEntity {
        return MemberJpaEntity(
            id = domain.id.value,
            socialProvider = domain.socialProvider.name,
            socialId = domain.socialId.value,
            nickname = domain.memberNickname.value,
            displayName = domain.displayName.value,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt
        )
    }

    override fun toDomain(entity: MemberJpaEntity): Member {
        val id = MemberId.from(entity.id)
        val socialProvider = SocialProvider.valueOf(entity.socialProvider)
        val socialId = SocialId(entity.socialId)
        val memberNickname = MemberNickname.of(entity.nickname)
        val displayName = MemberDisplayName.of(entity.displayName)
        return Member.reconstruct(
            id = id,
            socialProvider = socialProvider,
            socialId = socialId,
            memberNickname = memberNickname,
            displayName = displayName,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }
}
