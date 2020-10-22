package com.mauquoi.stockinformation.domain.service

import com.mauquoi.stockinformation.MarketCurrencyMismatchException
import com.mauquoi.stockinformation.MarketNotFoundException
import com.mauquoi.stockinformation.gateway.ecb.CurrencyConfiguration
import com.mauquoi.stockinformation.gateway.ecb.EcbGateway
import com.mauquoi.stockinformation.gateway.finnhub.MarketConfiguration
import com.mauquoi.stockinformation.util.TestObjectCreator
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.slot
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.hamcrest.core.Is
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*

@ExtendWith(MockKExtension::class)
internal class CurrencyServiceTest {

    private lateinit var currencyService: CurrencyService

    @MockK
    lateinit var ecbGateway: EcbGateway

    private val capturedCurrency = slot<Currency>()
    val usd = Currency.getInstance("USD")

    @BeforeEach
    fun setUp() {
        clearAllMocks()
        currencyService = CurrencyService(ecbGateway, CurrencyConfiguration().supportedCurrencies(), MarketConfiguration().markets())
    }

    @Test
    fun getRates() {
        every { ecbGateway.getConversionValues(capture(capturedCurrency)) } returns TestObjectCreator.createCurrencyLookup()

        val rates = currencyService.getRates(usd)

        org.junit.jupiter.api.assertAll(
                { MatcherAssert.assertThat(capturedCurrency.captured.currencyCode, Is.`is`("USD")) },
                { MatcherAssert.assertThat(rates.rates.size, Is.`is`(2)) }

        )
    }

    @Test
    fun getCurrencies() {
        val currencies = currencyService.getCurrencies()

        MatcherAssert.assertThat(currencies.size > 5, Is.`is`(true))
    }


    @Test
    fun getCurrencyForMarket_marketFound_currencyReturned() {
        MatcherAssert.assertThat(currencyService.getCurrencyForMarket("US"), Is.`is`(usd))
    }

    @Test
    fun getCurrencyForMarket_marketNotFound_errorThrown() {
        val err = org.junit.jupiter.api.assertThrows<MarketNotFoundException> { currencyService.getCurrencyForMarket("USS") }

        MatcherAssert.assertThat(err.localizedMessage, CoreMatchers.`is`("No market could be found by ID USS."))
    }

    @Test
    fun verifyCurrencyCompatibility_marketNotFound_errorThrown() {
        val err = org.junit.jupiter.api.assertThrows<MarketNotFoundException> { currencyService.verifyCurrencyCompatibility("USS", usd) }

        MatcherAssert.assertThat(err.localizedMessage, CoreMatchers.`is`("No market could be found by ID USS."))
    }

    @Test
    fun verifyCurrencyCompatibility_mismatch_errorThrown() {
        val err = org.junit.jupiter.api.assertThrows<MarketCurrencyMismatchException> { currencyService.verifyCurrencyCompatibility("SW", usd) }

        MatcherAssert.assertThat(err.localizedMessage, CoreMatchers.`is`("The market SW is not traded in USD."))
    }

    @Test
    fun verifyCurrencyCompatibility_match_noErrorThrown() {
        currencyService.verifyCurrencyCompatibility("US", usd)
    }
}