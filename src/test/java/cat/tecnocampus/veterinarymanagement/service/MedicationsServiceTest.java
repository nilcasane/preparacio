package cat.tecnocampus.veterinarymanagement.service;

import cat.tecnocampus.veterinarymanagement.application.MedicationsService;
import cat.tecnocampus.veterinarymanagement.application.VisitsService;
import cat.tecnocampus.veterinarymanagement.application.inputDTO.MedicationBatchCommand;
import cat.tecnocampus.veterinarymanagement.application.inputDTO.MedicationPrescriptionCommand;
import cat.tecnocampus.veterinarymanagement.application.outputDTO.MedicationBatchInformation;
import cat.tecnocampus.veterinarymanagement.application.outputDTO.MedicationInformation;
import cat.tecnocampus.veterinarymanagement.application.exceptions.*;
import cat.tecnocampus.veterinarymanagement.domain.LowStockAlert;
import cat.tecnocampus.veterinarymanagement.domain.Medication;
import cat.tecnocampus.veterinarymanagement.domain.MedicationBatch;
import cat.tecnocampus.veterinarymanagement.domain.MedicationIncompatibility;
import cat.tecnocampus.veterinarymanagement.persistence.LowStockAlertRepository;
import cat.tecnocampus.veterinarymanagement.persistence.MedicationBatchRepository;
import cat.tecnocampus.veterinarymanagement.persistence.MedicationIncompatibilityRepository;
import cat.tecnocampus.veterinarymanagement.persistence.MedicationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Sql(scripts = "classpath:cleanup-test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:data-test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class MedicationsServiceTest {
    @Autowired
    private MedicationsService medicationsService;
    @Autowired
    private MedicationRepository medicationRepository;
    @Autowired
    private MedicationBatchRepository medicationBatchRepository;
    @Autowired
    private LowStockAlertRepository lowStockAlertRepository;
    @Autowired
    private MedicationIncompatibilityRepository medicationIncompatibilityRepository;
    @Autowired
    private VisitsService visitsService;

    // ========== getMedicationById Tests ==========

    @Test
    public void getMedicationByExistingIdTest() {
        // data-test.sql inserts medications; first one is Amoxicillin with id 1
        Optional<MedicationInformation> infoOpt = medicationsService.getMedicationById(1L);
        assertTrue(infoOpt.isPresent());
        MedicationInformation info = infoOpt.orElseThrow();

        assertEquals(1L, info.medication_id());
        assertEquals("Amoxicillin", info.name());
        assertEquals("Amoxicillin", info.active_ingredient());
        assertEquals(500, info.dosage_unit());
        assertEquals(5.50, info.unit_price());
        assertEquals(100, info.reorder_threshold());
    }

    @Test
    public void getMedicationByNonExistingIdTest() {
        Optional<MedicationInformation> infoOpt = medicationsService.getMedicationById(999L);
        assertTrue(infoOpt.isEmpty());
    }

    @Test
    public void getMedicationByNullIdTest() {
        // query with null id returns empty Optional from repository
        Optional<MedicationInformation> infoOpt = medicationsService.getMedicationById(null);
        assertTrue(infoOpt.isEmpty());
    }

    // ========== Medication incompatibilities (UC2.11 & UC2.12) Tests ==========

    @Test
    public void createModifyDeleteIncompatibilityHappyPathTest() {
        // Pick two existing medications
        List<Medication> meds = medicationRepository.findAll();
        assertTrue(meds.size() >= 2);
        Medication a = meds.get(0);
        Medication b = meds.get(1);

        // Initially ensure no incompatibility exists
        Optional<MedicationIncompatibility> optBefore = medicationIncompatibilityRepository.findByMedications(a, b);
        assertTrue(optBefore.isEmpty());

        // Create incompatibility via repository
        MedicationIncompatibility mi = new MedicationIncompatibility();
        mi.setMedicationA(a);
        mi.setMedicationB(b);
        mi.setPersistsUntil(LocalDate.now().plusDays(10));
        medicationIncompatibilityRepository.save(mi);

        // Verify saved
        Optional<MedicationIncompatibility> optSaved = medicationIncompatibilityRepository.findByMedications(a, b);
        assertTrue(optSaved.isPresent());
        MedicationIncompatibility saved = optSaved.orElseThrow();
        assertEquals(a.getId(), saved.getMedicationA().getId());
        assertEquals(b.getId(), saved.getMedicationB().getId());

        // Modify persistsUntil
        saved.setPersistsUntil(LocalDate.now().plusDays(30));
        medicationIncompatibilityRepository.save(saved);

        Optional<MedicationIncompatibility> optModified = medicationIncompatibilityRepository.findByMedications(a, b);
        assertTrue(optModified.isPresent());
        assertEquals(LocalDate.now().plusDays(30), optModified.get().getPersistsUntil());

        // Delete incompatibility
        medicationIncompatibilityRepository.delete(optModified.get());
        Optional<MedicationIncompatibility> optAfterDelete = medicationIncompatibilityRepository.findByMedications(a, b);
        assertTrue(optAfterDelete.isEmpty());
    }

    @Test
    public void incompatibilityAlertWhenPrescribingTest() {
        // Ensure we have at least two medications
        List<Medication> meds = medicationRepository.findAll();
        assertTrue(meds.size() >= 2);
        Medication m1 = meds.get(0);
        Medication m2 = meds.get(1);

        // Create an incompatibility between m1 and m2 that persists
        MedicationIncompatibility mi = new MedicationIncompatibility();
        mi.setMedicationA(m1);
        mi.setMedicationB(m2);
        mi.setPersistsUntil(LocalDate.now().plusDays(5));
        medicationIncompatibilityRepository.save(mi);

        // Prepare a visit with an existing prescription for m2 in a recent visit of the same pet
        // Ensure the visit is recent (today) so it is picked up by the incompatibility check (last 30 days)
        visitsService.rescheduleVisit(1L, LocalDate.now().toString(), "10:00", "test");
        visitsService.startVisit(1L);

        MedicationPrescriptionCommand cmd = new MedicationPrescriptionCommand(1, "inst", 1);
        Long prescId = visitsService.addMedicationPrescription(1L, m2.getId(), cmd);
        assertNotNull(prescId);

        // Now try to prescribe m1 to the same visit. Should throw MedicationIncompatibilityExistsException
        assertThrows(MedicationIncompatibilityExistsException.class, () ->
                visitsService.addMedicationPrescription(1L, m1.getId(), new MedicationPrescriptionCommand(1, "x", 1)));

        // Cleanup
        medicationIncompatibilityRepository.findByMedications(m1, m2).ifPresent(medicationIncompatibilityRepository::delete);
    }

    // ========== Medication Batch tests (moved from MedicationBatchesServiceTest) ==========

    @Test
    public void getMedicationBatchByExistingIdTest() {
        MedicationBatchCommand cmd = new MedicationBatchCommand(1001L, "2025-01-01", "2026-01-01", 100, 80, 10.0);
        Long medId = medicationRepository.findAll().getFirst().getId();
        Long id = medicationsService.createMedicationBatch(medId, cmd);

        Optional<MedicationBatchInformation> infoOpt = medicationsService.getMedicationBatchById(id);
        assertTrue(infoOpt.isPresent());
        MedicationBatchInformation info = infoOpt.orElseThrow();

        assertEquals(id, info.batch_id());
        assertEquals(medId, info.medication_id());
        assertEquals(1001L, info.lot_number());
        assertEquals("2025-01-01", info.received_date().toString());
        assertEquals("2026-01-01", info.expiry_date().toString());
        assertEquals(100, info.initial_quantity());
        assertEquals(80, info.current_quantity());
        assertEquals(10.0, info.purchase_price_per_unit());
    }

    @Test
    public void getMedicationBatchByNonExistingIdTest() {
        Optional<MedicationBatchInformation> infoOpt = medicationsService.getMedicationBatchById(9999L);
        assertTrue(infoOpt.isEmpty());
    }

    @Test
    public void createMedicationBatchHappyTest() {
        Long medId = medicationRepository.findAll().getFirst().getId();
        MedicationBatchCommand cmd = new MedicationBatchCommand(2002L, "2025-02-01", "2026-02-01", 50, 50, 5.5);

        long before = medicationBatchRepository.count();
        Long id = medicationsService.createMedicationBatch(medId, cmd);
        long after = medicationBatchRepository.count();

        assertEquals(before + 1, after);
        assertNotNull(id);

        MedicationBatch saved = medicationBatchRepository.findById(id).orElseThrow();
        assertEquals(2002L, saved.getLotNumber());
        assertEquals("2025-02-01", saved.getReceivedDate().toString());
        assertEquals("2026-02-01", saved.getExpiryDate().toString());
        assertEquals(50, saved.getInitialQuantity());
        assertEquals(50, saved.getCurrentQuantity());
        assertEquals(5.5, saved.getPurchasePricePerUnit());
        assertEquals(medId, saved.getMedication().getId());
    }

    @Test
    public void createMedicationBatchNonExistingMedicationTest() {
        MedicationBatchCommand cmd = new MedicationBatchCommand(3003L, "2025-03-01", "2026-03-01", 10, 10, 1.0);
        Exception exception = assertThrows(MedicationDoesNotExistException.class, () -> {
            medicationsService.createMedicationBatch(12345L, cmd);
        });

        assertTrue(exception.getMessage().contains("Medication with id12345 does not exist") || exception.getMessage().contains("Medication with id 12345 does not exist"));
    }

    @Test
    public void createMedicationBatchNullMedicationIdTest() {
        MedicationBatchCommand cmd = new MedicationBatchCommand(4004L, "2025-04-01", "2026-04-01", 5, 5, 2.0);
        assertThrows(InvalidDataAccessApiUsageException.class, () -> {
            medicationsService.createMedicationBatch(null, cmd);
        });
    }

    @Test
    public void getMedicationBatchesHappyTest() {
        Long medId = medicationRepository.findAll().getFirst().getId();

        int existingBatchCount = medicationsService.getMedicationBatches(medId).size();

        MedicationBatchCommand a = new MedicationBatchCommand(5001L, "2025-05-01", "2026-05-01", 20, 20, 3.0);
        MedicationBatchCommand b = new MedicationBatchCommand(5002L, "2025-05-02", "2026-05-02", 30, 30, 4.0);
        Long idA = medicationsService.createMedicationBatch(medId, a);
        Long idB = medicationsService.createMedicationBatch(medId, b);

        List<MedicationBatchInformation> batches = medicationsService.getMedicationBatches(medId);
        List<Long> ids = batches.stream().map(MedicationBatchInformation::batch_id).toList();

        assertEquals(existingBatchCount + 2, batches.size());
        assertTrue(ids.contains(idA));
        assertTrue(ids.contains(idB));
    }

    @Test
    public void getMedicationBatchesNonExistingMedicationTest() {
        Exception exception = assertThrows(MedicationDoesNotExistException.class, () -> {
            medicationsService.getMedicationBatches(88888L);
        });

        assertTrue(exception.getMessage().contains("Medication with id 88888 does not exist") || exception.getMessage().contains("Medication with id88888 does not exist"));
    }

    @Test
    public void updateMedicationBatchHappyTest() {
        Long medId = medicationRepository.findAll().getFirst().getId();
        MedicationBatchCommand create = new MedicationBatchCommand(6001L, "2025-06-01", "2026-06-01", 40, 35, 6.0);
        Long id = medicationsService.createMedicationBatch(medId, create);

        MedicationBatchCommand update = new MedicationBatchCommand(6001L, "2025-06-10", "2026-06-10", 45, 40, 6.5);
        MedicationBatchInformation updated = medicationsService.updateMedicationBatch(id, update);

        assertEquals(id, updated.batch_id());
        assertEquals(6001L, updated.lot_number());
        assertEquals("2025-06-10", updated.received_date().toString());
        assertEquals("2026-06-10", updated.expiry_date().toString());
        assertEquals(45, updated.initial_quantity());
        assertEquals(40, updated.current_quantity());
        assertEquals(6.5, updated.purchase_price_per_unit());
    }

    @Test
    public void updateMedicationBatchNonExistingTest() {
        MedicationBatchCommand update = new MedicationBatchCommand(7001L, "2025-07-01", "2026-07-01", 10, 10, 1.0);
        Exception exception = assertThrows(MedicationBatchDoesNotExistException.class, () -> {
            medicationsService.updateMedicationBatch(999999L, update);
        });

        assertTrue(exception.getMessage().contains("Medication batch with id 999999 does not exist"));
    }

    @Test
    public void updateMedicationBatchInvalidCommandTest() {
        Long medId = medicationRepository.findAll().getFirst().getId();
        MedicationBatchCommand create = new MedicationBatchCommand(8001L, "2025-08-01", "2026-08-01", 15, 15, 2.5);
        Long id = medicationsService.createMedicationBatch(medId, create);

        MedicationBatchCommand invalid = new MedicationBatchCommand(8001L, "invalid", "2026-08-01", 15, 15, 2.5);
        assertThrows(DateTimeParseException.class, () -> {
            medicationsService.updateMedicationBatch(id, invalid);
        });
    }

    @Test
    public void updateMedicationBatchNullIdTest() {
        MedicationBatchCommand update = new MedicationBatchCommand(9001L, "2025-09-01", "2026-09-01", 5, 5, 1.5);
        assertThrows(InvalidDataAccessApiUsageException.class, () -> {
            medicationsService.updateMedicationBatch(null, update);
        });
    }

    @Test
    public void deleteMedicationBatchHappyTest() {
        Long medId = medicationRepository.findAll().getFirst().getId();
        MedicationBatchCommand cmd = new MedicationBatchCommand(10001L, "2025-10-01", "2026-10-01", 60, 60, 7.0);
        Long id = medicationsService.createMedicationBatch(medId, cmd);

        long before = medicationBatchRepository.count();
        medicationsService.deleteMedicationBatch(id);
        long after = medicationBatchRepository.count();

        assertEquals(before - 1, after);
        assertTrue(medicationBatchRepository.findById(id).isEmpty());
    }

    @Test
    public void deleteMedicationBatchNonExistingTest() {
        Exception exception = assertThrows(MedicationBatchDoesNotExistException.class, () -> {
            medicationsService.deleteMedicationBatch(777777L);
        });

        assertTrue(exception.getMessage().contains("Medication batch with id 777777 does not exist"));
    }

    @Test
    public void deleteMedicationBatchNullIdTest() {
        assertThrows(InvalidDataAccessApiUsageException.class, () -> {
            medicationsService.deleteMedicationBatch(null);
        });
    }

    // ========== updateMedicationStock (UC 2.8) Tests ==========

    @Test
    public void updateMedicationStockSingleBatchSufficientTest() {
        Long medId = medicationRepository.findAll().getFirst().getId();

        medicationsService.updateMedicationStock(medId, 50);

        List<MedicationBatch> batches = medicationBatchRepository.findByMedication(
                medicationRepository.findById(medId).orElseThrow());
        int total = batches.stream().mapToInt(MedicationBatch::getCurrentQuantity).sum();
        assertEquals(150, total);

        MedicationBatch only = batches.stream().filter(b -> b.getLotNumber() == 123456789L).findFirst().orElseThrow();
        assertEquals(150, only.getCurrentQuantity());
    }

    @Test
    public void updateMedicationStockMultipleBatchesAndDeleteEmptyTest() {
        Long medId = medicationRepository.findAll().getFirst().getId();

        MedicationBatchCommand a = new MedicationBatchCommand(90001L, "2025-01-01", "2025-12-10", 30, 30, 1.0);
        MedicationBatchCommand b = new MedicationBatchCommand(90002L, "2025-01-15", "2026-06-01", 50, 50, 1.0);
        Long idA = medicationsService.createMedicationBatch(medId, a);
        Long idB = medicationsService.createMedicationBatch(medId, b);

        medicationsService.updateMedicationStock(medId, 70);

        assertTrue(medicationBatchRepository.findById(idA).isEmpty());
        MedicationBatch batchB = medicationBatchRepository.findById(idB).orElseThrow();
        assertEquals(10, batchB.getCurrentQuantity());
        MedicationBatch original = medicationBatchRepository.findByMedication(
                medicationRepository.findById(medId).orElseThrow())
                .stream().filter(x -> x.getLotNumber() == 123456789L).findFirst().orElseThrow();
        assertEquals(200, original.getCurrentQuantity());
    }

    @Test
    public void updateMedicationStockInsufficientTotalShouldRollbackTest() {
        Long medId = medicationRepository.findAll().get(3).getId();
        Medication med = medicationRepository.findById(medId).orElseThrow();
        List<MedicationBatch> before = medicationBatchRepository.findByMedication(med);
        int totalBefore = before.stream().mapToInt(MedicationBatch::getCurrentQuantity).sum();
        assertEquals(5, totalBefore);

        assertThrows(NotEnoughMedicationInBatchesException.class, () ->
                medicationsService.updateMedicationStock(medId, 10));

        List<MedicationBatch> after = medicationBatchRepository.findByMedication(med);
        int totalAfter = after.stream().mapToInt(MedicationBatch::getCurrentQuantity).sum();
        assertEquals(5, totalAfter);
    }

    @Test
    public void updateMedicationStockInvalidQuantityTest() {
        Long medId = medicationRepository.findAll().getFirst().getId();
        assertThrows(IllegalArgumentException.class, () -> medicationsService.updateMedicationStock(medId, 0));
        assertThrows(IllegalArgumentException.class, () -> medicationsService.updateMedicationStock(medId, -5));
    }

    // ========== Low Stock Alert tests (moved from LowStockAlertServiceTest) ==========

    @Test
    public void getLowStockAlertByMedicationIdHappyTest() {
        assertEquals(0, lowStockAlertRepository.count());

        Medication med = medicationRepository.findById(4L).orElseThrow();
        LowStockAlert alert = new LowStockAlert();
        alert.setMedication(med);
        alert.setAlertDate(LocalDate.parse("2025-10-01"));
        alert.setAcknowledged(false);
        LowStockAlert saved = lowStockAlertRepository.save(alert);

        LowStockAlert fetched = medicationsService.getLowStockAlertByMedicationId(4L);
        assertNotNull(fetched);
        assertEquals(saved.getId(), fetched.getId());
        assertEquals(4L, fetched.getMedication().getId());
    }

    @Test
    public void getLowStockAlertByMedicationIdMedicationNotFoundTest() {
        Exception ex = assertThrows(LowStockAlertDoesNotExistException.class, () -> {
            medicationsService.getLowStockAlertByMedicationId(999L);
        });
        assertTrue(ex.getMessage().contains("Medication not found"));
    }

    @Test
    public void getLowStockAlertByMedicationIdNoAlertTest() {
        Exception ex = assertThrows(LowStockAlertDoesNotExistException.class, () -> {
            medicationsService.getLowStockAlertByMedicationId(5L);
        });
        assertTrue(ex.getMessage().contains("Low stock alert not found for medication"));
    }

    @Test
    public void getAllLowStockAlertsHappyTest() {
        Medication med1 = medicationRepository.findById(4L).orElseThrow();
        Medication med2 = medicationRepository.findById(5L).orElseThrow();
        LowStockAlert a1 = new LowStockAlert();
        a1.setMedication(med1); a1.setAlertDate(LocalDate.parse("2025-09-01")); a1.setAcknowledged(false);
        LowStockAlert a2 = new LowStockAlert();
        a2.setMedication(med2); a2.setAlertDate(LocalDate.parse("2025-09-02")); a2.setAcknowledged(true);
        lowStockAlertRepository.save(a1);
        lowStockAlertRepository.save(a2);

        List<LowStockAlert> all = medicationsService.getAllLowStockAlerts();
        assertEquals(2, all.size());
    }

    @Test
    public void createLowStockAlertHappyTest() {
        Medication med = medicationRepository.findById(5L).orElseThrow();
        LowStockAlert alert = new LowStockAlert();
        alert.setAlertDate(LocalDate.parse("2025-10-01"));
        alert.setAcknowledged(false);

        long before = lowStockAlertRepository.count();
        LowStockAlert created = medicationsService.createLowStockAlert(5L, alert);
        long after = lowStockAlertRepository.count();

        assertEquals(before + 1, after);
        assertNotNull(created.getId());
        assertEquals(5L, created.getMedication().getId());
    }

    @Test
    public void createLowStockAlertAlreadyExistsTest() {
        Medication med = medicationRepository.findById(4L).orElseThrow();
        LowStockAlert alert = new LowStockAlert();
        alert.setMedication(med); alert.setAlertDate(LocalDate.now()); alert.setAcknowledged(false);
        lowStockAlertRepository.save(alert);

        LowStockAlert newAlert = new LowStockAlert();
        newAlert.setAlertDate(LocalDate.now()); newAlert.setAcknowledged(false);

        Exception ex = assertThrows(LowStockAlertDoesAlreadyExistException.class, () -> {
            medicationsService.createLowStockAlert(4L, newAlert);
        });
        assertTrue(ex.getMessage().contains("Low stock alert already exists for this medication"));
    }

    @Test
    public void createLowStockAlertMedicationNotFoundTest() {
        LowStockAlert alert = new LowStockAlert();
        alert.setAlertDate(LocalDate.now()); alert.setAcknowledged(false);

        Exception ex = assertThrows(LowStockAlertDoesNotExistException.class, () -> {
            medicationsService.createLowStockAlert(999L, alert);
        });
        assertTrue(ex.getMessage().contains("Medication not found"));
    }

    @Test
    public void createLowStockAlertNullMedicationIdTest() {
        LowStockAlert alert = new LowStockAlert();
        alert.setAlertDate(LocalDate.now()); alert.setAcknowledged(false);
        assertThrows(IllegalArgumentException.class, () -> {
            medicationsService.createLowStockAlert(null, alert);
        });
    }

    @Test
    public void deleteLowStockAlertHappyTest() {
        Medication med = medicationRepository.findById(5L).orElseThrow();
        LowStockAlert alert = new LowStockAlert(); alert.setMedication(med); alert.setAlertDate(LocalDate.now()); alert.setAcknowledged(false);
        LowStockAlert saved = lowStockAlertRepository.save(alert);

        long before = lowStockAlertRepository.count();
        medicationsService.deleteLowStockAlert(saved.getId());
        long after = lowStockAlertRepository.count();

        assertEquals(before - 1, after);
        assertTrue(lowStockAlertRepository.findById(saved.getId()).isEmpty());
    }

    @Test
    public void deleteLowStockAlertNonExistingTest() {
        Exception ex = assertThrows(LowStockAlertDoesNotExistException.class, () -> {
            medicationsService.deleteLowStockAlert(9999L);
        });
        assertTrue(ex.getMessage().contains("Low stock alert does not exist"));
    }

    @Test
    public void deleteLowStockAlertNullIdTest() {
        assertThrows(IllegalArgumentException.class, () -> {
            medicationsService.deleteLowStockAlert(null);
        });
    }

    @Test
    public void checkAndUpdateLowStockAlertCreateAlertTest() {
        assertEquals(0, lowStockAlertRepository.findAll().size());
        medicationsService.checkAndUpdateLowStockAlert(4L);

        List<LowStockAlert> alerts = lowStockAlertRepository.findAll();
        assertEquals(1, alerts.size());
        assertEquals(4L, alerts.getFirst().getMedication().getId());
    }

    @Test
    public void checkAndUpdateLowStockAlertDeleteAlertTest() {
        Medication med5 = medicationRepository.findById(5L).orElseThrow();
        LowStockAlert alert = new LowStockAlert(); alert.setMedication(med5); alert.setAlertDate(LocalDate.now()); alert.setAcknowledged(false);
        lowStockAlertRepository.save(alert);
        assertEquals(1, lowStockAlertRepository.findByMedication(med5).size());

        MedicationBatch batch = medicationBatchRepository.findById(5L).orElseThrow();
        batch.setCurrentQuantity(100);
        medicationBatchRepository.save(batch);

        medicationsService.checkAndUpdateLowStockAlert(5L);

        assertEquals(0, lowStockAlertRepository.findByMedication(med5).size());
    }

    @Test
    public void checkAndUpdateLowStockAlertNoOpTest() {
        assertTrue(lowStockAlertRepository.findByMedication(medicationRepository.findById(5L).orElseThrow()).isEmpty());
        medicationsService.checkAndUpdateLowStockAlert(5L);
        assertTrue(lowStockAlertRepository.findByMedication(medicationRepository.findById(5L).orElseThrow()).isEmpty());
    }

    @Test
    public void checkAndUpdateLowStockAlertMedicationNotFoundTest() {
        Exception ex = assertThrows(MedicationDoesNotExistException.class, () -> {
            medicationsService.checkAndUpdateLowStockAlert(999L);
        });
        assertTrue(ex.getMessage().contains("Medication not found"));
    }
}
