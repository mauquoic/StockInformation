package com.mauquoi.stockinformation.domain.model.entity

import java.math.BigDecimal
import java.math.RoundingMode
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "stock_history")
data class StockHistory(

        @EmbeddedId
        val id: StockHistoryId,
        val valueAtClose: BigDecimal,
        val highestValue: BigDecimal,
        val lowestValue: BigDecimal,
        val valueAtOpen: BigDecimal,

        ) {
    fun comparePerformance(other: StockHistory): BigDecimal {
        if (this.id.date > other.id.date) {
            throw RuntimeException("Cannot compare to past.")
        }
        if (this.valueAtOpen.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO
        return (other.valueAtClose - this.valueAtOpen).times(BigDecimal(100))
                .divide(this.valueAtOpen, 2, RoundingMode.HALF_UP)
    }
}