package cat.tecnocampus.veterinarymanagement.application.outputDTO;

public record MedicationInformation(
    Long medication_id,
    String name,
    String active_ingredient,
    Integer dosage_unit,
    Double unit_price,
    Integer reorder_threshold
) {}

