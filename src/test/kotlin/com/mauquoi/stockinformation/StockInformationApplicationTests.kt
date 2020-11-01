package com.mauquoi.stockinformation

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.context.ActiveProfiles

@EmbeddedKafka
@ActiveProfiles("test")
@SpringBootTest
class StockInformationApplicationTests {

	@Test
	fun contextLoads() {
	}

}
