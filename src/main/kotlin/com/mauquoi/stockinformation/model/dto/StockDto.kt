package com.mauquoi.stockinformation.model.dto

import java.util.*

data class StockDto(val name: String,
                    val symbol: String,
                    val lookup: String)

data class ExchangeDto(val currency: Currency,
                       val market: String,
                       val stocks: List<StockDto> = emptyList(),
                       val exchangeName: String? = null)