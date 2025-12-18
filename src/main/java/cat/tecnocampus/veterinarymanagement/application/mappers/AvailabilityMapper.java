package cat.tecnocampus.veterinarymanagement.application.mappers;

import cat.tecnocampus.veterinarymanagement.application.inputDTO.AvailabilityCommand;
import cat.tecnocampus.veterinarymanagement.application.outputDTO.AvailabilityInformation;
import cat.tecnocampus.veterinarymanagement.domain.Availability;
import cat.tecnocampus.veterinarymanagement.domain.Veterinarian;

import java.time.LocalDate;
import java.time.LocalTime;

public class AvailabilityMapper {
    public static Availability inputAvailabilityToDomain(AvailabilityCommand command, Veterinarian vet) {
        return new Availability(command.day_of_week(), LocalTime.parse(command.start_time()), LocalTime.parse(command.end_time()), LocalDate.parse(command.period_start()), LocalDate.parse(command.period_end()), vet);
    }

    public static AvailabilityInformation toAvailabilityInformation(Availability availability) {
        return new AvailabilityInformation(
                availability.getId(),
                availability.getDayOfWeek(),
                availability.getStartTime().toString(),
                availability.getEndTime().toString(),
                availability.getPeriodStart().toString(),
                availability.getPeriodEnd().toString(),
                availability.getVeterinarian().getId()
        );
    }
}
