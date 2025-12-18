package cat.tecnocampus.veterinarymanagement.application.inputDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Positive;

public record DiscountCommand(
        @NotBlank(message = "The field 'code' cannot be blank")
        String code,

        @NotBlank(message = "The field 'discount_type' cannot be blank")
        @Pattern(regexp = "^(PERCENTAGE|FIXED_AMOUNT|LOYALTY_TIER)$", message = "The field 'discount_type' must be one of: PERCENTAGE, FIXED_AMOUNT, LOYALTY_TIER")
        String discount_type,

        @NotNull(message = "The field 'value_amount' cannot be null")
        @Positive(message = "The field 'value_amount' must be a positive number")
        Double value_amount,

        @NotBlank(message = "The field 'start_date' cannot be blank")
        @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "The field 'start_date' must be in YYYY-MM-DD format")
        String start_date,

        @NotBlank(message = "The field 'end_date' cannot be blank")
        @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "The field 'end_date' must be in YYYY-MM-DD format")
        String end_date,

        @NotNull(message = "The field 'max_uses' cannot be null")
        @PositiveOrZero(message = "The field 'max_uses' must be zero or greater")
        Integer max_uses,

        @NotNull(message = "The field 'uses_count' cannot be null")
        @PositiveOrZero(message = "The field 'uses_count' must be zero or greater")
        Integer uses_count
) {}
