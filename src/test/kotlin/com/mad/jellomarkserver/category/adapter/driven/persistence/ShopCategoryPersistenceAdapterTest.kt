package com.mad.jellomarkserver.category.adapter.driven.persistence

import com.mad.jellomarkserver.beautishop.core.domain.model.ShopId
import com.mad.jellomarkserver.category.adapter.driven.persistence.entity.CategoryJpaEntity
import com.mad.jellomarkserver.category.adapter.driven.persistence.mapper.CategoryMapperImpl
import com.mad.jellomarkserver.category.adapter.driven.persistence.repository.CategoryJpaRepository
import com.mad.jellomarkserver.category.adapter.driven.persistence.repository.ShopCategoryMappingJpaRepository
import com.mad.jellomarkserver.category.core.domain.model.CategoryId
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles
import java.time.Instant
import java.util.*

@DataJpaTest
@ActiveProfiles("test")
class ShopCategoryPersistenceAdapterTest {

    @Autowired
    lateinit var categoryJpaRepository: CategoryJpaRepository

    @Autowired
    lateinit var shopCategoryMappingJpaRepository: ShopCategoryMappingJpaRepository

    private lateinit var adapter: ShopCategoryPersistenceAdapter

    private val now = Instant.now()

    @BeforeEach
    fun setup() {
        adapter = ShopCategoryPersistenceAdapter(
            shopCategoryMappingJpaRepository,
            categoryJpaRepository,
            CategoryMapperImpl()
        )
        shopCategoryMappingJpaRepository.deleteAll()
        categoryJpaRepository.deleteAll()
    }

    private fun createCategory(name: String): CategoryJpaEntity {
        val entity = CategoryJpaEntity(
            id = UUID.randomUUID(),
            name = name,
            createdAt = now,
            updatedAt = now
        )
        return categoryJpaRepository.save(entity)
    }

    @Test
    fun `should set shop categories`() {
        val shopId = ShopId.new()
        val category1 = createCategory("네일")
        val category2 = createCategory("속눈썹")

        adapter.setShopCategories(
            shopId,
            listOf(CategoryId.from(category1.id), CategoryId.from(category2.id))
        )

        val categories = adapter.findCategoriesByShopId(shopId)
        assertThat(categories).hasSize(2)
        assertThat(categories.map { it.name.value }).containsExactlyInAnyOrder("네일", "속눈썹")
    }

    @Test
    fun `should replace existing categories when setting new ones`() {
        val shopId = ShopId.new()
        val category1 = createCategory("네일")
        val category2 = createCategory("속눈썹")
        val category3 = createCategory("왁싱")

        adapter.setShopCategories(shopId, listOf(CategoryId.from(category1.id)))

        adapter.setShopCategories(
            shopId,
            listOf(CategoryId.from(category2.id), CategoryId.from(category3.id))
        )

        val categories = adapter.findCategoriesByShopId(shopId)
        assertThat(categories).hasSize(2)
        assertThat(categories.map { it.name.value }).containsExactlyInAnyOrder("속눈썹", "왁싱")
    }

    @Test
    fun `should return empty list when shop has no categories`() {
        val shopId = ShopId.new()

        val categories = adapter.findCategoriesByShopId(shopId)

        assertThat(categories).isEmpty()
    }

    @Test
    fun `should add category to shop`() {
        val shopId = ShopId.new()
        val category = createCategory("네일")

        adapter.addCategory(shopId, CategoryId.from(category.id))

        val categories = adapter.findCategoriesByShopId(shopId)
        assertThat(categories).hasSize(1)
        assertThat(categories[0].name.value).isEqualTo("네일")
    }

    @Test
    fun `should not duplicate category when adding same category twice`() {
        val shopId = ShopId.new()
        val category = createCategory("네일")
        val categoryId = CategoryId.from(category.id)

        adapter.addCategory(shopId, categoryId)
        adapter.addCategory(shopId, categoryId)

        val categories = adapter.findCategoriesByShopId(shopId)
        assertThat(categories).hasSize(1)
    }

    @Test
    fun `should remove category from shop`() {
        val shopId = ShopId.new()
        val category1 = createCategory("네일")
        val category2 = createCategory("속눈썹")

        adapter.setShopCategories(
            shopId,
            listOf(CategoryId.from(category1.id), CategoryId.from(category2.id))
        )

        adapter.removeCategory(shopId, CategoryId.from(category1.id))

        val categories = adapter.findCategoriesByShopId(shopId)
        assertThat(categories).hasSize(1)
        assertThat(categories[0].name.value).isEqualTo("속눈썹")
    }

    @Test
    fun `should remove all categories from shop`() {
        val shopId = ShopId.new()
        val category1 = createCategory("네일")
        val category2 = createCategory("속눈썹")

        adapter.setShopCategories(
            shopId,
            listOf(CategoryId.from(category1.id), CategoryId.from(category2.id))
        )

        adapter.removeAllCategories(shopId)

        val categories = adapter.findCategoriesByShopId(shopId)
        assertThat(categories).isEmpty()
    }
}
