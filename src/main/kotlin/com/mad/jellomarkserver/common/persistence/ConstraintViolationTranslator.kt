package com.mad.jellomarkserver.common.persistence

import org.springframework.dao.DataIntegrityViolationException

interface ConstraintViolationTranslator {
    fun <T : RuntimeException> translateAndThrow(
        e: DataIntegrityViolationException,
        mappings: Map<String, () -> T>
    ): Nothing

    fun normalizeConstraintName(constraintName: String?): String?
}