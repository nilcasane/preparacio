package cat.tecnocampus.veterinarymanagement.application.inputDTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record MedicationPrescriptionCommand(
        @NotNull(message = "The field 'quantity' cannot be null")
        @Min(value = 1, message = "The field 'quantity' must be at least 1")
        Integer quantity,

        @NotBlank(message = "The field 'dosage_instructions' cannot be blank")
        String dosage_instructions,

        @NotNull(message = "The field 'duration_in_days' cannot be null")
        @Min(value = 1, message = "The field 'duration_in_days' must be at least 1")
        Integer duration_in_days
) {}
