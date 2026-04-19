package com.mad.jellomarkserver.member.port.driving

fun interface WithdrawMemberUseCase {
    fun withdraw(command: WithdrawMemberCommand)
}
