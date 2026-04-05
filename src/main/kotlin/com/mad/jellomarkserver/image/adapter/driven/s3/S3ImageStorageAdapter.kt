package com.mad.jellomarkserver.image.adapter.driven.s3

import com.mad.jellomarkserver.image.port.driven.ImageStoragePort
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.io.InputStream

@Component
@Primary
@ConditionalOnBean(S3Client::class)
class S3ImageStorageAdapter(
    private val s3Client: S3Client,
    @Value("\${aws.s3.bucket-name}") private val bucketName: String,
    @Value("\${aws.s3.region:ap-northeast-2}") private val region: String
) : ImageStoragePort {

    private val log = LoggerFactory.getLogger(S3ImageStorageAdapter::class.java)

    override fun upload(key: String, inputStream: InputStream, contentType: String, sizeBytes: Long): String {
        val request = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .contentType(contentType)
            .build()

        s3Client.putObject(request, RequestBody.fromInputStream(inputStream, sizeBytes))

        val url = "https://$bucketName.s3.$region.amazonaws.com/$key"
        log.info("Image uploaded to {}", url)
        return url
    }
}
