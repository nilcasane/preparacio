package cat.tecnocampus.veterinarymanagement.service;

import cat.tecnocampus.veterinarymanagement.application.VeterinariansService;
import cat.tecnocampus.veterinarymanagement.application.inputDTO.AvailabilityCommand;
import cat.tecnocampus.veterinarymanagement.application.outputDTO.AvailabilityInformation;
import cat.tecnocampus.veterinarymanagement.application.exceptions.AvailabilityDoesNotExistException;
import cat.tecnocampus.veterinarymanagement.application.exceptions.VeterinarianDoesNotExistException;
import cat.tecnocampus.veterinarymanagement.application.outputDTO.VeterinarianDemandInformation;
import cat.tecnocampus.veterinarymanagement.domain.Availability;
import cat.tecnocampus.veterinarymanagement.domain.AvailabilityException;
import cat.tecnocampus.veterinarymanagement.domain.Veterinarian;
import cat.tecnocampus.veterinarymanagement.persistence.AvailabilityRepository;
import cat.tecnocampus.veterinarymanagement.persistence.PersonRepository;
import cat.tecnocampus.veterinarymanagement.application.inputDTO.ExceptionCommand;
import cat.tecnocampus.veterinarymanagement.application.outputDTO.AvailabilityExceptionInformation;
import cat.tecnocampus.veterinarymanagement.application.exceptions.ExceptionDoesNotExistException;
import cat.tecnocampus.veterinarymanagement.persistence.AvailabilityExceptionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.time.format.DateTimeParseException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Sql(scripts = "classpath:cleanup-test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:data-test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class VeterinariansServiceTest {
    @Autowired
    private VeterinariansService veterinariansService;
    @Autowired
    private AvailabilityRepository availabilityRepository;
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private AvailabilityExceptionRepository availabilityExceptionRepository;

    // ========== getAvailabilityById Tests ==========

    @Test
    public void getAvailabilityByExistingIdTest() {
        AvailabilityInformation info = veterinariansService.getAvailabilityById(1L).orElseThrow();
        assertEquals(1L, info.availability_id());
        assertEquals(1, info.day_of_week());
        assertEquals("09:00", info.start_time());
        assertEquals("12:00", info.end_time());
        assertEquals("2025-01-01", info.period_start());
        assertEquals("2025-12-31", info.period_end());
        assertEquals(1L, info.veterinarian_id());
    }

    @Test
    public void getAvailabilityByNonExistingIdTest() {
        assertTrue(veterinariansService.getAvailabilityById(100L).isEmpty());
    }

    // ========== createAvailability Tests ==========

    @Test
    public void createAvailabilityTest() {
        AvailabilityCommand command = new AvailabilityCommand(4, "13:00", "15:00", "2025-04-01", "2025-09-30");

        long before = availabilityRepository.count();
        Long id = veterinariansService.createAvailability(1L, command);
        long after = availabilityRepository.count();

        assertEquals(before + 1, after);
        assertNotNull(id);

        Availability saved = availabilityRepository.findById(id).orElseThrow();
        assertEquals(4, saved.getDayOfWeek());
        assertEquals("13:00", saved.getStartTime().toString());
        assertEquals("15:00", saved.getEndTime().toString());
        assertEquals("2025-04-01", saved.getPeriodStart().toString());
        assertEquals("2025-09-30", saved.getPeriodEnd().toString());
        assertEquals(1L, saved.getVeterinarian().getId());
    }

    @Test
    public void createAvailabilityNonExistingVetTest() {
        AvailabilityCommand command = new AvailabilityCommand(4, "13:00", "15:00", "2025-04-01", "2025-09-30");
        Exception exception = assertThrows(VeterinarianDoesNotExistException.class, () -> {
            veterinariansService.createAvailability(100L, command);
        });

        assertTrue(exception.getMessage().contains("Veterinarian with id 100 does not exist"));
    }

    @Test
    public void createAvailabilityNullVetIdTest() {
        AvailabilityCommand command = new AvailabilityCommand(4, "13:00", "15:00", "2025-04-01", "2025-09-30");
        assertThrows(VeterinarianDoesNotExistException.class, () -> {
            veterinariansService.createAvailability(null, command);
        });
    }

    // ========== getAvailabilities Tests ==========

    @Test
    public void getAvailabilitiesHappyTest() {
        List<AvailabilityInformation> availabilities = veterinariansService.getAvailabilities(1L);
        List<Long> ids = availabilities.stream().map(AvailabilityInformation::availability_id).toList();

        assertEquals(2, availabilities.size());
        assertThat(ids, containsInAnyOrder(1L, 2L));
    }

    @Test
    public void getAvailabilitiesNonExistingVetTest() {
        Exception exception = assertThrows(VeterinarianDoesNotExistException.class, () -> {
            veterinariansService.getAvailabilities(100L);
        });

        assertTrue(exception.getMessage().contains("Veterinarian with id 100 does not exist"));
    }

    // ========== deleteAvailabilities Tests ==========

    @Test
    public void deleteAvailabilitiesHappyTest() {
        // ensure vet 1 has availabilities initially
        Veterinarian vet = personRepository.findVeterinarianById(1L).orElseThrow();
        assertEquals(2, availabilityRepository.findByVeterinarian(vet).size());

        veterinariansService.deleteAvailabilities(1L);

        assertEquals(0, availabilityRepository.findByVeterinarian(vet).size());
    }

    @Test
    public void deleteAvailabilitiesNonExistingVetTest() {
        Exception exception = assertThrows(VeterinarianDoesNotExistException.class, () -> {
            veterinariansService.deleteAvailabilities(100L);
        });

        assertTrue(exception.getMessage().contains("Veterinarian with id 100 does not exist"));
    }

    // ========== updateAvailability Tests ==========

    @Test
    public void updateAvailabilityHappyTest() {
        AvailabilityCommand command = new AvailabilityCommand(5, "07:00", "09:30", "2025-05-01", "2025-08-31");

        AvailabilityInformation updated = veterinariansService.updateAvailability(1L, command);

        assertEquals(1L, updated.availability_id());
        assertEquals(5, updated.day_of_week());
        assertEquals("07:00", updated.start_time());
        assertEquals("09:30", updated.end_time());
        assertEquals("2025-05-01", updated.period_start());
        assertEquals("2025-08-31", updated.period_end());
    }

    @Test
    public void updateAvailabilityNonExistingTest() {
        AvailabilityCommand command = new AvailabilityCommand(5, "07:00", "09:30", "2025-05-01", "2025-08-31");
        Exception exception = assertThrows(AvailabilityDoesNotExistException.class, () -> {
            veterinariansService.updateAvailability(100L, command);
        });

        assertTrue(exception.getMessage().contains("Availability with id 100 does not exist"));
    }

    @Test
    public void updateAvailabilityInvalidCommandTest() {
        // invalid time format will cause DateTimeParseException during update
        AvailabilityCommand invalid = new AvailabilityCommand(5, "invalid", "09:30", "2025-05-01", "2025-08-31");
        assertThrows(DateTimeParseException.class, () -> {
            veterinariansService.updateAvailability(1L, invalid);
        });
    }

    @Test
    public void updateAvailabilityNullIdTest() {
        AvailabilityCommand command = new AvailabilityCommand(5, "07:00", "09:30", "2025-05-01", "2025-08-31");
        assertThrows(IllegalArgumentException.class, () -> {
            veterinariansService.updateAvailability(null, command);
        });
    }

    // ========== deleteAvailability Tests ==========

    @Test
    public void deleteAvailabilityHappyTest() {
        long before = availabilityRepository.count();
        veterinariansService.deleteAvailability(1L);
        long after = availabilityRepository.count();

        assertEquals(before - 1, after);
        assertTrue(availabilityRepository.findById(1L).isEmpty());
    }

    @Test
    public void deleteAvailabilityNonExistingTest() {
        Exception exception = assertThrows(AvailabilityDoesNotExistException.class, () -> {
            veterinariansService.deleteAvailability(100L);
        });

        assertTrue(exception.getMessage().contains("Availability with id 100 does not exist"));
    }

    @Test
    public void deleteAvailabilityNullIdTest() {
        assertThrows(IllegalArgumentException.class, () -> {
            veterinariansService.deleteAvailability(null);
        });
    }
    // ===== Tests AvailabilityException integrados =====
    @Test
    public void createExceptionHappyTest() {
        ExceptionCommand command = new ExceptionCommand("Surgery", 1, "09:00", "10:00", "2025-06-01", "2025-06-30");
        long before = availabilityExceptionRepository.count();
        Long id = veterinariansService.createException(1L, command);
        long after = availabilityExceptionRepository.count();
        assertEquals(before + 1, after);
        assertNotNull(id);
        AvailabilityException saved = availabilityExceptionRepository.findById(id).orElseThrow();
        assertEquals("Surgery", saved.getReason());
        assertEquals(1, saved.getDayOfWeek());
        assertEquals("09:00", saved.getStartTime().toString());
        assertEquals("10:00", saved.getEndTime().toString());
        assertEquals("2025-06-01", saved.getPeriodStart().toString());
        assertEquals("2025-06-30", saved.getPeriodEnd().toString());
        assertEquals(1L, saved.getAvailability().getId());
    }

    @Test
    public void createExceptionNonExistingAvailabilityTest() {
        ExceptionCommand command = new ExceptionCommand("Surgery", 1, "09:00", "10:00", "2025-06-01", "2025-06-30");
        Exception ex = assertThrows(AvailabilityDoesNotExistException.class, () -> veterinariansService.createException(100L, command));
        assertTrue(ex.getMessage().contains("Availability with id 100 does not exist"));
    }

    @Test
    public void createExceptionNullAvailabilityIdTest() {
        ExceptionCommand command = new ExceptionCommand("Surgery", 1, "09:00", "10:00", "2025-06-01", "2025-06-30");
        assertThrows(IllegalArgumentException.class, () -> veterinariansService.createException(null, command));
    }

    @Test
    public void getExceptionByExistingIdTest() {
        ExceptionCommand command = new ExceptionCommand("Meeting", 2, "10:00", "11:00", "2025-07-01", "2025-07-31");
        Long id = veterinariansService.createException(1L, command);
        AvailabilityExceptionInformation info = veterinariansService.getExceptionById(id).orElseThrow();
        assertEquals(id, info.exception_id());
        assertEquals("Meeting", info.reason());
        assertEquals(2, info.day_of_week());
        assertEquals("10:00", info.start_time());
        assertEquals("11:00", info.end_time());
        assertEquals("2025-07-01", info.period_start());
        assertEquals("2025-07-31", info.period_end());
        assertEquals(1L, info.availability_id());
    }

    @Test
    public void getExceptionByNonExistingIdTest() {
        assertTrue(veterinariansService.getExceptionById(999L).isEmpty());
    }

    @Test
    public void getExceptionsHappyTest() {
        ExceptionCommand a = new ExceptionCommand("A", 1, "09:00", "10:00", "2025-05-01", "2025-05-31");
        ExceptionCommand b = new ExceptionCommand("B", 2, "10:00", "11:00", "2025-05-01", "2025-05-31");
        Long idA = veterinariansService.createException(1L, a);
        Long idB = veterinariansService.createException(1L, b);
        List<AvailabilityExceptionInformation> exceptions = veterinariansService.getExceptions(1L);
        List<Long> ids = exceptions.stream().map(AvailabilityExceptionInformation::exception_id).toList();
        assertEquals(2, exceptions.size());
        assertThat(ids, containsInAnyOrder(idA, idB));
    }

    @Test
    public void getExceptionsNonExistingAvailabilityTest() {
        Exception ex = assertThrows(AvailabilityDoesNotExistException.class, () -> veterinariansService.getExceptions(999L));
        assertTrue(ex.getMessage().contains("Availability with id 999 does not exist"));
    }

    @Test
    public void updateExceptionHappyTest() {
        ExceptionCommand create = new ExceptionCommand("Original", 3, "08:00", "09:00", "2025-08-01", "2025-08-31");
        Long id = veterinariansService.createException(1L, create);
        ExceptionCommand update = new ExceptionCommand("Updated", 4, "13:00", "14:30", "2025-09-01", "2025-09-30");
        AvailabilityExceptionInformation updated = veterinariansService.updateException(id, update);
        assertEquals(id, updated.exception_id());
        assertEquals("Updated", updated.reason());
        assertEquals(4, updated.day_of_week());
        assertEquals("13:00", updated.start_time());
        assertEquals("14:30", updated.end_time());
        assertEquals("2025-09-01", updated.period_start());
        assertEquals("2025-09-30", updated.period_end());
    }

    @Test
    public void updateExceptionNonExistingTest() {
        ExceptionCommand update = new ExceptionCommand("X", 1, "09:00", "10:00", "2025-01-01", "2025-01-02");
        Exception ex = assertThrows(ExceptionDoesNotExistException.class, () -> veterinariansService.updateException(999L, update));
        assertTrue(ex.getMessage().contains("Exception with id 999 does not exist"));
    }

    @Test
    public void updateExceptionInvalidCommandTest() {
        ExceptionCommand create = new ExceptionCommand("Orig", 3, "08:00", "09:00", "2025-08-01", "2025-08-31");
        Long id = veterinariansService.createException(1L, create);
        ExceptionCommand invalid = new ExceptionCommand("Bad", 3, "bad-time", "09:00", "2025-08-01", "2025-08-31");
        assertThrows(DateTimeParseException.class, () -> veterinariansService.updateException(id, invalid));
    }

    @Test
    public void updateExceptionNullIdTest() {
        ExceptionCommand update = new ExceptionCommand("X", 1, "09:00", "10:00", "2025-01-01", "2025-01-02");
        assertThrows(IllegalArgumentException.class, () -> veterinariansService.updateException(null, update));
    }

    @Test
    public void deleteExceptionHappyTest() {
        ExceptionCommand create = new ExceptionCommand("ToDelete", 5, "07:00", "08:00", "2025-10-01", "2025-10-31");
        Long id = veterinariansService.createException(1L, create);
        long before = availabilityExceptionRepository.count();
        veterinariansService.deleteException(id);
        long after = availabilityExceptionRepository.count();
        assertEquals(before - 1, after);
        assertTrue(availabilityExceptionRepository.findById(id).isEmpty());
    }

    @Test
    public void deleteExceptionNonExistingTest() {
        Exception ex = assertThrows(ExceptionDoesNotExistException.class, () -> veterinariansService.deleteException(999L));
        assertTrue(ex.getMessage().contains("Exception with id 999 does not exist"));
    }

    @Test
    public void deleteExceptionNullIdTest() {
        assertThrows(IllegalArgumentException.class, () -> veterinariansService.deleteException(null));
    }

    @Test
    public void deleteExceptionsHappyTest() {
        ExceptionCommand a = new ExceptionCommand("One", 1, "09:00", "09:30", "2025-03-01", "2025-03-31");
        ExceptionCommand b = new ExceptionCommand("Two", 2, "10:00", "10:30", "2025-03-01", "2025-03-31");
        veterinariansService.createException(1L, a);
        veterinariansService.createException(1L, b);
        Availability availability = availabilityRepository.findById(1L).orElseThrow();
        assertEquals(2, availabilityExceptionRepository.findByAvailability(availability).size());
        veterinariansService.deleteExceptions(1L);
        assertEquals(0, availabilityExceptionRepository.findByAvailability(availability).size());
    }

    @Test
    public void deleteExceptionsNonExistingAvailabilityTest() {
        Exception ex = assertThrows(AvailabilityDoesNotExistException.class, () -> veterinariansService.deleteExceptions(999L));
        assertTrue(ex.getMessage().contains("Availability with id 999 does not exist"));
    }
    // ===== Fin tests AvailabilityException =====

    // ========== getVeterinariansDemand Tests ==========

    @Test
    public void getVeterinariansDemandHappyTest() {
        List<VeterinarianDemandInformation> result = veterinariansService.getVeterinariansDemand("2025-10-01", "2025-10-31");
        assertEquals(3, result.size());

        // Ordenado por visitas descendente: vet 1 -> 2 visitas, vet 2 -> 1, vet 3 -> 0
        VeterinarianDemandInformation first = result.get(0);
        VeterinarianDemandInformation second = result.get(1);
        VeterinarianDemandInformation third = result.get(2);

        assertEquals(1L, first.veterinarian_id());
        assertEquals(2L, first.scheduled_visits());

        assertEquals(2L, second.veterinarian_id());
        assertEquals(1L, second.scheduled_visits());

        assertEquals(3L, third.veterinarian_id());
        assertEquals(0L, third.scheduled_visits());
    }

    @Test
    public void getVeterinariansDemandInvalidDateFormatTest() {
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                veterinariansService.getVeterinariansDemand("2025-10-01", "31-10-2025")
        );
        assertTrue(ex.getMessage().contains("Invalid date format"));
    }

    @Test
    public void getVeterinariansDemandInvalidRangeTest() {
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                veterinariansService.getVeterinariansDemand("2025-10-31", "2025-10-01")
        );
        assertTrue(ex.getMessage().contains("Invalid date range"));
    }

    @Test
    public void getVeterinariansDemandNoVisitsInRangeTest() {
        // Rango fuera de las fechas de visitas de prueba
        List<VeterinarianDemandInformation> result = veterinariansService.getVeterinariansDemand("2025-01-01", "2025-01-02");
        assertEquals(3, result.size());
        // Todos con 0 visitas
        assertEquals(0L, result.get(0).scheduled_visits());
        assertEquals(0L, result.get(1).scheduled_visits());
        assertEquals(0L, result.get(2).scheduled_visits());
    }
}
