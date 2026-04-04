package com.mad.jellomarkserver.image.adapter.driven.s3

import com.mad.jellomarkserver.image.port.driven.ImageStoragePort
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.stereotype.Component
import software.amazon.awssdk.services.s3.S3Client
import java.io.InputStream

@Component
@ConditionalOnMissingBean(S3Client::class)
class FakeImageStorageAdapter : ImageStoragePort {

    private val log = LoggerFactory.getLogger(FakeImageStorageAdapter::class.java)

    override fun upload(key: String, inputStream: InputStream, contentType: String, sizeBytes: Long): String {
        val url = "https://fake-bucket.s3.ap-northeast-2.amazonaws.com/$key"
        log.info("[FAKE] Image uploaded: key={}, size={}bytes, url={}", key, sizeBytes, url)
        return url
    }
}
