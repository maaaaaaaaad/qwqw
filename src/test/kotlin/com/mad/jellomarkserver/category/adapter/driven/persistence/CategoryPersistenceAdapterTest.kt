package com.mad.jellomarkserver.category.adapter.driven.persistence

import com.mad.jellomarkserver.category.adapter.driven.persistence.mapper.CategoryMapperImpl
import com.mad.jellomarkserver.category.adapter.driven.persistence.repository.CategoryJpaRepository
import com.mad.jellomarkserver.category.core.domain.model.Category
import com.mad.jellomarkserver.category.core.domain.model.CategoryId
import com.mad.jellomarkserver.category.core.domain.model.CategoryName
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles
import java.time.Clock
import java.time.Instant
import java.time.ZoneId

@DataJpaTest
@ActiveProfiles("test")
class CategoryPersistenceAdapterTest {

    @Autowired
    lateinit var categoryJpaRepository: CategoryJpaRepository

    private lateinit var adapter: CategoryPersistenceAdapter

    private val fixedClock = Clock.fixed(
        Instant.parse("2024-01-15T10:00:00Z"),
        ZoneId.of("UTC")
    )

    @BeforeEach
    fun setup() {
        adapter = CategoryPersistenceAdapter(categoryJpaRepository, CategoryMapperImpl())
        categoryJpaRepository.deleteAll()
    }

    @Test
    fun `should save and retrieve category`() {
        val category = Category.create(
            name = CategoryName.of("네일"),
            clock = fixedClock
        )

        val saved = adapter.save(category)

        assertThat(saved.id.value).isEqualTo(category.id.value)
        assertThat(saved.name.value).isEqualTo("네일")
    }

    @Test
    fun `should find category by id`() {
        val category = Category.create(
            name = CategoryName.of("속눈썹"),
            clock = fixedClock
        )
        adapter.save(category)

        val found = adapter.findById(category.id)

        assertThat(found).isNotNull
        assertThat(found!!.name.value).isEqualTo("속눈썹")
    }

    @Test
    fun `should return null when category not found`() {
        val found = adapter.findById(CategoryId.new())

        assertThat(found).isNull()
    }

    @Test
    fun `should find all categories`() {
        val categories = listOf("네일", "속눈썹", "왁싱").map {
            Category.create(name = CategoryName.of(it), clock = fixedClock)
        }
        categories.forEach { adapter.save(it) }

        val found = adapter.findAll()

        assertThat(found).hasSize(3)
        assertThat(found.map { it.name.value }).containsExactlyInAnyOrder("네일", "속눈썹", "왁싱")
    }

    @Test
    fun `should find categories by ids`() {
        val categories = listOf("네일", "속눈썹", "왁싱", "피부관리").map {
            Category.create(name = CategoryName.of(it), clock = fixedClock)
        }
        categories.forEach { adapter.save(it) }

        val idsToFind = listOf(categories[0].id, categories[2].id)
        val found = adapter.findByIds(idsToFind)

        assertThat(found).hasSize(2)
        assertThat(found.map { it.name.value }).containsExactlyInAnyOrder("네일", "왁싱")
    }

    @Test
    fun `should return empty list when finding by non-existent ids`() {
        val found = adapter.findByIds(listOf(CategoryId.new(), CategoryId.new()))

        assertThat(found).isEmpty()
    }
}
