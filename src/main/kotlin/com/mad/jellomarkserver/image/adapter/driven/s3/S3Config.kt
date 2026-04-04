package com.mad.jellomarkserver.image.adapter.driven.s3

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client

@Configuration
@ConditionalOnProperty(name = ["aws.s3.bucket-name"], matchIfMissing = false)
class S3Config {

    @Value("\${aws.s3.region:ap-northeast-2}")
    private lateinit var awsRegion: String

    @Bean
    fun s3Client(): S3Client {
        return S3Client.builder()
            .region(Region.of(awsRegion))
            .build()
    }
}
