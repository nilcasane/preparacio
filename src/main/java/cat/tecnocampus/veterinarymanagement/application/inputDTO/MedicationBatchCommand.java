package cat.tecnocampus.veterinarymanagement.application.inputDTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record MedicationBatchCommand(
    @NotNull(message = "The field 'lot_number' cannot be null")
    @Positive(message = "The field 'lot_number' must be a positive number")
    Long lot_number,

    @NotBlank(message = "The field 'received_date' cannot be blank")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "The field 'received_date' must be in YYYY-MM-DD format")
    String received_date,

    @NotBlank(message = "The field 'expiry_date' cannot be blank")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "The field 'expiry_date' must be in YYYY-MM-DD format")
    String expiry_date,

    @NotNull(message = "The field 'initial_quantity' cannot be null")
    @Min(value = 0, message = "The field 'initial_quantity' must be zero or greater")
    Integer initial_quantity,

    @NotNull(message = "The field 'current_quantity' cannot be null")
    @Min(value = 0, message = "The field 'current_quantity' must be zero or greater")
    Integer current_quantity,

    @NotNull(message = "The field 'purchase_price_per_unit' cannot be null")
    @PositiveOrZero(message = "The field 'purchase_price_per_unit' must be zero or greater")
    Double purchase_price_per_unit
) {}
