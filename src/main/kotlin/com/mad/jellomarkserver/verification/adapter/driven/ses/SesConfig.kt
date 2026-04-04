package com.mad.jellomarkserver.verification.adapter.driven.ses

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.ses.SesClient

@Configuration
@ConditionalOnProperty(name = ["aws.ses.sender-email"], matchIfMissing = false)
class SesConfig {

    @Value("\${aws.region:ap-northeast-2}")
    private lateinit var awsRegion: String

    @Bean
    fun sesClient(): SesClient {
        return SesClient.builder()
            .region(Region.of(awsRegion))
            .build()
    }
}
