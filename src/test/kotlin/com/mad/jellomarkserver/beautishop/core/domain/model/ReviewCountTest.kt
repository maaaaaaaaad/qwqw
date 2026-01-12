package com.mad.jellomarkserver.beautishop.core.domain.model

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class ReviewCountTest {

    @Test
    fun `should create ReviewCount with valid value`() {
        val count = ReviewCount.of(10)

        assertThat(count.value).isEqualTo(10)
    }

    @Test
    fun `should create ReviewCount with zero`() {
        val count = ReviewCount.of(0)

        assertThat(count.value).isEqualTo(0)
    }

    @Test
    fun `should create zero count using factory method`() {
        val count = ReviewCount.zero()

        assertThat(count.value).isEqualTo(0)
    }

    @ParameterizedTest
    @ValueSource(ints = [1, 5, 100, 1000, Int.MAX_VALUE])
    fun `should create ReviewCount with various valid values`(value: Int) {
        val count = ReviewCount.of(value)

        assertThat(count.value).isEqualTo(value)
    }

    @Test
    fun `should throw exception when count is negative`() {
        assertThatThrownBy { ReviewCount.of(-1) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("negative")
    }

    @Test
    fun `should be equal when values are same`() {
        val count1 = ReviewCount.of(5)
        val count2 = ReviewCount.of(5)

        assertThat(count1).isEqualTo(count2)
    }
}
