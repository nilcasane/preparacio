package cat.tecnocampus.veterinarymanagement.application.mappers;

import cat.tecnocampus.veterinarymanagement.application.inputDTO.TreatmentCommand;
import cat.tecnocampus.veterinarymanagement.application.outputDTO.TreatmentInformation;
import cat.tecnocampus.veterinarymanagement.domain.Treatment;

public class TreatmentMapper {
    public static Treatment inputTreatmentToDomain(TreatmentCommand command) {
        return new Treatment(command.name(), command.description(), command.cost());
    }

    public static TreatmentInformation toTreatmentInformation(Treatment treatment) {
        if (treatment == null) return null;
        return new TreatmentInformation(
                treatment.getId(),
                treatment.getName(),
                treatment.getDescription(),
                treatment.getCost()
        );
    }
}


