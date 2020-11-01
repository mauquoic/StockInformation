package com.mauquoi.stockinformation.gateway.finnhub

import com.mauquoi.stockinformation.gateway.finnhub.dto.FinnhubStockDto
import com.mauquoi.stockinformation.gateway.finnhub.dto.QuoteDto
import com.mauquoi.stockinformation.gateway.finnhub.dto.StockHistoryDto
import com.mauquoi.stockinformation.util.TestObjectCreator
import com.mauquoi.stockinformation.util.TestObjectCreator.createStockDtos
import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate
import java.time.LocalDate

internal class FinnhubGatewayTest {

    @MockK
    private lateinit var builder: RestTemplateBuilder

    @MockK
    private lateinit var restTemplate: RestTemplate

    private lateinit var finnhubGateway: FinnhubGateway

    private val capturedUrl = slot<String>()

    @BeforeEach
    fun setUp() {
        clearAllMocks()
        MockKAnnotations.init(this)
        every { builder.build() } returns restTemplate
        finnhubGateway = FinnhubGateway(builder, MarketConfiguration().markets(), "baseUrl/v1", listOf("token", "token"))
    }

    @Test
    fun getStockPrice() {
        every { restTemplate.getForEntity(capture(capturedUrl), QuoteDto::class.java) } returns ResponseEntity.ok(TestObjectCreator.createQuoteDto())

        finnhubGateway.getStockPrice("ACN")
        assertAll(
                { assertThat(capturedUrl.captured, Matchers.`is`("baseUrl/v1/quote?symbol=ACN&token=token")) }
        )
    }

    @Test
    fun getExchange() {
        every {
            restTemplate.exchange(capture(capturedUrl), HttpMethod.GET,
                    null, object : ParameterizedTypeReference<List<FinnhubStockDto>>() {})
        } returns ResponseEntity.ok(createStockDtos())

        val exchange = finnhubGateway.getExchange("US")
        assertAll(
                { assertThat(capturedUrl.captured, Matchers.`is`("baseUrl/v1/stock/symbol?exchange=US&token=token")) },
                { assertThat(exchange.stocks.size, Matchers.`is`(3)) }
        )
    }

    @Test
    fun getStockCandles() {

        every { restTemplate.getForEntity(capture(capturedUrl), StockHistoryDto::class.java) } returns ResponseEntity.ok(TestObjectCreator.createStockHistoryDto())

        val historyItems = finnhubGateway.getStockCandles(TestObjectCreator.createUsStock(),
                LocalDate.of(2020, 10, 22).minusDays(1),
                LocalDate.of(2020, 10, 22))
        assertAll(
                { assertThat(capturedUrl.captured.contains("baseUrl/v1/stock/candle?symbol=ACN&resolution=D&from="), `is`(true)) },
                { assertThat(historyItems.size, `is`(1)) }
        )
    }
}