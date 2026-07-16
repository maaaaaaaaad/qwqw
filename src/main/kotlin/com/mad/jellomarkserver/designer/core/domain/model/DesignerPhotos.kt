package com.mad.jellomarkserver.designer.core.domain.model

import com.mad.jellomarkserver.designer.core.domain.exception.InvalidDesignerPhotosException

class DesignerPhotos private constructor(val values: List<String>) {
    companion object {
        const val MAX_PHOTOS = 5

        fun of(urls: List<String>): DesignerPhotos {
            val cleaned = urls.map { it.trim() }.filter { it.isNotBlank() }
            if (cleaned.size > MAX_PHOTOS) {
                throw InvalidDesignerPhotosException(cleaned.size)
            }
            return DesignerPhotos(cleaned)
        }

        fun ofNullable(urls: List<String>?): DesignerPhotos {
            if (urls.isNullOrEmpty()) {
                return empty()
            }
            return of(urls)
        }

        fun empty(): DesignerPhotos = DesignerPhotos(emptyList())
    }

    fun toStringList(): List<String> = values

    fun isEmpty(): Boolean = values.isEmpty()

    val size: Int get() = values.size

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DesignerPhotos) return false
        return values == other.values
    }

    override fun hashCode(): Int = values.hashCode()
}
