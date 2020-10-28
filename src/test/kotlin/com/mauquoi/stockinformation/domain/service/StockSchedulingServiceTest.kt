package com.mauquoi.stockinformation.domain.service

import com.mauquoi.stockinformation.domain.model.Market
import com.mauquoi.stockinformation.domain.model.entity.Stock
import com.mauquoi.stockinformation.domain.model.entity.StockHistory
import com.mauquoi.stockinformation.domain.repository.StockHistoryRepository
import com.mauquoi.stockinformation.domain.repository.StockRepository
import com.mauquoi.stockinformation.gateway.finnhub.MarketConfiguration
import com.mauquoi.stockinformation.util.TestObjectCreator
import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.HttpStatus
import org.springframework.web.client.HttpClientErrorException
import java.lang.RuntimeException
import java.time.LocalDate

@ExtendWith(MockKExtension::class)
internal class StockSchedulingServiceTest {

    private lateinit var stockSchedulingService: StockSchedulingService

    @MockK
    private lateinit var stockService: StockService

    @MockK
    private lateinit var stockRepository: StockRepository

    @MockK
    private lateinit var stockHistoryRepository: StockHistoryRepository
    private val markets: List<Market> = MarketConfiguration().markets()

    private var storedStockSlot = slot<Stock>()
    private var stockHistorySlot = slot<List<StockHistory>>()
    private var dateCaptor = slot<LocalDate>()

    @BeforeEach
    fun setUp() {
        stockSchedulingService = StockSchedulingService(stockService, stockRepository, stockHistoryRepository, markets)
    }

    @Test
    fun updateMarkets() {
        every { stockService.updateStockExchange(any()) } just runs

        stockSchedulingService.updateMarkets()

        markets.forEach { verify { stockService.updateStockExchange(it.market) } }
    }

    @Test
    fun initialization_emptyRepo_runs() {
        every { stockService.updateStockExchange(any()) } just runs
        every { stockRepository.count() } returns 0
        stockSchedulingService.initialization()
        verify(exactly = 62) { stockService.updateStockExchange(any()) }
    }

    @Test
    fun initialization_nonEmptyRepo_doesNotRun() {
        every { stockService.updateStockExchange(any()) } just runs
        every { stockRepository.count() } returns 1
        stockSchedulingService.initialization()
        verify(exactly = 0) { stockService.updateStockExchange(any()) }
    }

    @Test
    fun updateStockValues_happyCase() {
        every { stockRepository.findTop10ByUpdatableIsTrueOrderByLastUpdateAsc() } returns listOf(TestObjectCreator.createUsStock())
        every { stockService.getStockValues(any(), any()) } returns listOf(TestObjectCreator.createStockHistory(id = "ACN", date = LocalDate.now()))
        every { stockRepository.save(capture(storedStockSlot)) } returns TestObjectCreator.createUsStock()
        every { stockHistoryRepository.saveAll(capture(stockHistorySlot)) } returns listOf(TestObjectCreator.createStockHistory())

        stockSchedulingService.updateStockValues()

        verify(exactly = 1) { stockHistoryRepository.saveAll(any()) }
        verify(exactly = 1) { stockRepository.save(any()) }

        assertAll(
                { assertThat(storedStockSlot.captured.lastUpdate).isEqualTo(LocalDate.now()) },
                { assertThat(storedStockSlot.captured.updatable).isTrue() },
                { assertThat(stockHistorySlot.captured[0].id.date).isEqualTo(LocalDate.now()) }
        )
    }

    @Test
    fun updateStockValues_exception_stockIsStoredAsNotUpdatable() {
        every { stockRepository.findTop10ByUpdatableIsTrueOrderByLastUpdateAsc() } returns listOf(TestObjectCreator.createUsStock(lastUpdate = null))
        every { stockService.getStockValues(any(), any()) } throws RuntimeException()
        every { stockRepository.save(capture(storedStockSlot)) } returns TestObjectCreator.createUsStock()

        stockSchedulingService.updateStockValues()

        verify(exactly = 0) { stockHistoryRepository.saveAll(any()) }
        verify(exactly = 1) { stockRepository.save(any()) }

        assertAll(
                { assertThat(storedStockSlot.captured.lastUpdate).isNull() },
                { assertThat(storedStockSlot.captured.updatable).isFalse() }
        )
    }

    @Test
    fun updateStockValues_multipleTimes_correctStartDateIsSet() {
        every { stockRepository.findTop10ByUpdatableIsTrueOrderByLastUpdateAsc() }.returnsMany(
                listOf(TestObjectCreator.createUsStock(lastUpdate = null)),
                listOf(TestObjectCreator.createUsStock(lastUpdate = LocalDate.of(2020, 9, 1)))
        )
        every { stockService.getStockValues(any(), capture(dateCaptor)) }.returnsMany(
                listOf(TestObjectCreator.createStockHistory(id = "ACN", date = LocalDate.of(2020, 9, 1))),
                listOf(TestObjectCreator.createStockHistory(id = "ACN", date = LocalDate.now()))
        )
        every { stockRepository.save(capture(storedStockSlot)) } returns TestObjectCreator.createUsStock()
        every { stockHistoryRepository.saveAll(capture(stockHistorySlot)) } returns listOf(TestObjectCreator.createStockHistory())

        //First update
        stockSchedulingService.updateStockValues()
        assertAll(
                { assertThat(storedStockSlot.captured.lastUpdate).isEqualTo(LocalDate.of(2020, 9, 1)) },
                { assertThat(storedStockSlot.captured.updatable).isTrue() },
                { assertThat(dateCaptor.captured).isEqualTo(LocalDate.now().minusYears(25)) },
                { assertThat(stockHistorySlot.captured[0].id.date).isEqualTo(LocalDate.of(2020, 9, 1)) }
        )

        //Second update
        stockSchedulingService.updateStockValues()
        assertAll(
                { assertThat(storedStockSlot.captured.lastUpdate).isEqualTo(LocalDate.now()) },
                { assertThat(storedStockSlot.captured.updatable).isTrue() },
                { assertThat(dateCaptor.captured).isEqualTo(LocalDate.of(2020, 9, 2)) },
                { assertThat(stockHistorySlot.captured[0].id.date).isEqualTo(LocalDate.now()) }
        )

        verify(exactly = 2) { stockHistoryRepository.saveAll(any()) }
        verify(exactly = 2) { stockRepository.save(any()) }
    }

    @Test
    fun startAndStopUpdates_updateStockValuesReact() {
        every { stockRepository.findTop10ByUpdatableIsTrueOrderByLastUpdateAsc() } returns emptyList()
        verify(exactly = 0) { stockRepository.findTop10ByUpdatableIsTrueOrderByLastUpdateAsc() }

        stockSchedulingService.startUpdates()
        stockSchedulingService.updateStockValues()
        verify(exactly = 1) { stockRepository.findTop10ByUpdatableIsTrueOrderByLastUpdateAsc() }
        stockSchedulingService.stopUpdates()
        stockSchedulingService.updateStockValues()
        verify(exactly = 1) { stockRepository.findTop10ByUpdatableIsTrueOrderByLastUpdateAsc() }
    }

    @Test
    fun tooManyRequests_stockIsNotSetAsNotUpdatable() {
        every { stockRepository.findTop10ByUpdatableIsTrueOrderByLastUpdateAsc() } returns listOf(TestObjectCreator.createChStock())
        every { stockService.getStockValues(any(), any()) } throws HttpClientErrorException.create(HttpStatus.TOO_MANY_REQUESTS,
                "", org.springframework.http.HttpHeaders.EMPTY, byteArrayOf(), null)
        stockSchedulingService.updateStockValues()
        verify(exactly = 0) { stockRepository.save(any()) }
    }


}