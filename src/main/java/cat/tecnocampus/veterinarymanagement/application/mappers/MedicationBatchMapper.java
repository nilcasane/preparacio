package cat.tecnocampus.veterinarymanagement.application.mappers;

import cat.tecnocampus.veterinarymanagement.application.inputDTO.MedicationBatchCommand;
import cat.tecnocampus.veterinarymanagement.application.outputDTO.MedicationBatchInformation;
import cat.tecnocampus.veterinarymanagement.domain.Medication;
import cat.tecnocampus.veterinarymanagement.domain.MedicationBatch;

import java.time.LocalDate;

public class MedicationBatchMapper {
    public static MedicationBatch inputMedicationBatchToDomain(MedicationBatchCommand command, Medication med) {
        MedicationBatch batch = new MedicationBatch();
        batch.setLotNumber(command.lot_number());
        batch.setReceivedDate(LocalDate.parse(command.received_date()));
        batch.setExpiryDate(LocalDate.parse(command.expiry_date()));
        batch.setInitialQuantity(command.initial_quantity());
        batch.setCurrentQuantity(command.current_quantity());
        batch.setPurchasePricePerUnit(command.purchase_price_per_unit());
        batch.setMedication(med);
        return batch;
    }

    public static MedicationBatchInformation toMedicationBatchInformation(MedicationBatch batch) {
        return new MedicationBatchInformation(
                batch.getId(),
                batch.getMedication().getId(),
                batch.getLotNumber(),
                batch.getReceivedDate(),
                batch.getExpiryDate(),
                batch.getInitialQuantity(),
                batch.getCurrentQuantity(),
                batch.getPurchasePricePerUnit()
        );
    }
}
