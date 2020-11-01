package com.mauquoi.stockinformation.domain.repository

import com.mauquoi.stockinformation.domain.model.entity.Stock
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDate
import java.util.*
import java.util.stream.Stream
import kotlin.streams.toList

interface StockRepository : JpaRepository<Stock, Long> {
    fun findByLookup(createLookup: String): Optional<Stock>
    fun findAllByMarketAndUpdatableIsTrue (market: String): Stream<Stock>
    fun findAllByMarketAndLastUpdateAfterAndUpdatableIsTrue (market: String, lastUpdate: LocalDate = LocalDate.now().minusDays(7)): Stream<Stock>
    fun findTop30ByUpdatableIsTrueOrderByLastUpdateAsc(): List<Stock>

    @JvmDefault
    fun getAllWithWeeklyUpdates(market: String): List<Stock>{
        return findAllByMarketAndLastUpdateAfterAndUpdatableIsTrue(market).toList()
    }
}