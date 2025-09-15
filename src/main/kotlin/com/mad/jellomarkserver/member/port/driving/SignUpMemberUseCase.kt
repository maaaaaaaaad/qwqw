package com.mad.jellomarkserver.member.port.driving

import com.mad.jellomarkserver.member.core.domain.model.Member

interface SignUpMemberUseCase {
    fun signUp(command: SignUpMemberCommand): Member
}
