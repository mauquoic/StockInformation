package com.mauquoi.stockinformation.controller

import com.mauquoi.stockinformation.const.URL.Stubs.WINNERS_LOSERS
import com.mauquoi.stockinformation.domain.service.StockSchedulingService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import javax.inject.Inject

@RestController
class StubController @Inject constructor(private val stockSchedulingService: StockSchedulingService?) {

    @GetMapping(WINNERS_LOSERS)
    fun calculateWinnersAndLosers() {
        stockSchedulingService?.findWinnersAndLosers()
    }
}