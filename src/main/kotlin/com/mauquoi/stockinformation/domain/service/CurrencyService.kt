package com.mauquoi.stockinformation.domain.service

import com.mauquoi.stockinformation.MarketCurrencyMismatchException
import com.mauquoi.stockinformation.MarketNotFoundException
import com.mauquoi.stockinformation.UnknownCurrencyException
import com.mauquoi.stockinformation.domain.model.CurrencyLookup
import com.mauquoi.stockinformation.gateway.ecb.EcbGateway
import com.mauquoi.stockinformation.domain.model.Market
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.util.*
import javax.inject.Inject

@Service
class CurrencyService @Inject constructor(private val ecbGateway: EcbGateway,
                                          private val supportedCurrencies: List<Currency>,
                                          private val markets: List<Market>) {

    fun getRates(baseCurrency: Currency, date: LocalDate? = null): CurrencyLookup {
        return ecbGateway.getConversionValues(baseCurrency, date)
    }

    fun getCurrencies(): List<Currency> {
        return supportedCurrencies
    }

    private fun calculateMainCurrencyValue(distribution: Map<Currency, BigDecimal>, preferredCurrency: Currency): BigDecimal {
        return distribution.map { entry -> convertCurrency(preferredCurrency, entry.key, entry.value) }
                .fold(BigDecimal.ZERO) { acc, nextValue -> acc.plus(nextValue) }
                .setScale(2, RoundingMode.HALF_UP)
    }

    private fun convertCurrency(base: Currency, to: Currency, amount: BigDecimal): BigDecimal {
        if (base == to) return amount
        val currencies = getRates(baseCurrency = base)
        return currencies.rates[to]?.let { amount.setScale(2).div(it.toBigDecimal()) }
                ?: throw UnknownCurrencyException(base)
    }

    fun getCurrencyForMarket(market: String): Currency {
        return markets.firstOrNull { m -> m.market == market }?.currency ?: throw MarketNotFoundException(market)
    }

    fun verifyCurrencyCompatibility(market: String, currency: Currency) {
        val marketCurrency = getCurrencyForMarket(market)
        if (marketCurrency != currency) throw MarketCurrencyMismatchException(market, currency)
    }
}
