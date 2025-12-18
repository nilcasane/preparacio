package cat.tecnocampus.veterinarymanagement.application.inputDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

public record VisitCommand(
    @NotBlank(message = "The field 'visit_date' cannot be blank")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "The field 'visit_date' must be in YYYY-MM-DD format")
    String visit_date,

    @NotBlank(message = "The field 'visit_time' cannot be blank")
    @Pattern(regexp = "^([01]\\d|2[0-3]):([0-5]\\d)$", message = "The field 'visit_time' must be in HH:MM format")
    String visit_time,

    @Positive(message = "The field 'duration' must be positive")
    Integer duration,

    @NotBlank(message = "The field 'reasonForVisit' cannot be blank")
    String reasonForVisit,

    @NotNull(message = "The field 'price_per_fifteen' cannot be null")
    @Positive(message = "The field 'price_per_fifteen' must be positive")
    Double price_per_fifteen,

    @NotNull(message = "The field 'veterinarian_id' cannot be null")
    Long veterinarian_id,

    @NotNull(message = "The field 'pet_id' cannot be null")
    Long pet_id,

    @NotNull(message = "The field 'pet_owner_id' cannot be null")
    Long pet_owner_id
) {}
