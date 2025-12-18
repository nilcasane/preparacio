package cat.tecnocampus.veterinarymanagement.application;

import cat.tecnocampus.veterinarymanagement.application.exceptions.*;
import cat.tecnocampus.veterinarymanagement.application.inputDTO.MedicationBatchCommand;
import cat.tecnocampus.veterinarymanagement.application.mappers.MedicationBatchMapper;
import cat.tecnocampus.veterinarymanagement.application.outputDTO.MedicationBatchInformation;
import cat.tecnocampus.veterinarymanagement.application.outputDTO.MedicationInformation;
import cat.tecnocampus.veterinarymanagement.domain.LowStockAlert;
import cat.tecnocampus.veterinarymanagement.domain.Medication;
import cat.tecnocampus.veterinarymanagement.domain.MedicationBatch;
import cat.tecnocampus.veterinarymanagement.persistence.LowStockAlertRepository;
import cat.tecnocampus.veterinarymanagement.persistence.MedicationBatchRepository;
import cat.tecnocampus.veterinarymanagement.persistence.MedicationRepository;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MedicationsService {
    private final MedicationRepository medicationRepository;
    private final MedicationBatchRepository medicationBatchRepository;
    private final LowStockAlertRepository lowStockAlertRepository;

    public MedicationsService(MedicationRepository medicationRepository,
                              MedicationBatchRepository medicationBatchRepository,
                              LowStockAlertRepository lowStockAlertRepository) {
        this.medicationRepository = medicationRepository;
        this.medicationBatchRepository = medicationBatchRepository;
        this.lowStockAlertRepository = lowStockAlertRepository;
    }

    public Optional<MedicationInformation> getMedicationById(Long id) {
        return medicationRepository.findMedicationInformationById(id);
    }

    public Optional<MedicationBatchInformation> getMedicationBatchById(Long id) {
        return medicationBatchRepository
                .findMedicationBatchById(id)
                .map(MedicationBatchMapper::toMedicationBatchInformation);
    }

    public Long createMedicationBatch(Long medId, MedicationBatchCommand command) {
        Medication med = medicationRepository
                .findById(medId)
                .orElseThrow(() -> new MedicationDoesNotExistException("Medication with id" + medId + " does not exist"));

        MedicationBatch batch = MedicationBatchMapper.inputMedicationBatchToDomain(command, med);
        var saved = medicationBatchRepository.save(batch);
        return saved.getId();
    }

    public List<MedicationBatchInformation> getMedicationBatches(Long medId) {
        Medication med = medicationRepository
                .findById(medId)
                .orElseThrow(() -> new MedicationDoesNotExistException("Medication with id " + medId + " does not exist"));

        List<MedicationBatch> medicationBatches = medicationBatchRepository.findByMedication(med);
        return medicationBatches.stream()
                .map(MedicationBatchMapper::toMedicationBatchInformation)
                .toList();
    }

    public MedicationBatchInformation updateMedicationBatch(Long batchId, MedicationBatchCommand command) {
        MedicationBatch batch = medicationBatchRepository.findById(batchId)
                .orElseThrow(() -> new MedicationBatchDoesNotExistException("Medication batch with id " + batchId + " does not exist"));
        batch.updateMedicationBatch(command);
        medicationBatchRepository.save(batch);
        checkAndUpdateLowStockAlert(batch.getMedication().getId());
        return MedicationBatchMapper.toMedicationBatchInformation(batch);
    }

    public void deleteMedicationBatch(Long batchId) {
        MedicationBatch batch = medicationBatchRepository.findById(batchId)
                .orElseThrow(() -> new MedicationBatchDoesNotExistException("Medication batch with id " + batchId + " does not exist"));
        medicationBatchRepository.delete(batch);
    }


    @Transactional
    public void updateMedicationStock(Long medId, int quantityToDeduct) {
        if (quantityToDeduct <= 0) {
            throw new IllegalArgumentException("Quantity to deduct must be positive");
        }
        Medication med = medicationRepository
                .findById(medId)
                .orElseThrow(() -> new MedicationDoesNotExistException("Medication with id " + medId + " does not exist"));

        List<MedicationBatch> availableBatches = medicationBatchRepository.findAvailableBatchesByMedication(med);
        if (availableBatches.isEmpty()) {
            throw new NotEnoughMedicationInBatchesException("No available batches for medication " + medId);
        }

        int totalAvailable = availableBatches.stream().mapToInt(MedicationBatch::getCurrentQuantity).sum();
        if (totalAvailable < quantityToDeduct) {
            throw new NotEnoughMedicationInBatchesException("Insufficient stock in batches. Required: " + quantityToDeduct + ", available: " + totalAvailable);
        }

        int deducted = 0;

        MedicationBatch firstBatch = availableBatches.getFirst();
        if (firstBatch.getCurrentQuantity() >= quantityToDeduct) { // simple path: first batch covers all
            firstBatch.setCurrentQuantity(firstBatch.getCurrentQuantity() - quantityToDeduct);
            deducted = quantityToDeduct;
            medicationBatchRepository.save(firstBatch);
            if (firstBatch.getCurrentQuantity() == 0) {
                deleteMedicationBatch(firstBatch.getId());
            }
        } else { // distribute across batches
            int remaining = quantityToDeduct;
            for (MedicationBatch batch : availableBatches) {
                if (remaining == 0) break;
                if (batch.getCurrentQuantity() >= remaining) { // this batch completes what's missing
                    batch.setCurrentQuantity(batch.getCurrentQuantity() - remaining);
                    deducted += remaining;
                    remaining = 0;
                    medicationBatchRepository.save(batch);
                    if (batch.getCurrentQuantity() == 0) {
                        deleteMedicationBatch(batch.getId());
                    }
                    break;
                } else { // consume entire batch and continue
                    remaining -= batch.getCurrentQuantity();
                    deducted += batch.getCurrentQuantity();
                    batch.setCurrentQuantity(0);
                    medicationBatchRepository.save(batch);
                    deleteMedicationBatch(batch.getId());
                }
            }
        }

        if (deducted < quantityToDeduct) {
            // Defensive: should not happen due to earlier totalAvailable check, but ensures rollback if it does
            throw new NotEnoughMedicationInBatchesException("Could not deduct full quantity. Deducted: " + deducted + ", required: " + quantityToDeduct);
        }

        checkAndUpdateLowStockAlert(medId);
    }
    public LowStockAlert getLowStockAlertByMedicationId(Long medicationId) {
        Medication med = medicationRepository.findById(medicationId)
                .orElseThrow(() -> new LowStockAlertDoesNotExistException("Medication not found"));
        return lowStockAlertRepository.findByMedication(med).stream().findFirst()
                .orElseThrow(() -> new LowStockAlertDoesNotExistException("Low stock alert not found for medication"));
    }

    public List<LowStockAlert> getAllLowStockAlerts() {
        List<LowStockAlert> result = new ArrayList<>();
        lowStockAlertRepository.findAll().forEach(result::add);
        return result;
    }

    public LowStockAlert createLowStockAlert(Long medicationId, LowStockAlert alert) {
        try {
            Medication med = medicationRepository.findById(medicationId)
                    .orElseThrow(() -> new LowStockAlertDoesNotExistException("Medication not found"));
            if (!lowStockAlertRepository.findByMedication(med).isEmpty()) {
                throw new LowStockAlertDoesAlreadyExistException("Low stock alert already exists for this medication");
            }
            alert.setMedication(med);
            return lowStockAlertRepository.save(alert);
        } catch (InvalidDataAccessApiUsageException e) {
            throw new IllegalArgumentException("Error creating the low stock alert");
        }
    }

    public void deleteLowStockAlert(Long alertId) {
        try {
            if (!lowStockAlertRepository.existsById(alertId)) {
                throw new LowStockAlertDoesNotExistException("Low stock alert does not exist");
            }
            lowStockAlertRepository.deleteById(alertId);
        } catch (InvalidDataAccessApiUsageException e) {
            throw new IllegalArgumentException("Error deleting low stock alert with id " + alertId, e);
        }
    }

    public void checkAndUpdateLowStockAlert(Long medicationId) {
        try {
            Medication med = medicationRepository.findById(medicationId)
                    .orElseThrow(() -> new MedicationDoesNotExistException("Medication not found"));
            int totalQuantity = medicationBatchRepository.findByMedication(med).stream()
                    .mapToInt(batch -> batch.getCurrentQuantity() != null ? batch.getCurrentQuantity() : 0)
                    .sum();
            int threshold = med.getReorderThreshold();
            LowStockAlert existingAlert = lowStockAlertRepository.findByMedication(med).stream().findFirst().orElse(null);
            if (totalQuantity < threshold && existingAlert == null) {
                LowStockAlert alert = new LowStockAlert();
                alert.setMedication(med);
                alert.setAlertDate(LocalDate.now());
                alert.setAcknowledged(false);
                lowStockAlertRepository.save(alert);
            } else if (totalQuantity >= threshold && existingAlert != null) {
                lowStockAlertRepository.deleteById(existingAlert.getId());
            }
        } catch (InvalidDataAccessApiUsageException e) {
            throw new IllegalArgumentException("Error checking/updating low stock alert for medication with id " + medicationId, e);
        }
    }
}
