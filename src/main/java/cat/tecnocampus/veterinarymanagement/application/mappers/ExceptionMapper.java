package cat.tecnocampus.veterinarymanagement.application.mappers;

import cat.tecnocampus.veterinarymanagement.application.inputDTO.ExceptionCommand;
import cat.tecnocampus.veterinarymanagement.application.outputDTO.AvailabilityExceptionInformation;
import cat.tecnocampus.veterinarymanagement.domain.Availability;
import cat.tecnocampus.veterinarymanagement.domain.AvailabilityException;

import java.time.LocalDate;
import java.time.LocalTime;

public class ExceptionMapper {
    public static AvailabilityException inputExceptionToDomain(ExceptionCommand command, Availability availability) {
        AvailabilityException exception = new AvailabilityException();
        exception.setReason(command.reason());
        exception.setDayOfWeek(command.day_of_week());
        exception.setStartTime(LocalTime.parse(command.start_time()));
        exception.setEndTime(LocalTime.parse(command.end_time()));
        exception.setPeriodStart(LocalDate.parse(command.period_start()));
        exception.setPeriodEnd(LocalDate.parse(command.period_end()));
        exception.setAvailability(availability);
        return exception;
    }

    public static AvailabilityExceptionInformation toExceptionInformation(AvailabilityException exception) {
        return new AvailabilityExceptionInformation(
                exception.getId(),
                exception.getReason(),
                exception.getDayOfWeek(),
                exception.getStartTime().toString(),
                exception.getEndTime().toString(),
                exception.getPeriodStart().toString(),
                exception.getPeriodEnd().toString(),
                exception.getAvailability().getId()
        );
    }
}
