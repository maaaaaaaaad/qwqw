package com.mad.jellomarkserver.architecture

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.io.File

@DisplayName("UseCase Test Coverage Verification")
class UseCaseTestCoverageTest {

    companion object {
        private const val BASE_PACKAGE = "com.mad.jellomarkserver"
        private const val MAIN_SRC = "src/main/kotlin/com/mad/jellomarkserver"
        private const val TEST_SRC = "src/test/kotlin/com/mad/jellomarkserver"

        private val domains = listOf("auth", "member", "owner", "beautishop", "review", "category")
    }

    @Test
    @DisplayName("Every UseCaseImpl should have a corresponding unit test")
    fun everyUseCaseImplShouldHaveTest() {
        val missingTests = mutableListOf<String>()

        domains.forEach { domain ->
            val applicationDir = File("$MAIN_SRC/$domain/core/application")
            if (applicationDir.exists()) {
                applicationDir.listFiles()
                    ?.filter { it.name.endsWith("UseCaseImpl.kt") }
                    ?.forEach { useCaseFile ->
                        val testFileName = useCaseFile.name.replace(".kt", "Test.kt")
                        val testFile = File("$TEST_SRC/$domain/core/application/$testFileName")
                        if (!testFile.exists()) {
                            missingTests.add("$domain: ${useCaseFile.name} -> missing $testFileName")
                        }
                    }
            }
        }

        assertTrue(missingTests.isEmpty()) {
            "Missing UseCase unit tests:\n${missingTests.joinToString("\n") { "  - $it" }}"
        }
    }

    @Test
    @DisplayName("Every PersistenceAdapter should have a corresponding integration test")
    fun everyPersistenceAdapterShouldHaveTest() {
        val missingTests = mutableListOf<String>()

        domains.forEach { domain ->
            val repositoryDir = File("$MAIN_SRC/$domain/adapter/driven/persistence/repository")
            if (repositoryDir.exists()) {
                repositoryDir.listFiles()
                    ?.filter { it.name.endsWith("PersistenceAdapter.kt") }
                    ?.forEach { adapterFile ->
                        val testFileName = adapterFile.name.replace(".kt", "Test.kt")
                        val testFile = File("$TEST_SRC/$domain/adapter/driven/persistence/repository/$testFileName")
                        if (!testFile.exists()) {
                            missingTests.add("$domain: ${adapterFile.name} -> missing $testFileName")
                        }
                    }
            }
        }

        assertTrue(missingTests.isEmpty()) {
            "Missing PersistenceAdapter integration tests:\n${missingTests.joinToString("\n") { "  - $it" }}"
        }
    }

    @Test
    @DisplayName("Every Mapper should have a corresponding unit test")
    fun everyMapperShouldHaveTest() {
        val missingTests = mutableListOf<String>()

        domains.forEach { domain ->
            val mapperDir = File("$MAIN_SRC/$domain/adapter/driven/persistence/mapper")
            if (mapperDir.exists()) {
                mapperDir.listFiles()
                    ?.filter { it.name.endsWith("MapperImpl.kt") }
                    ?.forEach { mapperFile ->
                        val testFileName = mapperFile.name.replace(".kt", "Test.kt")
                        val testFile = File("$TEST_SRC/$domain/adapter/driven/persistence/mapper/$testFileName")
                        if (!testFile.exists()) {
                            missingTests.add("$domain: ${mapperFile.name} -> missing $testFileName")
                        }
                    }
            }
        }

        assertTrue(missingTests.isEmpty()) {
            "Missing Mapper unit tests:\n${missingTests.joinToString("\n") { "  - $it" }}"
        }
    }
}
