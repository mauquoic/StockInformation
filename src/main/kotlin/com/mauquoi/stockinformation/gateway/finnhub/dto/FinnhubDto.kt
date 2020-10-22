package com.mauquoi.stockinformation.gateway.finnhub.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.mauquoi.stockinformation.domain.model.entity.Stock
import java.math.BigDecimal
import java.math.BigInteger
import java.time.Instant
import java.time.LocalDate

data class QuoteDto(
        @set:JsonProperty("o") var open: Double,
        @set:JsonProperty("h") var high: Double,
        @set:JsonProperty("l") var low: Double,
        @set:JsonProperty("c") var current: Double,
        @set:JsonProperty("pc") var previousClose: Double,
        @set:JsonProperty("t") var timeStamp: Instant
)

data class FinnhubStockDto(
        val description: String,
        val displaySymbol: String,
        val symbol: String,
        val currency: String? = null,
        val type: String? = null
)

data class StockHistoryDto(
        @set:JsonProperty("c") var closeList: List<BigDecimal>,
        @set:JsonProperty("h") var highList: List<BigDecimal>,
        @set:JsonProperty("l") var lowList: List<BigDecimal>,
        @set:JsonProperty("o") var openList: List<BigDecimal>,
        @set:JsonProperty("s") var status: String,
        @set:JsonProperty("t") var timestamps: List<Instant>,
        @set:JsonProperty("v") var volumeList: List<BigInteger>
)