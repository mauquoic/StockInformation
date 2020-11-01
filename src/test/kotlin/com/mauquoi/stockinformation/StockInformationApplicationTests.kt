package com.mauquoi.stockinformation

import org.junit.jupiter.api.Test
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.context.ActiveProfiles


@EmbeddedKafka
@ActiveProfiles("test")
@SpringBootTest(properties = ["spring.kafka.bootstrap-servers=\${spring.embedded.kafka.brokers}"],
        classes = [KafkaAutoConfiguration::class])
class StockInformationApplicationTests {

    @Test
    fun contextLoads() {
    }

}
