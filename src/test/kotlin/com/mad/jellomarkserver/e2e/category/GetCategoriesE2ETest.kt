package com.mad.jellomarkserver.e2e.category

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.*
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.jdbc.Sql.ExecutionPhase

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(
    scripts = [
        "classpath:sql/truncate-categories.sql",
        "classpath:sql/seed-categories.sql"
    ],
    executionPhase = ExecutionPhase.BEFORE_TEST_METHOD
)
class GetCategoriesE2ETest {

    @LocalServerPort
    private var port: Int = 0

    @Autowired
    lateinit var rest: TestRestTemplate

    private fun url(path: String) = "http://localhost:$port$path"

    @Test
    fun `should return all categories without authentication`() {
        val headers = HttpHeaders().apply { contentType = MediaType.APPLICATION_JSON }

        val response = rest.exchange(
            url("/api/categories"),
            HttpMethod.GET,
            HttpEntity<Any>(headers),
            object : ParameterizedTypeReference<List<Map<String, Any?>>>() {}
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val categories = requireNotNull(response.body)
        assertThat(categories).hasSize(6)

        val categoryNames = categories.map { it["name"] as String }
        assertThat(categoryNames).containsExactlyInAnyOrder(
            "네일", "속눈썹", "왁싱", "피부관리", "태닝", "발관리"
        )
    }

    @Test
    fun `should return category with id and timestamps`() {
        val headers = HttpHeaders().apply { contentType = MediaType.APPLICATION_JSON }

        val response = rest.exchange(
            url("/api/categories"),
            HttpMethod.GET,
            HttpEntity<Any>(headers),
            object : ParameterizedTypeReference<List<Map<String, Any?>>>() {}
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val categories = requireNotNull(response.body)

        val firstCategory = categories.first()
        assertThat(firstCategory["id"]).isNotNull()
        assertThat(firstCategory["name"]).isNotNull()
        assertThat(firstCategory["createdAt"]).isNotNull()
        assertThat(firstCategory["updatedAt"]).isNotNull()
    }
}
