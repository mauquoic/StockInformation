package com.mauquoi.stockinformation.domain.service

import com.mauquoi.stockinformation.StockNotFoundException
import com.mauquoi.stockinformation.domain.model.entity.Exchange
import com.mauquoi.stockinformation.domain.model.entity.Stock
import com.mauquoi.stockinformation.domain.repository.StockRepository
import com.mauquoi.stockinformation.gateway.finnhub.FinnhubGateway
import com.mauquoi.stockinformation.util.TestObjectCreator
import com.mauquoi.stockinformation.util.TestObjectCreator.usd
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.slot
import io.mockk.verify
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*

@ExtendWith(MockKExtension::class)
internal class StockServiceTest {

    @MockK
    private lateinit var stockRepository: StockRepository

    @MockK
    private lateinit var finnhubGateway: FinnhubGateway

    private lateinit var stockService: StockService
    private val capturedStockId = slot<Long>()
    private val capturedStock = slot<Stock>()
    private val capturedShortForm = slot<String>()
    private val capturedExchange = slot<String>()

    @BeforeEach
    fun setUp() {
        clearAllMocks()
        stockService = StockService(stockRepository, finnhubGateway)
    }

    @Test
    fun getAccount_stockDoesNotExist_errorThrown() {
        every { stockRepository.findById(any()) } returns Optional.empty()

        val err = assertThrows<StockNotFoundException> { stockService.getStock(1L) }

        assertThat(err.localizedMessage, CoreMatchers.`is`("No stock could be found by that ID."))
    }

    @Test
    fun getStockPrice() {
        every { finnhubGateway.getStockPrice(capture(capturedShortForm)) } returns TestObjectCreator.createQuoteDto()
        stockService.getStockPrice("ACN")
        assertThat(capturedShortForm.captured, `is`("ACN"))
    }

    @Test
    fun getStockName_swissMarket_lookupDoneCorrectly() {

        every { finnhubGateway.getExchange(capture(capturedExchange)) } returns TestObjectCreator.createExchange()

        val stockName = stockService.getStockName("GEBN", "SW")

        assertAll(
                { assertThat(stockName.name, `is`("Geberit")) },
                { assertThat(capturedExchange.captured, `is`("SW")) }
        )
    }

    @Test
    fun getStockName_usMarket_lookupDoneCorrectly() {

        every { finnhubGateway.getExchange(capture(capturedExchange)) } returns TestObjectCreator.createExchange()

        val stockName = stockService.getStockName("ACN", "US")

        assertAll(
                { assertThat(stockName.name, `is`("Accenture")) },
                { assertThat(capturedExchange.captured, `is`("US")) }
        )
    }

    @Test
    fun getStock_stockExists() {
        every { stockRepository.findById(capture(capturedStockId)) } returns Optional.of(TestObjectCreator.createUsStock())
        val stock = stockService.getStock(1L)

        assertAll(
                { assertThat(stock.name, `is`("Accenture")) },
                { assertThat(capturedStockId.captured, `is`(1L)) }
        )
    }

    @Test
    fun getStock_stockDoesNotExist_errorIsThrown() {
        every { stockRepository.findById(capture(capturedStockId)) } returns Optional.empty()

        assertThrows<StockNotFoundException> { stockService.getStock(1L) }
    }

    @Test
    fun updateStockExchange_nonUS_lookupContainsMarket() {
        every { finnhubGateway.getExchange(capture(capturedExchange)) } returns TestObjectCreator.createExchange()
        every { stockRepository.save(capture(capturedStock)) } returns TestObjectCreator.createChStock()
        every { stockRepository.findByLookup(any()) }.returnsMany(
                Optional.of(TestObjectCreator.createChStock()),
                Optional.empty(),
                Optional.of(TestObjectCreator.createChStock()))

        stockService.updateStockExchange("SW")

        assertAll(
                { assertThat(capturedExchange.captured, `is`("SW")) },
                { assertThat(capturedStock.captured.name, `is`("Geberit")) },
                { assertThat(capturedStock.captured.lookup, `is`("GEBN.SW")) }
        )
    }

    @Test
    fun updateStockExchange_duplicatesAreFilteredOut_usMarket_lookupDoesNotContainMarket() {
        val exchange = Exchange(listOf(
                Stock(name = "Accenture", symbol = "ACN", currency = usd(), type = "DS", market = "US"),
                Stock(name = "Accenture", symbol = "ACN", currency = usd(), type = "DEQ", market = "US"),
                Stock(name = "Agriculture Something", symbol = "AGM.A", currency = usd(), type = "ETF", market = "US")
        ))

        every { finnhubGateway.getExchange(capture(capturedExchange)) } returns exchange
        every { stockRepository.save(capture(capturedStock)) } returns TestObjectCreator.createChStock()
        every { stockRepository.findByLookup(any()) }.returnsMany(
                Optional.of(TestObjectCreator.createChStock()),
                Optional.empty())

        stockService.updateStockExchange("US")

        verify(exactly = 2) { stockRepository.findByLookup(any()) }
        assertAll(
                { assertThat(capturedExchange.captured, `is`("US")) },
                { assertThat(capturedStock.captured.name, `is`("Agriculture Something")) },
                { assertThat(capturedStock.captured.lookup, `is`("AGM.A")) }
        )
    }
}