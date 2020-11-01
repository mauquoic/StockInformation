package com.mauquoi.stockinformation.domain.repository

import com.mauquoi.stockinformation.util.TestObjectCreator
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDate

@ExtendWith(SpringExtension::class)
@DataJpaTest
internal class StockHistoryRepositoryTest {

    @Autowired
    private lateinit var repository: StockHistoryRepository

    @Test
    internal fun findAllByIdStockLookupAndIdDateIn() {
        val histories = (0..8L).map { TestObjectCreator.createStockHistory(date = LocalDate.now().minusDays(it)) }
        repository.saveAll(histories)

        val week = repository.findAllByIdStockLookupAndIdDateAfterOrderByIdDateAsc("ACN", LocalDate.now().minusDays(6))

        assertAll(
                { assertThat(week[0].id.date).isEqualTo(LocalDate.now().minusDays(5)) },
                { assertThat(week[5].id.date).isEqualTo(LocalDate.now()) },
        )
    }
}