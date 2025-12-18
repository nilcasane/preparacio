package cat.tecnocampus.veterinarymanagement.application.outputDTO;

import java.time.LocalDate;

public record MedicationBatchInformation(
    Long batch_id,
    Long medication_id,
    Long lot_number,
    LocalDate received_date,
    LocalDate expiry_date,
    Integer initial_quantity,
    Integer current_quantity,
    Double purchase_price_per_unit
) {}

