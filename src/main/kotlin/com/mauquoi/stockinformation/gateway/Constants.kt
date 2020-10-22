package com.mauquoi.stockinformation.gateway

object Constants {

    object Endpoint {
        const val QUOTE = "/quote"
        const val SYMBOL = "/stock/symbol"
        const val CANDLES = "/stock/candle"
    }

    object Path {
        const val DATE = "date"
        const val LATEST = "latest"
    }

    object Query {
        const val BASE = "base"
        const val SYMBOL = "symbol"
        const val TOKEN = "token"
        const val RESOLUTION = "resolution"
        const val FROM = "from"
        const val TO = "to"
        const val EXCHANGE = "exchange"
    }
}