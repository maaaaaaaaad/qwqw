package com.mad.jellomarkserver.member.port.driving

import com.mad.jellomarkserver.member.core.domain.model.Member

fun interface GetCurrentMemberUseCase {
    fun execute(command: GetCurrentMemberCommand): Member
}
