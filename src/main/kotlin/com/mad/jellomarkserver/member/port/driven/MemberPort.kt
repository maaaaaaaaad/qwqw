package com.mad.jellomarkserver.member.port.driven

import com.mad.jellomarkserver.member.core.domain.model.Email
import com.mad.jellomarkserver.member.core.domain.model.Member

interface MemberPort {
    fun existsByEmail(email: Email): Boolean
    fun save(member: Member): Member
}