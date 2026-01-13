package com.mad.jellomarkserver.architecture

import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("Hexagonal Architecture Rules")
class HexagonalArchitectureTest {

    companion object {
        private const val BASE_PACKAGE = "com.mad.jellomarkserver"
        private val importedClasses = ClassFileImporter()
            .withImportOption(ImportOption.DoNotIncludeTests())
            .importPackages(BASE_PACKAGE)

        private val domainPackages = listOf("auth", "member", "owner", "beautishop", "review", "category")
    }

    @Nested
    @DisplayName("API Gateway Pattern")
    inner class ApiGatewayPatternTests {

        @Test
        @DisplayName("HTTP layer (web) should only exist in apigateway package")
        fun httpLayerOnlyInApiGateway() {
            domainPackages.forEach { domain ->
                val webPackage = "$BASE_PACKAGE.$domain.adapter.driving.web"
                val classesInWebPackage = importedClasses.stream()
                    .filter { it.packageName.startsWith(webPackage) }
                    .toList()

                assertTrue(classesInWebPackage.isEmpty()) {
                    "Found HTTP layer classes in $domain domain: ${classesInWebPackage.map { it.simpleName }}"
                }
            }
        }

        @Test
        @DisplayName("Controllers should only exist in apigateway package")
        fun controllersOnlyInApiGateway() {
            val controllersOutsideApiGateway = importedClasses.stream()
                .filter { it.simpleName.endsWith("Controller") }
                .filter { !it.packageName.startsWith("$BASE_PACKAGE.apigateway") }
                .toList()

            assertTrue(controllersOutsideApiGateway.isEmpty()) {
                "Found Controllers outside apigateway: ${controllersOutsideApiGateway.map { it.name }}"
            }
        }

        @Test
        @DisplayName("Response DTOs should only exist in apigateway package (excluding external API responses)")
        fun responseDtosOnlyInApiGateway() {
            val responsesOutsideApiGateway = importedClasses.stream()
                .filter { it.simpleName.endsWith("Response") }
                .filter { !it.packageName.startsWith("$BASE_PACKAGE.apigateway") }
                .filter { !it.packageName.contains("adapter.driven") }
                .toList()

            assertTrue(responsesOutsideApiGateway.isEmpty()) {
                "Found Response DTOs outside apigateway: ${responsesOutsideApiGateway.map { it.name }}"
            }
        }
    }

    @Nested
    @DisplayName("Domain Bounded Context Rules")
    inner class BoundedContextTests {

        @Test
        @DisplayName("Domain packages should not depend on apigateway")
        fun domainsShouldNotDependOnApiGateway() {
            val domainCorePackages = domainPackages.map { "$BASE_PACKAGE.$it.core.." }.toTypedArray()

            val rule = noClasses()
                .that().resideInAnyPackage(*domainCorePackages)
                .should().dependOnClassesThat()
                .resideInAPackage("$BASE_PACKAGE.apigateway..")

            rule.check(importedClasses)
        }

        @Test
        @DisplayName("Domain core should not depend on persistence adapter")
        fun domainCoreShouldNotDependOnPersistenceAdapter() {
            val domainCorePackages = domainPackages.map { "$BASE_PACKAGE.$it.core.." }.toTypedArray()
            val persistencePackages =
                domainPackages.map { "$BASE_PACKAGE.$it.adapter.driven.persistence.." }.toTypedArray()

            val rule = noClasses()
                .that().resideInAnyPackage(*domainCorePackages)
                .should().dependOnClassesThat()
                .resideInAnyPackage(*persistencePackages)

            rule.check(importedClasses)
        }
    }

    @Nested
    @DisplayName("Port Interface Rules")
    inner class PortInterfaceTests {

        @Test
        @DisplayName("UseCase interfaces should be in port.driving package")
        fun useCaseInterfacesInDrivingPort() {
            val useCaseInterfacesOutsideDrivingPort = importedClasses.stream()
                .filter { it.simpleName.endsWith("UseCase") }
                .filter { it.isInterface }
                .filter { !it.packageName.contains("port.driving") }
                .toList()

            assertTrue(useCaseInterfacesOutsideDrivingPort.isEmpty()) {
                "Found UseCase interfaces outside port.driving: ${useCaseInterfacesOutsideDrivingPort.map { it.name }}"
            }
        }

        @Test
        @DisplayName("Port interfaces should be in port.driven package")
        fun portInterfacesInDrivenPort() {
            val portInterfacesOutsideDrivenPort = importedClasses.stream()
                .filter { it.simpleName.endsWith("Port") }
                .filter { it.isInterface }
                .filter { !it.packageName.contains("port.driven") }
                .filter { !it.packageName.contains("port.driving") }
                .toList()

            assertTrue(portInterfacesOutsideDrivenPort.isEmpty()) {
                "Found Port interfaces outside port.driven: ${portInterfacesOutsideDrivenPort.map { it.name }}"
            }
        }
    }
}
