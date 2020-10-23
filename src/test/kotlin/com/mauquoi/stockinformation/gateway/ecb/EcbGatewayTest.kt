package com.mauquoi.stockinformation.gateway.ecb

import com.mauquoi.stockinformation.gateway.ecb.dto.CurrencyLookupDto
import com.mauquoi.stockinformation.util.TestObjectCreator
import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate
import java.time.LocalDate
import java.util.*

internal class EcbGatewayTest {

    @MockK
    private lateinit var builder: RestTemplateBuilder

    @MockK
    private lateinit var restTemplate: RestTemplate

    private lateinit var ecbGateway: EcbGateway

    private val capturedUrl = slot<String>()

    @BeforeEach
    fun setUp() {
        clearAllMocks()
        MockKAnnotations.init(this)
        every { builder.build() } returns restTemplate
        ecbGateway = EcbGateway(builder, "baseUrl/{date}")
    }

    @Test
    fun getConversionValues_dateGiven_dateInPath() {
        every { restTemplate.getForEntity(capture(capturedUrl), CurrencyLookupDto::class.java) } returns ResponseEntity.ok(TestObjectCreator.createCurrencyLookupDto())

        ecbGateway.getConversionValues(Currency.getInstance("USD"), LocalDate.of(2020, 1, 18))
        assertAll(
                { assertThat(capturedUrl.captured, `is`("baseUrl/2020-01-18?base=USD")) }
        )
    }

    @Test
    fun getConversionValues_noDateGiven_latestInPath() {
        every { restTemplate.getForEntity(capture(capturedUrl), CurrencyLookupDto::class.java) } returns ResponseEntity.ok(TestObjectCreator.createCurrencyLookupDto())

        ecbGateway.getConversionValues(Currency.getInstance("USD"))
        assertAll(
                { assertThat(capturedUrl.captured, `is`("baseUrl/latest?base=USD")) }
        )
    }
}
