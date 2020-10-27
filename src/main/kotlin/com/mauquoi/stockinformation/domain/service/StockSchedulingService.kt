package com.mauquoi.stockinformation.domain.service

import com.mauquoi.stockinformation.domain.model.Market
import com.mauquoi.stockinformation.domain.model.entity.Stock
import com.mauquoi.stockinformation.domain.repository.StockHistoryRepository
import com.mauquoi.stockinformation.domain.repository.StockRepository
import kotlinx.coroutines.*
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDate
import javax.inject.Inject
import javax.transaction.Transactional

@Profile("!test")
@Service
class StockSchedulingService @Inject constructor(private val stockService: StockService,
                                                 private val stockRepository: StockRepository,
                                                 private val stockHistoryRepository: StockHistoryRepository,
                                                 private val markets: List<Market>) {

    private var updateStocks: Boolean = true

    @Value("\${app.scheduling.waiting-time}")
    private var waitingTime: Long = 1

    @Scheduled(cron = "0 0 0 1 1/1 ?")
    fun updateMarkets() {
        updateStocks = false
        LOGGER.info("Starting the market update.")
        runBlocking {
            val jobs = markets.shuffled().map { GlobalScope.launch { stockService.updateStockExchange(it.market) } }
            jobs.joinAll()
        }
        LOGGER.info("Finished the market update.")
        Thread.sleep(waitingTime)
        updateStocks = true
    }

    @EventListener(value = [ContextRefreshedEvent::class])
    fun initialization() {
        if (stockRepository.count() == 0L) {
            updateMarkets()
        }
    }

    @Scheduled(fixedRate = 10000)
    fun updateStockValues() {
        if (updateStocks) {
            LOGGER.info("Updating new stocks.")
            val stocksThatNeedUpdates = stockRepository.findTop10ByUpdatableIsTrueOrderByLastUpdateAsc()
            runBlocking {
                val jobs = stocksThatNeedUpdates.map { GlobalScope.launch(Dispatchers.Default) { updateStock(it) } }
                jobs.joinAll()
            }
            LOGGER.trace("Updated 8 more stocks")
        }
    }

    @Transactional
    fun updateStock(stock: Stock) {
        try {
            val startDate: LocalDate = stock.lastUpdate?.plusDays(1L) ?: LocalDate.now().minusYears(25)
            LOGGER.info("Gathering historical values for ${stock.lookup} for the time between $startDate and today.")
            val stockValues = this.stockService.getStockValues(stock, stock.lastUpdate?.plusDays(1)
                    ?: LocalDate.now().minusYears(25))
            stockHistoryRepository.saveAll(stockValues)
            stock.updated(stockValues.maxByOrNull { it.id.date }?.id?.date)
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