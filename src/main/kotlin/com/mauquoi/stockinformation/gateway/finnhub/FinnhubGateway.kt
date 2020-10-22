package com.mauquoi.stockinformation.gateway.finnhub

import com.mauquoi.stockinformation.domain.model.Market
import com.mauquoi.stockinformation.domain.model.entity.Exchange
import com.mauquoi.stockinformation.domain.model.entity.Stock
import com.mauquoi.stockinformation.domain.model.entity.StockHistory
import com.mauquoi.stockinformation.domain.model.entity.StockHistoryId
import com.mauquoi.stockinformation.gateway.Constants
import com.mauquoi.stockinformation.gateway.Constants.Endpoint.CANDLES
import com.mauquoi.stockinformation.gateway.Constants.Endpoint.QUOTE
import com.mauquoi.stockinformation.gateway.Constants.Query.EXCHANGE
import com.mauquoi.stockinformation.gateway.Constants.Query.FROM
import com.mauquoi.stockinformation.gateway.Constants.Query.RESOLUTION
import com.mauquoi.stockinformation.gateway.Constants.Query.SYMBOL
import com.mauquoi.stockinformation.gateway.Constants.Query.TO
import com.mauquoi.stockinformation.gateway.Constants.Query.TOKEN
import com.mauquoi.stockinformation.gateway.finnhub.dto.FinnhubStockDto
import com.mauquoi.stockinformation.gateway.finnhub.dto.QuoteDto
import com.mauquoi.stockinformation.gateway.finnhub.dto.StockHistoryDto
import com.mauquoi.stockinformation.mapping.extension.toDomain
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.cache.annotation.Cacheable
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Component
import org.springframework.web.util.UriComponentsBuilder
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

@Component
class FinnhubGateway @Inject constructor(private val builder: RestTemplateBuilder,
                                         private val markets: List<Market>,
                                         @Value("\${rest.finnhub.url}") private val baseUrl: String,
                                         @Value("\${rest.finnhub.token}") private val token: String) {

    private val zone = ZoneId.systemDefault()

    fun getStockPrice(shortForm: String): QuoteDto {
        val restTemplate = builder.build()
        val url = UriComponentsBuilder.fromUriString("$baseUrl$QUOTE")
                .queryParam(SYMBOL, shortForm)
                .queryParam(TOKEN, token)
                .build()
                .toUriString()
        val stockInfo = restTemplate.getForEntity(url, QuoteDto::class.java).body
        return stockInfo ?: throw RuntimeException("Could not retrieve the stock price")
    }

    fun getStockCandles(stock: Stock, startDate: LocalDate, endDate: LocalDate): List<StockHistory> {
        val restTemplate = builder.build()

        val url = UriComponentsBuilder.fromUriString("$baseUrl$CANDLES")
                .queryParam(SYMBOL, stock.lookup)
                .queryParam(RESOLUTION, "D")
                .queryParam(FROM, startDate.atStartOfDay(zone).toEpochSecond())
                .queryParam(TO, endDate.atStartOfDay(zone).toEpochSecond())
                .queryParam(TOKEN, token)
                .build()
                .toUriString()
        val stockInfo: StockHistoryDto = restTemplate.getForEntity(url, StockHistoryDto::class.java).body!!
        val historyItems = mutableListOf<StockHistory>()
        for (i in stockInfo.closeList.indices) {
            val historyItem: StockHistory = StockHistory(
                    id = StockHistoryId(
                            stockLookup = stock.lookup!!,
                            date = stockInfo.timestamps[i].atZone(zone).toLocalDate()),
                    valueAtClose = stockInfo.closeList[i],
                    valueAtOpen = stockInfo.openList[i],
                    lowestValue = stockInfo.lowList[i],
                    highestValue = stockInfo.highList[i]
            )
            historyItems.add(historyItem)
        }
        return historyItems
    }

    // todo not yet cached correctly
    @Cacheable(value = ["exchangesCache"])
    fun getExchange(exchange: String): Exchange {
        val restTemplate = builder.build()
        val url = UriComponentsBuilder.fromUriString("$baseUrl${Constants.Endpoint.SYMBOL}")
                .queryParam(EXCHANGE, exchange)
                .queryParam(TOKEN, token)
                .build()
                .toUriString()
        val stocks = restTemplate.exchange(url, HttpMethod.GET, null, object : ParameterizedTypeReference<List<FinnhubStockDto>>() {}).body
                ?: throw RuntimeException("Could not retrieve the exchange information")
        return Exchange(stocks.map { it.toDomain(exchange, markets) })
    }

//    todo: Premium feature? News from company
}