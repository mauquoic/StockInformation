package com.mauquoi.stockinformation.mapping.extension

import com.mauquoi.stockinformation.domain.model.CurrencyLookup
import com.mauquoi.stockinformation.domain.model.Market
import com.mauquoi.stockinformation.domain.model.MarketPerformance
import com.mauquoi.stockinformation.domain.model.StockPerformance
import com.mauquoi.stockinformation.domain.model.entity.Stock
import com.mauquoi.stockinformation.gateway.ecb.dto.CurrencyLookupDto
import com.mauquoi.stockinformation.gateway.finnhub.dto.FinnhubStockDto
import com.mauquoi.stockinformation.mapping.MappingUtil
import com.mauquoi.stockinformation.model.dto.ExchangeDto
import com.mauquoi.stockinformation.model.dto.MarketPerformanceDto
import com.mauquoi.stockinformation.model.dto.StockDto
import com.mauquoi.stockinformation.model.dto.StockPerformanceDto
import java.util.*

fun Stock.toDto(): StockDto = StockDto(name = this.name,
        symbol = this.symbol,
        lookup = this.lookup!!
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

fun Market.toDto(): ExchangeDto {
    return ExchangeDto(
            currency = this.currency,
            market = this.market,
            exchangeName = this.description
    )
}

fun StockPerformance.toDto(): StockPerformanceDto {
    return StockPerformanceDto(
            stock = this.stock.toDto(),
            performance = this.performance)
}

fun MarketPerformance.toDto(): MarketPerformanceDto {
    return MarketPerformanceDto(
            market = this.market.toDto(),
            winners = this.winners.map { it.toDto() },
            losers = this.losers.map { it.toDto() },
    )
}