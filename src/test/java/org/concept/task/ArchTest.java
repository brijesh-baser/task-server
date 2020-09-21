package org.concept.task;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class ArchTest {

    @Test
    void servicesAndRepositoriesShouldNotDependOnWebLayer() {

        JavaClasses importedClasses = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("org.concept.task");

        noClasses()
            .that()
                .resideInAnyPackage("org.concept.task.service..")
            .or()
                .resideInAnyPackage("org.concept.task.repository..")
            .should().dependOnClassesThat()
                .resideInAnyPackage("..org.concept.task.web..")
        .because("Services and repositories should not depend on web layer")
        .check(importedClasses);
    }
}
