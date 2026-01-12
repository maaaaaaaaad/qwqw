package com.mad.jellomarkserver.beautishop.core.domain.model

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class AverageRatingTest {

    @Test
    fun `should create AverageRating with valid value`() {
        val rating = AverageRating.of(4.5)

        assertThat(rating.value).isEqualTo(4.5)
    }

    @Test
    fun `should create AverageRating with zero`() {
        val rating = AverageRating.of(0.0)

        assertThat(rating.value).isEqualTo(0.0)
    }

    @Test
    fun `should create AverageRating with maximum value`() {
        val rating = AverageRating.of(5.0)

        assertThat(rating.value).isEqualTo(5.0)
    }

    @Test
    fun `should create zero rating using factory method`() {
        val rating = AverageRating.zero()

        assertThat(rating.value).isEqualTo(0.0)
    }

    @ParameterizedTest
    @ValueSource(doubles = [1.0, 2.5, 3.33, 4.0, 4.99])
    fun `should create AverageRating with various valid values`(value: Double) {
        val rating = AverageRating.of(value)

        assertThat(rating.value).isEqualTo(value)
    }

    @Test
    fun `should throw exception when rating is negative`() {
        assertThatThrownBy { AverageRating.of(-0.1) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("0.0")
            .hasMessageContaining("5.0")
    }

    @Test
    fun `should throw exception when rating exceeds maximum`() {
        assertThatThrownBy { AverageRating.of(5.1) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("0.0")
            .hasMessageContaining("5.0")
    }

    @Test
    fun `should be equal when values are same`() {
        val rating1 = AverageRating.of(4.5)
        val rating2 = AverageRating.of(4.5)

        assertThat(rating1).isEqualTo(rating2)
    }
}
