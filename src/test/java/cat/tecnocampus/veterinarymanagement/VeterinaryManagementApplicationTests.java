package cat.tecnocampus.veterinarymanagement;

import cat.tecnocampus.veterinarymanagement.domain.VisitsDomainTests;
import cat.tecnocampus.veterinarymanagement.service.*;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 * Main test suite that runs all service tests.
 * This class serves as a test suite that executes all included service test classes.
 * To run all tests: mvn test -Dtest=VeterinaryManagementApplicationTests
 */
@Suite
@SelectClasses({
        VeterinariansServiceTest.class,
        MedicationsServiceTest.class,
        PromotionsServiceTest.class,
        VisitsServiceTest.class,
        VisitsDomainTests.class
})
class VeterinaryManagementApplicationTests {
}
