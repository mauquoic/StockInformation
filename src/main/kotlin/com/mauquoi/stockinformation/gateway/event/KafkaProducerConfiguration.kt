package com.mauquoi.stockinformation.gateway.event

import com.mauquoi.stockinformation.domain.model.MarketPerformance
import com.mauquoi.stockinformation.model.dto.MarketPerformanceDto
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import org.springframework.kafka.support.serializer.JsonSerializer

@Configuration
class KafkaProducerConfiguration {

    fun producerFactory(bootstrapAddress: String): ProducerFactory<String, List<MarketPerformanceDto>> {
        val configProps = HashMap<String, Any>()
        configProps[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapAddress
        configProps[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        configProps[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = JsonSerializer::class.java
        return DefaultKafkaProducerFactory(configProps)
    }

    @Bean
    fun kafkaTemplate(@Value("\${kafka.bootstrapAddress}") bootstrapAddress: String): KafkaTemplate<String, List<MarketPerformanceDto>> {
        return KafkaTemplate(producerFactory(bootstrapAddress))
    }
}