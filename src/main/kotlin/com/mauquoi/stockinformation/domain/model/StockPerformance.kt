package com.mauquoi.stockinformation.domain.model

import com.mauquoi.stockinformation.domain.model.entity.Stock
import java.math.BigDecimal

data class StockPerformance constructor(val stock: Stock, val performance: BigDecimal) {

    companion object {
        fun fromPair(pair: Pair<Stock, BigDecimal>): StockPerformance {
            return StockPerformance(stock = pair.first, performance = pair.second)
        }
    }
}

data class MarketPerformance constructor(val market: Market, val winners: List<StockPerformance>, val losers: List<StockPerformance>){

    fun containsPerformances(): Boolean {
        return winners.isNotEmpty()
    }
}