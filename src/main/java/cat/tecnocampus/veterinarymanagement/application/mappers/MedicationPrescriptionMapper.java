package cat.tecnocampus.veterinarymanagement.application.mappers;

import cat.tecnocampus.veterinarymanagement.application.inputDTO.MedicationPrescriptionCommand;
import cat.tecnocampus.veterinarymanagement.application.outputDTO.MedicationPrescriptionInformation;
import cat.tecnocampus.veterinarymanagement.domain.Medication;
import cat.tecnocampus.veterinarymanagement.domain.MedicationPrescription;
import cat.tecnocampus.veterinarymanagement.domain.Visit;

public class MedicationPrescriptionMapper {
    public static MedicationPrescriptionInformation toMedicationPrescriptionInformation(MedicationPrescription mp) {
        return new MedicationPrescriptionInformation(
                mp.getId(),
                mp.getVisit() != null ? mp.getVisit().getId() : null,
                mp.getMedication() != null ? mp.getMedication().getId() : null,
                mp.getMedication() != null ? mp.getMedication().getName() : null,
                mp.getQuantityPrescribed(),
                mp.getDosageInstructions(),
                mp.getDurationInDays()
        );
    }

    public static MedicationPrescription inputMedicationPrescriptionToDomain(MedicationPrescriptionCommand command,
                                                                             Visit visit,
                                                                             Medication medication
                                                                             ) {
        MedicationPrescription mp = new MedicationPrescription();
        mp.setVisit(visit);
        mp.setMedication(medication);
        mp.setQuantityPrescribed(command.quantity());
        mp.setDosageInstructions(command.dosage_instructions());
        mp.setDurationInDays(command.duration_in_days());
        return mp;
    }
}
