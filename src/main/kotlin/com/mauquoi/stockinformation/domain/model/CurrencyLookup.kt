package com.mauquoi.stockinformation.domain.model

import java.io.Serializable
import java.time.LocalDate
import java.util.*

data class CurrencyLookup(val base: Currency,
                          val date: LocalDate,
                          val rates: Map<Currency, Double>) : Serializable