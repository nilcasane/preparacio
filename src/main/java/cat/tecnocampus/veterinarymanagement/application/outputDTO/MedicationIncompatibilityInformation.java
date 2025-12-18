package cat.tecnocampus.veterinarymanagement.application.outputDTO;

import java.time.LocalDate;

public record MedicationIncompatibilityInformation(
        Long id,
        Long medication_a_id,
        String medication_a_name,
        Long medication_b_id,
        String medication_b_name,
        LocalDate persists_until,
        String description
) {}
