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
        val stocks = (0..35L).map { TestObjectCreator.createUsStock(lastUpdate = LocalDate.now().minusDays(it), symbol = it.toString()) }
        stocks[29].updatable = false
        repository.saveAll(stocks)

        val lastUpdates = repository.findTop30ByUpdatableIsTrueOrderByLastUpdateAsc()

        assertAll(
                { assertThat(lastUpdates.size).isEqualTo(30) },
                { assertThat(lastUpdates[0].lookup).isEqualTo("35") },
                { assertThat(lastUpdates[9].lookup).isEqualTo("25") },
                { assertThat(lastUpdates.none { it.lookup == "29" }).isTrue() },
        )
    }
}