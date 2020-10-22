package com.mauquoi.stockinformation.controller

import com.mauquoi.stockinformation.domain.model.Market
import com.mauquoi.stockinformation.domain.service.CurrencyService
import com.mauquoi.stockinformation.domain.service.StockService
import com.mauquoi.stockinformation.gateway.finnhub.MarketConfiguration
import com.mauquoi.stockinformation.util.TestObjectCreator
import com.ninjasquad.springmockk.MockkBean
import io.mockk.*
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.util.*

@WebMvcTest(MarketController::class)
@ActiveProfiles("test")
internal class MarketControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var stockService: StockService

    @MockkBean
    private lateinit var currencyService: CurrencyService

    @TestConfiguration
    class AdditionalConfig {
        @Bean
        fun markets(): List<Market> {
            return MarketConfiguration().markets()
        }
    }

    private val capturedStockSymbol = slot<String>()
    private val capturedMarket = slot<String>()

    @BeforeEach
    fun setUp() {
        clearAllMocks()
    }


    @Test
    fun getStockName() {
        every { stockService.getStockName(capture(capturedStockSymbol), capture(capturedMarket)) } returns TestObjectCreator.createExchange().stocks[0]

        mockMvc.perform(MockMvcRequestBuilders.get("/markets/US/stocks/ACN")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("name", CoreMatchers.`is`("Accenture")))
                .andExpect(MockMvcResultMatchers.jsonPath("market", CoreMatchers.`is`("US")))
                .andExpect(MockMvcResultMatchers.jsonPath("symbol", CoreMatchers.`is`("ACN")))

        org.junit.jupiter.api.assertAll(
                { assertThat(capturedStockSymbol.captured, Matchers.`is`("ACN")) },
                { assertThat(capturedMarket.captured, Matchers.`is`("US")) }
        )
    }

    @Test
    fun getMarket() {
        every { stockService.getStockExchange(capture(capturedMarket)) } returns listOf(TestObjectCreator.createUsStock(), TestObjectCreator.createChStock())
        every { currencyService.getCurrencyForMarket(any()) } returns Currency.getInstance("USD")

        mockMvc.perform(MockMvcRequestBuilders.get("/markets/US/stocks")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("market", CoreMatchers.`is`("US")))
                .andExpect(MockMvcResultMatchers.jsonPath("currency", CoreMatchers.`is`("USD")))
                .andExpect(MockMvcResultMatchers.jsonPath("stocks[0].name", CoreMatchers.`is`("Accenture")))
                .andExpect(MockMvcResultMatchers.jsonPath("stocks[1].name", CoreMatchers.`is`("Geberit")))
                .andExpect(MockMvcResultMatchers.jsonPath("stocks[0].symbol", CoreMatchers.`is`("ACN")))
                .andExpect(MockMvcResultMatchers.jsonPath("stocks[1].symbol", CoreMatchers.`is`("GEBN")))
                .andExpect(MockMvcResultMatchers.jsonPath("stocks[0].lookup", CoreMatchers.`is`("ACN")))
                .andExpect(MockMvcResultMatchers.jsonPath("stocks[1].lookup", CoreMatchers.`is`("GEBN.SW")))
    }

    @Test
    fun getMarkets() {
        mockMvc.perform(MockMvcRequestBuilders.get("/markets")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize<Int>(62)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].market", CoreMatchers.`is`("US")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].description", CoreMatchers.`is`("United States Exchanges")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].market", CoreMatchers.`is`("SW")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].description", CoreMatchers.`is`("Swiss Exchange")))
    }

    @Test
    fun updateEntireMarket() {
        every { stockService.updateStockExchange(capture(capturedMarket)) } just runs

        mockMvc.perform(MockMvcRequestBuilders.put("/markets/SW/stocks")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent)

        assertThat(capturedMarket.captured, Matchers.`is`("SW"))
    }
}