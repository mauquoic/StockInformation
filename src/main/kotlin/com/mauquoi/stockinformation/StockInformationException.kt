package com.mauquoi.stockinformation

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import java.util.*


/**
 * Contains some errors class that can be thrown during runtime
 */

@ResponseStatus(value = HttpStatus.NOT_FOUND)
class StockNotFoundException : RuntimeException("No stock could be found by that ID.")

@ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY)
class UnknownCurrencyException(currency: Currency) : RuntimeException("Could not convert to the currency $currency.")

@ResponseStatus(value = HttpStatus.NOT_FOUND)
class MarketNotFoundException(id: String) : RuntimeException("No market could be found by ID $id.")

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
class MarketCurrencyMismatchException(market: String, currency: Currency) : RuntimeException("The market $market is not traded in $currency.")