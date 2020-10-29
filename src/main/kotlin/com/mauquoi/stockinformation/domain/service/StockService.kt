package com.mauquoi.stockinformation.domain.service

import com.mauquoi.stockinformation.StockNotFoundException
import com.mauquoi.stockinformation.domain.model.Market
import com.mauquoi.stockinformation.domain.model.entity.Stock
import com.mauquoi.stockinformation.domain.model.entity.StockHistory
import com.mauquoi.stockinformation.domain.repository.StockHistoryRepository
import com.mauquoi.stockinformation.domain.repository.StockRepository
import com.mauquoi.stockinformation.gateway.finnhub.FinnhubGateway
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import javax.inject.Inject
import javax.transaction.Transactional
import kotlin.streams.asSequence
import kotlin.streams.toList

@Service
class StockService @Inject constructor(private val stockRepository: StockRepository,
                                       private val finnhubGateway: FinnhubGateway,
                                       private val stockHistoryRepository: StockHistoryRepository) {

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
    fun getWinnersAndLosersForMarket(market: Market): Pair<Map<Stock, BigDecimal>, Map<Stock, BigDecimal>> {
        val orderedMap = stockRepository.findAllByMarketAndLastUpdateAfterAndUpdatableIsTrue(market.market)
                .asSequence()
                .associateBy({ it }, {
                    calculateGain(stockHistoryRepository.findAllByIdStockLookupAndIdDateInOrderByIdDateAsc(it.lookup!!))
                })
                .filter { it.value != BigDecimal.ZERO }
                .toList()
                .sortedByDescending { (_, value) -> value }
                .toMap()
        val winners = orderedMap.toList().take(10).toMap()
        val losers = orderedMap.toList().takeLast(10).toMap()
        return Pair(winners, losers)
    }

    private fun calculateGain(stockHistories: List<StockHistory>): BigDecimal {
        return when (stockHistories.size) {
            2 -> {
                (stockHistories[1].valueAtClose - stockHistories[0].valueAtOpen).times(BigDecimal(100))
                        .divide(stockHistories[0].valueAtOpen, 2, RoundingMode.HALF_UP)
            }
            1 -> {
                LOGGER.warn("Couldn't calculate the performance for ${stockHistories[0].id.stockLookup}")
                BigDecimal.ZERO
            }
            else -> {
                BigDecimal.ZERO
            }
        }
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(StockService::class.java)
    }
}