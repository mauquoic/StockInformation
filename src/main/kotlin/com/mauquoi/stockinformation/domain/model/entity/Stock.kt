package com.mauquoi.stockinformation.domain.model.entity

import java.time.LocalDate
import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity
@Table(name = "stock")
data class Stock(
        @Id
        @Column(name = "lookup", nullable = false, unique = true)
        @NotNull var lookup: String? = null,
        @Column(name = "name", nullable = false) @NotNull val name: String,
        @Column(name = "symbol") @NotNull val symbol: String,
        @Column(name = "market") @NotNull val market: String,
        @Column(name = "currency", nullable = false) @NotNull val currency: Currency,
        @Column(name = "type") val type: String? = null,
        @Column(name = "remark") val remark: String? = null,
        @Column(name = "lastUpdate") var lastUpdate: LocalDate? = null,
        @Column(name = "updatable") var updatable: Boolean = true,
        @Transient var value: Double = 5.0
) {
    init {
        this.lookup = createLookup()
    }

    fun createLookup(): String {
        return if (market != "US") {
            "$symbol.$market"
        } else {
            symbol
        }
    }

    fun updated(updatedUntil: LocalDate?) {
        this.lastUpdate = updatedUntil ?: LocalDate.now()
    }
}
