package com.mauquoi.stockinformation.util

import com.mauquoi.stockinformation.domain.model.CurrencyLookup
import com.mauquoi.stockinformation.domain.model.entity.Exchange
import com.mauquoi.stockinformation.domain.model.entity.Stock
import com.mauquoi.stockinformation.gateway.ecb.dto.CurrencyLookupDto
import com.mauquoi.stockinformation.gateway.finnhub.dto.FinnhubStockDto
import com.mauquoi.stockinformation.gateway.finnhub.dto.QuoteDto
import com.mauquoi.stockinformation.gateway.finnhub.dto.StockHistoryDto
import java.math.BigDecimal
import java.math.BigInteger
import java.time.Instant
import java.time.LocalDate
import java.util.*

object TestObjectCreator {

    fun usd(): Currency = Currency.getInstance("USD")
    fun chf(): Currency = Currency.getInstance("CHF")

    fun createUsStock(symbol: String = "ACN",
                      market: String = "US",
                      name: String = "Accenture",
                      currency: Currency = Currency.getInstance("USD"),
                      updatable: Boolean = true,
                      lastUpdate: LocalDate = LocalDate.now(),
                      type: String = "EQS",
                      remark: String? = null): Stock {
        return Stock(symbol = symbol, market = market, name = name, currency = currency, type = type, lastUpdate = lastUpdate, remark = remark, updatable = updatable)
    }

    internal fun createChStock(
            symbol: String = "GEBN",
            market: String = "SW",
            name: String = "Geberit",
            currency: Currency = Currency.getInstance("CHF"),
            updatable: Boolean = true,
            lastUpdate: LocalDate = LocalDate.now(),
            type: String = "EQS",
            remark: String? = null): Stock {
        return Stock(symbol = symbol, market = market, name = name, currency = currency, type = type, lastUpdate = lastUpdate, remark = remark, updatable = updatable)
    }

    fun createCurrencyLookup(): CurrencyLookup {
        val currency = Currency.getInstance("USD")
        return CurrencyLookup(base = currency, date = LocalDate.now(), rates = mapOf(
                Currency.getInstance("CHF") to 1.1,
                Currency.getInstance("EUR") to 0.9
        ))
    }

    fun createExchange(): Exchange {
        return Exchange(listOf(
                Stock(name = "Accenture", symbol = "ACN", currency = usd(), type = "EQS", market = "US"),
                Stock(name = "Geberit", symbol = "GEBN", currency = chf(), type = "EQS", market = "SW"),
                Stock(name = "Swiss high dividends", symbol = "CHDVD", currency = chf(), type = "ETF", market = "SW")
        ))
    }

    fun createQuoteDto(open: Double = 2.0,
                       previousClose: Double = 1.8,
                       current: Double = 1.9,
                       low: Double = 1.7,
                       high: Double = 2.1,
                       timeStamp: Instant = Instant.now()
    ): QuoteDto {
        return QuoteDto(open = open, previousClose = previousClose, current = current, low = low, high = high, timeStamp = timeStamp)
    }

    fun createStockHistoryDto(): StockHistoryDto {
        return StockHistoryDto(closeList = bigDecimalList(), openList = bigDecimalList(), highList = bigDecimalList(), lowList = bigDecimalList(),
                timestamps = listOf(Instant.now()), volumeList = listOf(BigInteger.ONE), status = "OK")
    }

    fun createStockDtos(): List<FinnhubStockDto> {
        return listOf(
                FinnhubStockDto(description = "Accenture", symbol = "ACN", displaySymbol = "ACN", currency = "USD", type = "DS"),
                FinnhubStockDto(description = "Geberit", symbol = "GEBN.SW", displaySymbol = "GEBN.SW", currency = "CHF", type = "DS"),
                FinnhubStockDto(description = "Swiss high dividends", symbol = "CHDVD.SW", displaySymbol = "CHDVD.SW", currency = "CHF", type = "DS")
        )
    }

    fun createCurrencyLookupDto(): CurrencyLookupDto {
        val currency = Currency.getInstance("USD")
        return CurrencyLookupDto(base = currency, date = LocalDate.now(), rates = mapOf(
                Currency.getInstance("CHF") to 1.1,
                Currency.getInstance("EUR") to 0.9
        ))
    }

    private fun bigDecimalList(): List<BigDecimal> {
        return listOf(BigDecimal.ONE)
    }
}