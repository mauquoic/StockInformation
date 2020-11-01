package com.mauquoi.stockinformation.gateway.event

import org.apache.kafka.clients.admin.AdminClientConfig
import org.apache.kafka.clients.admin.NewTopic
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.KafkaAdmin
import java.util.*


@Configuration
class KafkaConfiguration {


    @Bean
    fun kafkaAdmin(
            @Value("\${kafka.bootstrapAddress}") bootstrapAddress: String,
    ): KafkaAdmin {
        val configs: MutableMap<String, Any> = HashMap()
        configs[AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapAddress
        return KafkaAdmin(configs)
    }

    @Bean
    fun performanceTopic(): NewTopic {
        return NewTopic(PERFORMANCE, 1, 1.toShort())
    }

    companion object {
        const val PERFORMANCE = "PERFORMANCE"
    }
}