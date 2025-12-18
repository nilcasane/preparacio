package cat.tecnocampus.veterinarymanagement.application.mappers;

import cat.tecnocampus.veterinarymanagement.application.inputDTO.LowStockAlertCommand;
import cat.tecnocampus.veterinarymanagement.application.outputDTO.LowStockAlertInformation;
import cat.tecnocampus.veterinarymanagement.domain.LowStockAlert;
import cat.tecnocampus.veterinarymanagement.domain.Medication;

import java.time.LocalDate;

public class LowStockAlertMapper {
    public static LowStockAlert inputLowToDomain(LowStockAlertCommand command, Medication med) {
        LowStockAlert alert = new LowStockAlert();
        alert.setAlertDate(LocalDate.parse(command.alert_date()));
        alert.setAcknowledged(command.acknowledge());
        alert.setMedication(med);
        return alert;
    }

    public static LowStockAlertInformation toMedicationBatchInformation(LowStockAlert alert) {
        return new LowStockAlertInformation(
                alert.getId(),
                alert.getMedication().getId(),
                alert.getAlertDate(),
                alert.getAcknowledged()
        );
    }
}
