package com.mauquoi.stockinformation.mapping.extension

import com.mauquoi.stockinformation.domain.model.CurrencyLookup
import com.mauquoi.stockinformation.domain.model.Market
import com.mauquoi.stockinformation.domain.model.entity.Stock
import com.mauquoi.stockinformation.gateway.ecb.dto.CurrencyLookupDto
import com.mauquoi.stockinformation.gateway.finnhub.dto.FinnhubStockDto
import com.mauquoi.stockinformation.mapping.MappingUtil
import com.mauquoi.stockinformation.model.dto.StockDetailsDto
import com.mauquoi.stockinformation.model.dto.StockDto
import java.util.*

fun Stock.toDto(): StockDto = StockDto(name = this.name,
        symbol = this.symbol,
        lookup = this.lookup!!)

fun Stock.toDetailsDto(): StockDetailsDto = StockDetailsDto(name = this.name,
        symbol = this.symbol,
        market = this.market,
        currency = this.currency,
        type = this.type)

fun StockDetailsDto.toDomain(): Stock = Stock(
        name = this.name,
        symbol = this.symbol,
        market = this.market,
        currency = this.currency,
        type = this.type
)

fun FinnhubStockDto.toDomain(market: String, markets: List<Market>): Stock {
    return Stock(
            name = this.description,
            market = market,
            symbol = MappingUtil.getSymbol(market, this.symbol),
            currency = getCurrency(this.currency, market, markets),
            remark = getNecessaryRemark(this),
            type = if (!this.type.isNullOrBlank()) this.type else null
    )
}

fun getNecessaryRemark(finnhubStockDto: FinnhubStockDto): String? {
    return if (finnhubStockDto.currency == "GBX") {
        "Traded in GBX"
    } else null
}

fun CurrencyLookupDto.toDomain(): CurrencyLookup = CurrencyLookup(
        base = this.base,
        date = this.date,
        rates = this.rates
)

fun getCurrency(currency: String?, market: String, markets: List<Market>): Currency {
    return try {
        if (currency.isNullOrBlank()) markets.first { it.market == market }.currency else Currency.getInstance(currency)
    } catch (e: Exception) {
        if (currency == "GBX") return Currency.getInstance("GBP")
        else Currency.getInstance("USD")
    }
}