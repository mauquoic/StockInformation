package com.mauquoi.stockinformation.domain.repository

import com.mauquoi.stockinformation.util.TestObjectCreator
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDate

@ExtendWith(SpringExtension::class)
@DataJpaTest
internal class StockRepositoryTest {

    @Autowired
    private lateinit var repository: StockRepository

    @Test
    internal fun findTop10ByUpdatableIsTrueOrderByLastUpdateAsc() {
        val stocks = (0..15L).map { TestObjectCreator.createUsStock(lastUpdate = LocalDate.now().minusDays(it), symbol = it.toString()) }
        stocks[12].updatable = false
        repository.saveAll(stocks)

        val lastUpdates = repository.findTop10ByUpdatableIsTrueOrderByLastUpdateAsc()

        assertAll(
                { assertThat(lastUpdates.size).isEqualTo(10) },
                { assertThat(lastUpdates[0].lookup).isEqualTo("15") },
                { assertThat(lastUpdates[9].lookup).isEqualTo("5") },
                { assertThat(lastUpdates.none { it.lookup == "12" }).isTrue() },
        )
    }
}