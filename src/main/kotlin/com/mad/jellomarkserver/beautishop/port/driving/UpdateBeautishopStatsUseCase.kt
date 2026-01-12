package com.mad.jellomarkserver.beautishop.port.driving

fun interface UpdateBeautishopStatsUseCase {
    fun execute(command: UpdateBeautishopStatsCommand)
}
