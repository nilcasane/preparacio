package cat.tecnocampus.veterinarymanagement.application.outputDTO;

import cat.tecnocampus.veterinarymanagement.domain.VisitStatus;

public record VisitInformation(
    Long visit_id,
    String visitDate,      // YYYY-MM-DD
    String visitTime,      // HH:MM
    Integer duration,
    String reasonForVisit,
    Double pricerPerFifteen,
    VisitStatus status,
    Long veterinarian_id,
    Long pet_id,
    Long pet_owner_id,
    TreatmentInformation treatment
) {}
