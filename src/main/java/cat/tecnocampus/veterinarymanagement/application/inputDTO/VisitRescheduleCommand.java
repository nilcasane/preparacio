package cat.tecnocampus.veterinarymanagement.application.inputDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record VisitRescheduleCommand(
        @NotBlank(message = "The field 'newDate' cannot be blank")
        @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "The field 'newDate' must be in YYYY-MM-DD format")
        String newDate,

        @NotBlank(message = "The field 'newTime' cannot be blank")
        @Pattern(regexp = "^([01]\\d|2[0-3]):([0-5]\\d)$", message = "The field 'newTime' must be in HH:MM format")
        String newTime,

        String performedBy
) {}

