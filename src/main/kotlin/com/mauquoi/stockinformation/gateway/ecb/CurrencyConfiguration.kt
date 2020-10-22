package com.mauquoi.stockinformation.gateway.ecb

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.*

@Configuration
class CurrencyConfiguration {

    @Bean
    fun supportedCurrencies(): List<Currency>{
        return listOf(
                Currency.getInstance("EUR"),
                Currency.getInstance("CAD"),
                Currency.getInstance("HKD"),
                Currency.getInstance("SGD"),
                Currency.getInstance("PHP"),
                Currency.getInstance("DKK"),
                Currency.getInstance("HUF"),
                Currency.getInstance("CZK"),
                Currency.getInstance("AUD"),
                Currency.getInstance("RON"),
                Currency.getInstance("SEK"),
                Currency.getInstance("IDR"),
                Currency.getInstance("BRL"),
                Currency.getInstance("RUB"),
                Currency.getInstance("HRK"),
                Currency.getInstance("JPY"),
                Currency.getInstance("THB"),
                Currency.getInstance("CHF"),
                Currency.getInstance("PLN"),
                Currency.getInstance("BGN"),
                Currency.getInstance("TRY"),
                Currency.getInstance("CNY"),
                Currency.getInstance("NOK"),
                Currency.getInstance("NZD"),
                Currency.getInstance("ZAR"),
                Currency.getInstance("USD"),
                Currency.getInstance("MXN"),
                Currency.getInstance("ILS"),
                Currency.getInstance("GBP"),
                Currency.getInstance("KRW"),
                Currency.getInstance("MYR")
        )
    }
}