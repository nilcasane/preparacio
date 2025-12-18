package cat.tecnocampus.veterinarymanagement.application.inputDTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record LoyaltyTierCommand(
    @NotBlank(message = "The field 'name' cannot be blank")
    @Size(max = 100, message = "The field 'name' must be at most 100 characters")
    String name,

    @NotBlank(message = "The field 'description' cannot be blank")
    @Size(max = 500, message = "The field 'description' must be at most 500 characters")
    String description,

    @NotNull(message = "The field 'min_points' cannot be null")
    @Min(value = 0, message = "The field 'min_points' must be zero or greater")
    Integer min_points,

    @NotBlank(message = "The field 'benefits' cannot be blank")
    @Size(max = 500, message = "The field 'benefits' must be at most 500 characters")
    String benefits
) {
}
