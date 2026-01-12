package com.mad.jellomarkserver.category.adapter.driven.persistence.repository

import com.mad.jellomarkserver.category.adapter.driven.persistence.entity.CategoryJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface CategoryJpaRepository : JpaRepository<CategoryJpaEntity, UUID> {
    fun findByName(name: String): CategoryJpaEntity?
}
