package com.mauquoi.stockinformation.gateway.finnhub

import com.mauquoi.stockinformation.domain.model.Market
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.*

@Configuration
class MarketConfiguration {

    @Bean
    fun markets(): List<Market> {
        return listOf(
                Market(market = "US", currency = Currency.getInstance("USD"), description = "United States Exchanges"),
                Market(market = "SW", currency = Currency.getInstance("CHF"), description = "Swiss Exchange"),
                Market(market = "DE", currency = Currency.getInstance("EUR"), description = "XETRA"),
                Market(market = "PA", currency = Currency.getInstance("EUR"), description = "Euronext Paris"),
                Market(market = "L", currency = Currency.getInstance("GBP"), description = "London Stock Exchange"),
                Market(market = "BR", currency = Currency.getInstance("EUR"), description = "Euronext Brussels"),
                Market(market = "SI", currency = Currency.getInstance("SGD"), description = "Singapore Exchange"),
                Market(market = "HK", currency = Currency.getInstance("HKD"), description = "Hong Kong Exchanges"),
                Market(market = "SS", currency = Currency.getInstance("CNY"), description = "Shanghai Stock Exchange"),
                Market(market = "SZ", currency = Currency.getInstance("CNY"), description = "Shenzhen Stock Exchange"),
                Market(market = "T", currency = Currency.getInstance("JPY"), description = "Tokyo Stock Exchange"),
                Market(market = "VN", currency = Currency.getInstance("VND"), description = "Vietnam exchanges including HOSE, HNX and UPCOM\n"),
                Market(market = "SG", currency = Currency.getInstance("EUR"), description = "Börse Stuttgart"),
                Market(market = "BO", currency = Currency.getInstance("INR"), description = "BSE Ltd (India)"),
                Market(market = "HE", currency = Currency.getInstance("EUR"), description = "NASDAQ OMX Helsinki Ltd"),
                Market(market = "BC", currency = Currency.getInstance("COP"), description = "Bolsa de valores de Colombia"),
                Market(market = "OL", currency = Currency.getInstance("NOK"), description = "Oslo Bors ASA"),
                Market(market = "ME", currency = Currency.getInstance("RUB"), description = "Moscow Exchange"),
                Market(market = "NZ", currency = Currency.getInstance("NZD"), description = "New Zealand Exchange Ltd"),
                Market(market = "MX", currency = Currency.getInstance("MXN"), description = "Bolsa Mexicana de valores"),
                Market(market = "JK", currency = Currency.getInstance("IDR"), description = "Indonesia Stock Exchange"),
                Market(market = "TO", currency = Currency.getInstance("CAD"), description = "Toronto Stock Exchange"),
                Market(market = "AS", currency = Currency.getInstance("EUR"), description = "Euronext Amsterdam"),
                Market(market = "WA", currency = Currency.getInstance("PLN"), description = "Warsaw Stock Exchanges"),
                Market(market = "BE", currency = Currency.getInstance("EUR"), description = "Börse Berlin"),
                Market(market = "DU", currency = Currency.getInstance("EUR"), description = "Börse Düsseldorf"),
                Market(market = "AX", currency = Currency.getInstance("AUD"), description = "ASX (Australia)"),
                Market(market = "SA", currency = Currency.getInstance("BRL"), description = "Sao Paolo Stock Exchange"),
                Market(market = "JO", currency = Currency.getInstance("ZAR"), description = "Johannesburg Stock Exchange"),
                Market(market = "IR", currency = Currency.getInstance("EUR"), description = "Irish Stock Exchange"),
                Market(market = "VI", currency = Currency.getInstance("EUR"), description = "Wiener Börse"),
                Market(market = "MI", currency = Currency.getInstance("EUR"), description = "Market for Investment Vehicles"),
                Market(market = "KQ", currency = Currency.getInstance("KRW"), description = "KOSDAQ (Korea)"),
                Market(market = "KS", currency = Currency.getInstance("KRW"), description = "Korea Exchange"),
                Market(market = "F", currency = Currency.getInstance("EUR"), description = "Deutsche Börse AG"),
                Market(market = "DB", currency = Currency.getInstance("AED"), description = "Dubai Financial Market"),
                Market(market = "LS", currency = Currency.getInstance("EUR"), description = "Euronext Lisbon"),
                Market(market = "RG", currency = Currency.getInstance("EUR"), description = "NASDAQ OMX Riga"),
                Market(market = "MU", currency = Currency.getInstance("EUR"), description = "Börse München"),
                Market(market = "KL", currency = Currency.getInstance("MYR"), description = "Bursa Malaysia"),
                Market(market = "VS", currency = Currency.getInstance("EUR"), description = "NASDAQ OMX Vilnius"),
                Market(market = "HM", currency = Currency.getInstance("EUR"), description = "Hanseatische Wertpapierbörse Hamburg"),
                Market(market = "TL", currency = Currency.getInstance("EUR"), description = "NASDAQ OMX Tallinn"),
                Market(market = "AT", currency = Currency.getInstance("EUR"), description = "Athens Exchange"),
                Market(market = "MC", currency = Currency.getInstance("EUR"), description = "Bolsa de Madrid"),
                Market(market = "QA", currency = Currency.getInstance("QAR"), description = "Qatar Exchange"),
                Market(market = "ST", currency = Currency.getInstance("SEK"), description = "NASDAQ OMX Nordic"),
                Market(market = "PR", currency = Currency.getInstance("CZK"), description = "Prague Stock Exchange"),
                Market(market = "V", currency = Currency.getInstance("CAD"), description = "TSX Venture Exchange & NEX"),
                Market(market = "CN", currency = Currency.getInstance("CAD"), description = "Canadian National Stock Exchange"),
                Market(market = "BK", currency = Currency.getInstance("THB"), description = "Stock Exchange of Thailand"),
                Market(market = "IS", currency = Currency.getInstance("TRY"), description = "Borsa Istanbul"),
                Market(market = "SN", currency = Currency.getInstance("CLP"), description = "Santiago Stock Exchange"),
                Market(market = "TA", currency = Currency.getInstance("ILS"), description = "Tel Aviv Stock Exchange"),
                Market(market = "TW", currency = Currency.getInstance("TWD"), description = "Taiwan Stock Exchange"),
                Market(market = "SR", currency = Currency.getInstance("SAR"), description = "Saudi Stock Exchange"),
                Market(market = "BA", currency = Currency.getInstance("ARS"), description = "Bolsa de Comercio de Buenos Aires"),
                Market(market = "NE", currency = Currency.getInstance("CAD"), description = "Aequitas NEO Exchange"),
                Market(market = "IC", currency = Currency.getInstance("ISK"), description = "NASDAQ OMX Iceland"),
                Market(market = "NS", currency = Currency.getInstance("INR"), description = "National Stock Exchange of India"),
                Market(market = "BD", currency = Currency.getInstance("HUF"), description = "Budapest Stock Exchange"),
                Market(market = "CO", currency = Currency.getInstance("DKK"), description = "OMX Nordic Exchange Copenhagen")
        )
    }
}