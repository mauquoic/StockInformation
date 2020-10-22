package com.mauquoi.stockinformation.controller

import com.mauquoi.stockinformation.const.URL
import com.mauquoi.stockinformation.const.URL.Market.BASE
import com.mauquoi.stockinformation.const.URL.Market.STOCKS_BY_MARKET
import com.mauquoi.stockinformation.const.URL.Market.STOCK_NAME
import com.mauquoi.stockinformation.domain.model.Market
import com.mauquoi.stockinformation.domain.model.entity.Stock
import com.mauquoi.stockinformation.domain.service.CurrencyService
import com.mauquoi.stockinformation.domain.service.StockService
import com.mauquoi.stockinformation.mapping.extension.toDto
import com.mauquoi.stockinformation.model.dto.ExchangeDto
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.inject.Inject

@RestController
@RequestMapping(BASE)
class MarketController @Inject constructor(private val stockService: StockService,
                                           private val currencyService: CurrencyService,
                                           val markets: List<Market>) {

    @GetMapping
    fun getMarkets(): ResponseEntity<List<Market>> {
        return ResponseEntity.ok(markets)
    }


    @PutMapping(STOCKS_BY_MARKET)
    fun updateEntireMarket(@PathVariable(URL.PathVariable.MARKET) market: String): ResponseEntity<Nothing> {
        stockService.updateStockExchange(market)
        return ResponseEntity.noContent().build()
    }

    @GetMapping(STOCKS_BY_MARKET)
    fun getMarket(@PathVariable(URL.PathVariable.MARKET) market: String): ResponseEntity<ExchangeDto> {
        val currencyInMarket = currencyService.getCurrencyForMarket(market)
        val stocks = stockService.getStockExchange(market).map { it.toDto() }
        return ResponseEntity.ok(ExchangeDto(currency = currencyInMarket, market = market, stocks = stocks))
    }

    @GetMapping(STOCK_NAME)
    fun getStockName(@PathVariable(URL.PathVariable.MARKET) market: String,
                     @PathVariable(URL.PathVariable.STOCK_SYMBOL) symbol: String): ResponseEntity<Stock> {
        return ResponseEntity.ok(stockService.getStockName(symbol, market))
    }
}