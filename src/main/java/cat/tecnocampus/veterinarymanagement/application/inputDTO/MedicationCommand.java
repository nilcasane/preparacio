package cat.tecnocampus.veterinarymanagement.application.inputDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record MedicationCommand(
    @NotBlank(message = "The field 'name' cannot be blank")
    String name,

    @NotBlank(message = "The field 'active_ingredient' cannot be blank")
    String active_ingredient,

    @NotNull(message = "The field 'dosage_unit' cannot be null")
    @Positive(message = "The field 'dosage_unit' must be a positive integer")
    Integer dosage_unit,

    @NotNull(message = "The field 'unit_price' cannot be null")
    @PositiveOrZero(message = "The field 'unit_price' must be zero or a positive number")
    Double unit_price,

    @NotNull(message = "The field 'reorder_threshold' cannot be null")
    @PositiveOrZero(message = "The field 'reorder_threshold' must be zero or a positive number")
    Integer reorder_threshold
) {}

