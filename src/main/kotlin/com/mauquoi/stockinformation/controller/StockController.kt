package com.mauquoi.stockinformation.controller

import com.mauquoi.stockinformation.const.URL.PathVariable.STOCK_SYMBOL
import com.mauquoi.stockinformation.const.URL.Stock.STOCK_QUOTE
import com.mauquoi.stockinformation.domain.service.StockService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import javax.inject.Inject

@RestController
class StockController @Inject constructor(private val stockService: StockService) {

    @GetMapping(STOCK_QUOTE)
    fun getStockValue(@PathVariable(STOCK_SYMBOL) symbol: String): ResponseEntity<Double> {
        return ResponseEntity.ok(stockService.getStockPrice(symbol))
    }

}