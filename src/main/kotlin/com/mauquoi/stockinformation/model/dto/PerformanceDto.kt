package com.mauquoi.stockinformation.model.dto

import java.math.BigDecimal

data class StockPerformanceDto(val stock: StockDto, val performance: BigDecimal)

data class MarketPerformanceDto(val market: ExchangeDto, val winners: List<StockPerformanceDto>, val losers: List<StockPerformanceDto>)