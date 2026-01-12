package com.mad.jellomarkserver.beautishop.port.driving

data class ListBeautishopsCommand(
    val page: Int = 0,
    val size: Int = 20
)
