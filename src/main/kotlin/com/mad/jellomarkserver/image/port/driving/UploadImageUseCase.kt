package com.mad.jellomarkserver.image.port.driving

import java.io.InputStream

fun interface UploadImageUseCase {
    fun execute(command: UploadImageCommand): String
}

data class UploadImageCommand(
    val inputStream: InputStream,
    val contentType: String,
    val originalFilename: String,
    val sizeBytes: Long
)
