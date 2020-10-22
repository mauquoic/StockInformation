package com.mauquoi.stockinformation.util

import com.mauquoi.stockinformation.domain.model.CurrencyLookup
import com.mauquoi.stockinformation.domain.model.entity.Exchange
import com.mauquoi.stockinformation.domain.model.entity.Stock
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
}