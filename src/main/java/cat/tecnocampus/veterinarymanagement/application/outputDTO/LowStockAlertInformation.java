package cat.tecnocampus.veterinarymanagement.application.outputDTO;

import java.time.LocalDate;

public record LowStockAlertInformation(
    Long id,
    Long medication_id,
    LocalDate alert_date,
    Boolean acknowledged
) {}

