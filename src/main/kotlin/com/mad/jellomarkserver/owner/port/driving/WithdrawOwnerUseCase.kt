package com.mad.jellomarkserver.owner.port.driving

fun interface WithdrawOwnerUseCase {
    fun withdraw(command: WithdrawOwnerCommand)
}
