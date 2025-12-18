package cat.tecnocampus.veterinarymanagement.application.inputDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record PromotionCommand(
    @NotBlank(message = "The field 'name' cannot be blank")
    @Size(max = 100, message = "The field 'name' must be at most 100 characters")
    String name,

    @NotBlank(message = "The field 'description' cannot be blank")
    @Size(max = 500, message = "The field 'description' must be at most 500 characters")
    String description,

    @NotBlank(message = "The field 'discount_code' cannot be blank")
    @Size(max = 50, message = "The field 'discount_code' must be at most 50 characters")
    String discount_code,

    @NotBlank(message = "The field 'start_date' cannot be blank")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "The field 'start_date' must be in YYYY-MM-DD format")
    String start_date,

    @NotBlank(message = "The field 'end_date' cannot be blank")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "The field 'end_date' must be in YYYY-MM-DD format")
    String end_date
) {}
