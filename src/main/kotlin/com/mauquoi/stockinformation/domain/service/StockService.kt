package com.mauquoi.stockinformation.domain.service

import com.mauquoi.stockinformation.StockNotFoundException
import com.mauquoi.stockinformation.domain.model.entity.Stock
import com.mauquoi.stockinformation.domain.model.entity.StockHistory
import com.mauquoi.stockinformation.domain.repository.StockRepository
import com.mauquoi.stockinformation.gateway.finnhub.FinnhubGateway
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDate
import javax.inject.Inject

@Service
class StockService @Inject constructor(private val stockRepository: StockRepository,
                                       private val finnhubGateway: FinnhubGateway) {

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

    fun getStockExchange(market: String): List<Stock> {
        return stockRepository.findAllByMarket(market).sortedBy { it.lookup }
    }

    fun getStockValues(stock: Stock, startDate: LocalDate, endDate: LocalDate = LocalDate.now()): List<StockHistory> {
        return finnhubGateway.getStockCandles(stock, startDate, endDate)
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(StockService::class.java)
    }
}