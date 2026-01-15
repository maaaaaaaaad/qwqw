package com.mad.jellomarkserver.member.port.driven

import com.mad.jellomarkserver.member.core.domain.model.Member
import com.mad.jellomarkserver.member.core.domain.model.MemberId
import com.mad.jellomarkserver.member.core.domain.model.SocialId
import com.mad.jellomarkserver.member.core.domain.model.SocialProvider

interface MemberPort {
    fun save(member: Member): Member
    fun findBySocial(provider: SocialProvider, socialId: SocialId): Member?
    fun findBySocialId(socialId: SocialId): Member?
    fun findByIds(ids: List<MemberId>): List<Member>
}
