package cat.tecnocampus.veterinarymanagement.application.mappers;

import cat.tecnocampus.veterinarymanagement.application.outputDTO.MedicationInformation;
import cat.tecnocampus.veterinarymanagement.domain.Medication;

public class MedicationMapper {

    public static MedicationInformation toMedicationInformation(Medication medication) {
        return new MedicationInformation(
                medication.getId(),
                medication.getName(),
                medication.getActiveIngredient(),
                medication.getDosageUnit(),
                medication.getUnitPrice(),
                medication.getReorderThreshold()
        );
    }
}