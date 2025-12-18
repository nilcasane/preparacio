package cat.tecnocampus.veterinarymanagement.application.inputDTO;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record MedicationIncompatibilityCommand(
        @NotNull(message = "medication_a_id cannot be null")
        Long medication_a_id,
        @NotNull(message = "medication_b_id cannot be null")
        Long medication_b_id,
        // Optional: the date until the incompatibility persists (null = indefinite)
        LocalDate persists_until,
        // Optional textual description of the incompatibility
        String description
) {}
