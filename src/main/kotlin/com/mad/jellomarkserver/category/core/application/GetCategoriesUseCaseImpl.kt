package com.mad.jellomarkserver.category.core.application

import com.mad.jellomarkserver.category.core.domain.model.Category
import com.mad.jellomarkserver.category.port.driven.CategoryPort
import com.mad.jellomarkserver.category.port.driving.GetCategoriesUseCase
import org.springframework.stereotype.Service

@Service
class GetCategoriesUseCaseImpl(
    private val categoryPort: CategoryPort
) : GetCategoriesUseCase {

    override fun execute(): List<Category> {
        return categoryPort.findAll()
    }
}
