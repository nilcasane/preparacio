package cat.tecnocampus.veterinarymanagement.application.outputDTO;

public record MedicationPrescriptionInformation(
        Long prescription_id,
        Long visit_id,
        Long medication_id,
        String medication_name,
        Integer quantity,
        String dosage_instructions,
        Integer duration_in_days
) {}
