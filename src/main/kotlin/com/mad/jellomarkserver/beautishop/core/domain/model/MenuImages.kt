package com.mad.jellomarkserver.beautishop.core.domain.model

class MenuImages private constructor(val values: List<ShopImage>) {
    companion object {
        const val MAX_IMAGES = 3

        fun of(images: List<String>): MenuImages {
            require(images.size <= MAX_IMAGES) { "Maximum $MAX_IMAGES menu images allowed" }
            return MenuImages(images.map { ShopImage.of(it) })
        }

        fun ofNullable(images: List<String>?): MenuImages {
            if (images.isNullOrEmpty()) {
                return empty()
            }
            return of(images)
        }

        fun empty(): MenuImages = MenuImages(emptyList())
    }

    fun toStringList(): List<String> = values.map { it.value }

    fun isEmpty(): Boolean = values.isEmpty()

    fun isNotEmpty(): Boolean = values.isNotEmpty()

    val size: Int get() = values.size

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MenuImages) return false
        return values == other.values
    }

    override fun hashCode(): Int = values.hashCode()
}
