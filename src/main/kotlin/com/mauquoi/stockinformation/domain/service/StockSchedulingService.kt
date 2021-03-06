package com.mauquoi.stockinformation.domain.service

import com.mauquoi.stockinformation.domain.model.Market
import com.mauquoi.stockinformation.domain.model.MarketPerformance
import com.mauquoi.stockinformation.domain.model.entity.Stock
import com.mauquoi.stockinformation.domain.repository.StockHistoryRepository
import com.mauquoi.stockinformation.domain.repository.StockRepository
import com.mauquoi.stockinformation.gateway.event.StockEventSender
import kotlinx.coroutines.*
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException
import java.time.LocalDate
import javax.inject.Inject
import javax.transaction.Transactional

@Profile("!test")
@Service
class StockSchedulingService @Inject constructor(
        private val stockService: StockService,
        private val stockRepository: StockRepository,
        private val stockHistoryRepository: StockHistoryRepository,
        private val markets: List<Market>,
        private val stockEventSender: StockEventSender,
) {

    private var updateStocks: Boolean = true

    @Value("\${app.scheduling.waiting-time}")
    private var waitingTime: Long = 1

    @Scheduled(cron = "0 0 0 1 * ?")
    fun updateMarkets() {
        updateStocks = false
        Thread.sleep(waitingTime) // needed to not run into 429 from finnhub
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

    @Scheduled(cron = "0 0 8 * * ?")
    fun stopUpdates() {
        LOGGER.info("Stopping updates for the day")
        updateStocks = false
    }

    @Scheduled(cron = "0 0 22 * * ?")
    fun startUpdates() {
        LOGGER.info("Starting updates for the night")
        updateStocks = true
    }

    @Scheduled(fixedRate = 25000)
    fun updateStockValues() {
        if (updateStocks) {
            LOGGER.info("Updating new stocks.")
            val stocksThatNeedUpdates = stockRepository.findTop30ByUpdatableIsTrueOrderByLastUpdateAsc()
            runBlocking {
                val jobs =
                        coroutineScope {
                            stocksThatNeedUpdates.map {
                                launch(Dispatchers.Default) {
                                    withContext(Dispatchers.IO) {
                                        updateStock(it)
                                    }
                                }
                            }
                        }
                jobs.joinAll()
            }
            LOGGER.info("Updated more stocks")
        }
    }

    @Transactional
    fun updateStock(stock: Stock) {
        try {
            val startDate: LocalDate = stock.lastUpdate?.plusDays(1L) ?: LocalDate.now().minusYears(25)
            LOGGER.info("Gathering historical values for ${stock.lookup} for the time between $startDate and today.")
            val stockValues = this.stockService.getStockValues(stock, startDate = stock.lastUpdate?.plusDays(1)
                    ?: LocalDate.now().minusYears(25),
                    endDate = LocalDate.now().plusDays(1)
            )
            stockHistoryRepository.saveAll(stockValues)
            stock.updated(stockValues.maxByOrNull { it.id.date }?.id?.date)
            LOGGER.info("Saved historical values for ${stock.lookup} for the time between $startDate and today.")
            stockRepository.save(stock)
        } catch (e: HttpClientErrorException.TooManyRequests) {
            LOGGER.info("Too many requests.")
        } catch (e: Exception) {
            LOGGER.warn("Failed to update the history for ${stock.lookup}", e)
            stock.updatable = false
            stockRepository.save(stock)
        }
    }

    @Scheduled(cron = "0 0 21 * * SAT")
    fun findWinnersAndLosers() {
        LOGGER.info("Starting the winner and loser calculation.")
        val marketPerformances = mutableListOf<MarketPerformance>()
        markets.forEach {
            LOGGER.info("Starting analysis for market ${it.market}")
            val marketPerformance = stockService.getWinnersAndLosersForMarket(it)
            if (marketPerformance.containsPerformances()) {
                marketPerformances.add(marketPerformance)
            }
            LOGGER.info("The winners for market ${it.market} are ${marketPerformance.winners}.")
            LOGGER.info("The losers for market ${it.market} are ${marketPerformance.losers}.")
        }
        LOGGER.info("Finished the winner and loser calculation, now sending event.")
        stockEventSender.sendWinnersAndLosers(marketPerformances)
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(StockSchedulingService::class.java)
    }
}