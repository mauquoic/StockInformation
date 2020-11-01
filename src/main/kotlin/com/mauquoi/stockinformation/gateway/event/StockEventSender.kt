package com.mauquoi.stockinformation.gateway.event

import com.mauquoi.stockinformation.domain.model.MarketPerformance
import com.mauquoi.stockinformation.gateway.event.KafkaConfiguration.Companion.PERFORMANCE
import com.mauquoi.stockinformation.mapping.extension.toDto
import com.mauquoi.stockinformation.model.dto.MarketPerformanceDto
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import javax.inject.Inject

@Component
class StockEventSender @Inject constructor(private val kafkaTemplate: KafkaTemplate<String, List<MarketPerformanceDto>>) {

    fun sendWinnersAndLosers(marketPerformance: List<MarketPerformance>) {
        LOGGER.debug("Sending event containing best and worst performers.")
        kafkaTemplate.send(PERFORMANCE, marketPerformance.map { it.toDto() })
        LOGGER.info("Sent event containing best and worst performers.")
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(StockEventSender::class.java)
    }
}