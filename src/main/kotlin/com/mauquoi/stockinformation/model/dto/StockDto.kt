package com.mauquoi.stockinformation.model.dto

import java.math.BigDecimal
import java.time.LocalDate
import java.util.*

data class StockDto(val name: String,
                    val symbol: String,
                    val lookup: String)

data class StockDetailsDto(val name: String,
                           val symbol: String,
                           val currency: Currency,
                           val market: String,
                           val type: String? = null)

data class ExchangeDto(val currency: Currency,
                       val market: String,
                       val stocks: List<StockDto>,
                       val exchangeName: String? = null)

data class PositionDto(val id: Long? = null,
                       val stock: StockDetailsDto,
                       val transactions: List<TransactionDto>,
                       val dividends: List<DividendDto>,
                       val description: String? = null,
                       val value: BigDecimal? = null)

data class TransactionDto(val id: Long? = null,
                          val amount: Int,
                          val purchasePrice: Double,
                          val fees: Double = 0.0,
                          val date: LocalDate)

data class DividendDto(val id: Long? = null,
                       val amount: Double,
                       val date: LocalDate)