package cat.tecnocampus.veterinarymanagement.application.mappers;

import cat.tecnocampus.veterinarymanagement.application.inputDTO.MedicationIncompatibilityCommand;
import cat.tecnocampus.veterinarymanagement.application.outputDTO.MedicationIncompatibilityInformation;
import cat.tecnocampus.veterinarymanagement.domain.MedicationIncompatibility;
import cat.tecnocampus.veterinarymanagement.domain.Medication;

public class MedicationIncompatibilityMapper {
    public static MedicationIncompatibilityInformation toInformation(MedicationIncompatibility mi) {
        if (mi == null) return null;
        return new MedicationIncompatibilityInformation(
                mi.getId(),
                mi.getMedicationA() != null ? mi.getMedicationA().getId() : null,
                mi.getMedicationA() != null ? mi.getMedicationA().getName() : null,
                mi.getMedicationB() != null ? mi.getMedicationB().getId() : null,
                mi.getMedicationB() != null ? mi.getMedicationB().getName() : null,
                mi.getPersistsUntil(),
                mi.getDescription()
        );
    }

    public static MedicationIncompatibility inputCommandToDomain(MedicationIncompatibilityCommand command,
                                                                  Medication medA,
                                                                  Medication medB) {
        MedicationIncompatibility mi = new MedicationIncompatibility();
        mi.setMedicationA(medA);
        mi.setMedicationB(medB);
        mi.setPersistsUntil(command.persists_until());
        mi.setDescription(command.description());
        return mi;
    }
}
