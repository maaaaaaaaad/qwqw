package com.mad.jellomarkserver.member.port.driven

import com.mad.jellomarkserver.member.core.domain.model.Member
import com.mad.jellomarkserver.member.core.domain.model.MemberEmail

interface MemberPort {
    fun save(member: Member): Member
    fun findByEmail(email: MemberEmail): Member?
}