package com.mad.jellomarkserver.image.port.driven

import java.io.InputStream

interface ImageStoragePort {
    fun upload(key: String, inputStream: InputStream, contentType: String, sizeBytes: Long): String
}
