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
import org.springframework.web.client.HttpClientErrorException
import java.math.BigDecimal
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

    @Scheduled(fixedRate = 10100)
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
        val winnerMap = mutableMapOf<Market, Map<Stock, BigDecimal>>()
        val loserMap = mutableMapOf<Market, Map<Stock, BigDecimal>>()
        markets.forEach {
            LOGGER.info("Starting analysis for market ${it.market}")
            val (winners, losers) = stockService.getWinnersAndLosersForMarket(it)
            winnerMap[it] = winners
            loserMap[it] = losers
            LOGGER.info("The winners for market ${it.market} are ${winners.keys.map { win -> win.lookup }} with performances of ${winners.values} respectively.")
            LOGGER.info("The losers for market ${it.market} are ${losers.keys.map { los -> los.lookup }} with performances of ${losers.values} respectively.")
        }
        LOGGER.info("Finished the winner and loser calculation.")
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(StockSchedulingService::class.java)
    }
}