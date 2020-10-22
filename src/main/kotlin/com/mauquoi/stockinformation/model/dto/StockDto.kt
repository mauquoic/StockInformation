package com.mauquoi.stockinformation.model.dto

import java.util.*

data class StockDto(val name: String,
                    val symbol: String,
                    val lookup: String)

data class StockDetailsDto(val name: String,
                           val symbol: String,
                           val currency: Currency,
                           val market: String,
                           val type: String? = null)

data class ExchangeDto(val currency: Currency,
                       val market: String,
                       val stocks: List<StockDto>,
                       val exchangeName: String? = null)