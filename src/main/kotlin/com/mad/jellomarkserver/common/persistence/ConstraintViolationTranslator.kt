package com.mad.jellomarkserver.common.persistence

import org.hibernate.exception.ConstraintViolationException
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Component

@Component
class ConstraintViolationTranslator {

    fun <T : RuntimeException> translateAndThrow(
        e: DataIntegrityViolationException,
        mappings: Map<String, () -> T>
    ): Nothing {
        val cve = e.cause as? ConstraintViolationException
            ?: e.cause?.cause as? ConstraintViolationException

        val rawName = cve?.constraintName
        val normalized = normalizeConstraintName(rawName)

        if (normalized != null) {
            mappings[normalized]?.let { throw it.invoke() }
        }

        val s = (rawName ?: "").lowercase()
        val msg = e.mostSpecificCause.message?.lowercase() ?: ""
        for (key in mappings.keys) {
            val k = key.lowercase()
            if (s.contains(k) || msg.contains(k)) {
                mappings[key]?.let { throw it.invoke() }
            }
        }
        throw e
    }

    private fun normalizeConstraintName(constraintName: String?): String? {
        if (constraintName.isNullOrBlank()) return null
        return constraintName
            .trim()
            .trim('"')
            .lowercase()
            .substringAfterLast('.')
            .substringBefore(' ')
            .replace(Regex("_index_\\d+$"), "")
    }
}
