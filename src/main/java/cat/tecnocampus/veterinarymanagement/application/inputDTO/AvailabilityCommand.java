package cat.tecnocampus.veterinarymanagement.application.inputDTO;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record AvailabilityCommand (

    @NotNull(message = "The field 'day_of_week' cannot be null")
    @Min(value = 1, message = "The field 'day_of_week' must be between 1 and 7")
    @Max(value = 7, message = "The field 'day_of_week' must be between 1 and 7")
    Integer day_of_week,

    @NotBlank(message = "The field 'start_time' cannot be blank")
    @Pattern(regexp = "^([01]\\d|2[0-3]):([0-5]\\d)$", message = "The field 'start_time' must be in HH:MM format")
    String start_time,

    @NotBlank(message = "The field 'end_time' cannot be blank")
    @Pattern(regexp = "^([01]\\d|2[0-3]):([0-5]\\d)$", message = "The field 'end_time' must be in HH:MM format")
    String end_time,

    @NotBlank(message = "The field 'period_start' cannot be blank")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "The field 'period_start' must be in YYYY-MM-DD format")
    String period_start,

    @NotBlank(message = "The field 'period_end' cannot be blank")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "The field 'period_end' must be in YYYY-MM-DD format")
    String period_end
) {
}
