package cat.tecnocampus.veterinarymanagement.application.inputDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record TreatmentCommand(
        @NotBlank(message = "The field 'name' cannot be blank")
        String name,

        String description,

        @NotNull(message = "The field 'cost' cannot be null")
        @PositiveOrZero(message = "The field 'cost' must be zero or a positive number")
        Double cost
) {}
