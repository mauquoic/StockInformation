package com.mauquoi.stockinformation.gateway.ecb

import com.mauquoi.stockinformation.domain.model.CurrencyLookup
import com.mauquoi.stockinformation.gateway.Constants.Path.DATE
import com.mauquoi.stockinformation.gateway.Constants.Path.LATEST
import com.mauquoi.stockinformation.gateway.Constants.Query.BASE
import com.mauquoi.stockinformation.gateway.ecb.dto.CurrencyLookupDto
import com.mauquoi.stockinformation.mapping.extension.toDomain
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Component
import org.springframework.web.util.UriComponentsBuilder
import java.time.LocalDate
import java.util.*
import javax.inject.Inject

@Component
class EcbGateway @Inject constructor(private val builder: RestTemplateBuilder,
                                     @Value("\${rest.ecb.url}") private val baseUrl: String) {

    @Cacheable(value = ["conversionValuesCache"])
    fun getConversionValues(baseCurrency: Currency, date: LocalDate? = null): CurrencyLookup {
        val restTemplate = builder.build()
        val pathVariables = mapOf(DATE to (date?.toString() ?: LATEST))
        val url = UriComponentsBuilder.fromUriString(baseUrl)
                .queryParam(BASE, baseCurrency)
                .buildAndExpand(pathVariables)
                .toUriString()
        val exchangeRates = restTemplate.getForEntity(url, CurrencyLookupDto::class.java).body
        return exchangeRates?.toDomain() ?: throw RuntimeException("Could not retrieve the conversion values")
    }
}