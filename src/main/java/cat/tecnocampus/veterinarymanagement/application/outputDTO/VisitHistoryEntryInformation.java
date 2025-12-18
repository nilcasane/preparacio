package cat.tecnocampus.veterinarymanagement.application.outputDTO;

import java.util.List;

public record VisitHistoryEntryInformation(
        String occurred_date,
        VisitInformation visit,
        TreatmentInformation treatment,
        List<MedicationPrescriptionInformation> prescriptions
) {}
