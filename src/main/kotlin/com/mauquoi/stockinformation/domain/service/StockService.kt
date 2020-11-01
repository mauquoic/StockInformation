package com.mauquoi.stockinformation.domain.service

import com.mauquoi.stockinformation.StockNotFoundException
import com.mauquoi.stockinformation.domain.model.Market
import com.mauquoi.stockinformation.domain.model.MarketPerformance
import com.mauquoi.stockinformation.domain.model.StockPerformance
import com.mauquoi.stockinformation.domain.model.entity.Stock
import com.mauquoi.stockinformation.domain.model.entity.StockHistory
import com.mauquoi.stockinformation.domain.repository.StockHistoryRepository
import com.mauquoi.stockinformation.domain.repository.StockRepository
import com.mauquoi.stockinformation.gateway.finnhub.FinnhubGateway
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject
import javax.transaction.Transactional
import kotlin.streams.toList

@Service
class StockService @Inject constructor(
        private val stockRepository: StockRepository,
        private val finnhubGateway: FinnhubGateway,
        private val stockHistoryRepository: StockHistoryRepository,
) {

    fun getStock(id: Long): Stock {
        return stockRepository.findById(id).orElseThrow { StockNotFoundException() }
    }

    fun getStockPrice(symbol: String): Double {
        return finnhubGateway.getStockPrice(symbol).current
    }

    fun getStockName(symbol: String, market: String): Stock {
        val exchange = finnhubGateway.getExchange(market)
        return exchange.stocks.first { it.symbol == symbol }
    }

    fun updateStockExchange(market: String) {
        LOGGER.info("Updating market $market.")
        try {
            val exchange = finnhubGateway.getExchange(market)
            exchange.stocks.distinctBy { it.lookup }
                    .forEach {
                        if (stockRepository.findByLookup(it.lookup!!).isEmpty) {
                            stockRepository.save(it)
                        }
                    }
        } catch (e: Exception) {
            LOGGER.warn("Failed to update $market", e)
        }
    }

    @Transactional
    fun getStockExchange(market: String): List<Stock> {
        return stockRepository.findAllByMarketAndUpdatableIsTrue(market).toList().sortedBy { it.lookup }
    }

    fun getStockValues(stock: Stock, startDate: LocalDate, endDate: LocalDate = LocalDate.now()): List<StockHistory> {
        return finnhubGateway.getStockCandles(stock, startDate, endDate)
    }

    @Transactional
    fun getWinnersAndLosersForMarket(market: Market): MarketPerformance {
        LOGGER.info("Before lookup: ${LocalDateTime.now()} ")
//        val lookups = stockRepository.findAllByMarketAndLastUpdateAfterAndUpdatableIsTrue(market.market)
//                .associateBy({ it }, {
//                    val histories = stockHistoryRepository.getWeeklyPerformance(it.lookup!!)
//
//                    if (histories.isNotEmpty()) {
//                        histories[0].comparePerformance(histories[histories.size - 1])
//                    } else BigDecimal.ZERO
//                })
        val stockLookups = stockRepository.getAllWithWeeklyUpdates(market.market)
        val weeklyPerformance = stockHistoryRepository.getWeeklyPerformance(stockLookups.map { it.lookup!! })
        val performances = stockLookups
                .associateBy({ it }, {
                    val histories = weeklyPerformance.filter { h -> h.id.stockLookup == it.lookup }
                    if (histories.isNotEmpty()) {
                        histories[0].comparePerformance(histories[histories.size - 1])
                    } else BigDecimal.ZERO
                })
                .filter { it.value != BigDecimal.ZERO && it.value < BigDecimal(250) }
                .toList()
                .sortedByDescending { (_, value) -> value }
                .toMap()
        val winners = performances.toList().take(10).map { StockPerformance.fromPair(it) }
        val losers = performances.toList().takeLast(10).map { StockPerformance.fromPair(it) }
        return MarketPerformance(market, winners, losers)
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(StockService::class.java)
    }
}