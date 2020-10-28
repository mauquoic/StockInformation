package com.mauquoi.stockinformation.domain.repository

import com.mauquoi.stockinformation.domain.model.entity.StockHistory
import com.mauquoi.stockinformation.domain.model.entity.StockHistoryId
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDate

interface StockHistoryRepository: JpaRepository<StockHistory, StockHistoryId> {

    fun findAllByIdStockLookup(stockLookup: String): List<StockHistory>
    fun findAllByIdStockLookupAndIdDateAfterAndIdDateBefore(stockLookup: String, startDate: LocalDate, endDate: LocalDate = LocalDate.now())
    fun findTopByIdStockLookupOrderByIdDateDesc(stockLookup: String): StockHistory
    fun findAllByIdStockLookupAndIdDateInOrderByIdDateAsc(stockLookup: String, dates: List<LocalDate>): List<StockHistory>

}