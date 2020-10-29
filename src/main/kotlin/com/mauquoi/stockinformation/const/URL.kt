package com.mauquoi.stockinformation.const

import com.mauquoi.stockinformation.const.URL.PathVariable.MARKET
import com.mauquoi.stockinformation.const.URL.PathVariable.STOCK_SYMBOL

object URL {

    object Stubs {
        const val WINNERS_LOSERS = "/stubs/performance"
    }

    object Stock {
        const val STOCK_QUOTE = "/stocks/{$STOCK_SYMBOL}"
    }

    object Market {
        const val BASE = "/markets"
        const val STOCK_NAME = "/{$MARKET}/stocks/{$STOCK_SYMBOL}"
        const val STOCKS_BY_MARKET = "/{$MARKET}/stocks"
    }

    object PathVariable {
        const val USER_ID = "userId"
        const val POSITION_ID = "positionId"
        const val ACCOUNT_ID = "accountId"
        const val DEPOSIT_ID = "depositId"
        const val AUDIT_ID = "auditId"
        const val STOCK_SYMBOL = "symbol"
        const val MARKET = "market"
    }

    object QueryParameter {
        const val BASE_CURRENCY = "base-currency"
        const val DATE = "date"
    }
}