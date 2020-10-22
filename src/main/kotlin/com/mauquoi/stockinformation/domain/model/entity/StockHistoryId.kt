package com.mauquoi.stockinformation.domain.model.entity

import java.io.Serializable
import java.time.LocalDate
import javax.persistence.Embeddable
import javax.validation.constraints.NotNull

@Embeddable
data class StockHistoryId(
        @NotNull
        val stockLookup: String,
        val date: LocalDate
) : Serializable