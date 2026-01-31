package com.mad.jellomarkserver.beautishop.core.domain.model

class ShopImages private constructor(val values: List<ShopImage>) {
    companion object {
        const val MAX_IMAGES = 5

        fun of(images: List<String>): ShopImages {
            require(images.size <= MAX_IMAGES) { "Maximum $MAX_IMAGES images allowed" }
            return ShopImages(images.map { ShopImage.of(it) })
        }

        fun ofNullable(images: List<String>?): ShopImages {
            if (images.isNullOrEmpty()) {
                return empty()
            }
            return of(images)
        }

        fun empty(): ShopImages = ShopImages(emptyList())
    }

    fun toStringList(): List<String> = values.map { it.value }

    fun isEmpty(): Boolean = values.isEmpty()

    fun isNotEmpty(): Boolean = values.isNotEmpty()

    val size: Int get() = values.size

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ShopImages) return false
        return values == other.values
    }

    override fun hashCode(): Int = values.hashCode()
}
