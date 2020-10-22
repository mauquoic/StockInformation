package com.mauquoi.stockinformation.domain.model.entity

import java.math.BigDecimal
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
        val valueAtOpen: BigDecimal

)