package com.mauquoi.stockinformation.domain.repository

import com.mauquoi.stockinformation.domain.model.entity.Stock
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface StockRepository: JpaRepository<Stock, Long> {
    fun findByLookup(createLookup: String): Optional<Stock>
    fun findAllByMarket(market: String): Set<Stock>
    fun findTop10ByUpdatableIsTrueOrderByLastUpdateAsc(): List<Stock>
}