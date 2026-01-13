package com.mad.jellomarkserver.beautishop.port.driving

fun interface DeleteBeautishopUseCase {
    fun delete(command: DeleteBeautishopCommand)
}
