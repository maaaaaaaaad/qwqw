package com.mad.jellomarkserver.member.port.driven

import com.mad.jellomarkserver.member.core.domain.model.Member

fun interface MemberPort {
    fun save(member: Member): Member
}