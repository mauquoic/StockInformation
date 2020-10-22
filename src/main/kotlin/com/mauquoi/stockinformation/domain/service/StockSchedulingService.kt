package com.mauquoi.stockinformation.domain.service

import com.mauquoi.stockinformation.domain.model.Market
import com.mauquoi.stockinformation.domain.model.entity.Stock
import com.mauquoi.stockinformation.domain.repository.StockHistoryRepository
import com.mauquoi.stockinformation.domain.repository.StockRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Profile
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDate
import javax.inject.Inject
import javax.transaction.Transactional

@Profile("!test")
@Service
class StockSchedulingService @Inject constructor(val stockService: StockService,
                                                 val stockRepository: StockRepository,
                                                 val stockHistoryRepository: StockHistoryRepository,
                                                 val markets: List<Market>) {

    private var updateStocks: Boolean = true

    @Scheduled(cron = "0 0 0 1 1/1 ?")
    fun updateMarkets() {
        updateStocks = false
        LOGGER.info("Starting the market update.")
        markets.shuffled().forEach { this.stockService.updateStockExchange(it.market) }
        LOGGER.info("Finished the market update.")
        Thread.sleep(60000)
        updateStocks = true
    }

    @EventListener(value = [ContextRefreshedEvent::class])
    fun initialization() {
        if (stockRepository.count() == 0L) {
            updateMarkets()
        }
    }

    @Scheduled(fixedRate = 8000)
    fun updateStockValues() {
        if (updateStocks) {
            GlobalScope.launch {
                LOGGER.info("Updating new stocks.")
                val stocksThatNeedUpdates = stockRepository.findTop8ByUpdatableIsTrueOrderByLastUpdateAsc()
                stocksThatNeedUpdates.forEach { async { updateStock(it) } }
                LOGGER.trace("Updated 60 more stocks")
            }
        }
    }

    @Async
    @Transactional
    fun updateStock(stock: Stock) {
        try {
            val startDate: LocalDate = stock.lastUpdate ?: LocalDate.now().minusYears(25)
            LOGGER.info("Gathering historical values for ${stock.lookup} for the time between $startDate and today.")
            val stockValues = this.stockService.getStockValues(stock.lookup!!)
            stockHistoryRepository.saveAll(stockValues)
            stock.updated(stockValues.maxBy { it.id.date }?.id?.date)
            LOGGER.info("Saved historical values for ${stock.lookup} for the time between $startDate and today.")
        } catch (e: Exception) {
            LOGGER.warn("Failed to update the history for ${stock.lookup}", e)
            stock.updatable = false
        } finally {
            stockRepository.save(stock)
        }
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(StockSchedulingService::class.java)
    }
}