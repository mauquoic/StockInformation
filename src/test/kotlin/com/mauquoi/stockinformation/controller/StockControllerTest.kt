package com.mauquoi.stockinformation.controller

import com.mauquoi.stockinformation.domain.service.StockService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.slot
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@WebMvcTest(StockController::class)
@ActiveProfiles("test")
internal class StockControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var stockService: StockService

    private val capturedStockSymbol = slot<String>()

    @BeforeEach
    fun setUp() {
        clearAllMocks()
    }

    @Test
    fun getStockValue() {
        every { stockService.getStockPrice(capture(capturedStockSymbol)) } returns 34.0

        mockMvc.perform(MockMvcRequestBuilders.get("/stocks/ACN")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("$", CoreMatchers.`is`(34.0)))

        assertAll(
                { assertThat(capturedStockSymbol.captured, `is`("ACN")) }
        )
    }

}