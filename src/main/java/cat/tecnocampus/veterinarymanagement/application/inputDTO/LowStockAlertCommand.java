package cat.tecnocampus.veterinarymanagement.application.inputDTO;

import jakarta.validation.constraints.Pattern;

public record LowStockAlertCommand(
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Period start must be in YYYY-MM-DD format")
    String alert_date,

    Boolean acknowledge
) {}

