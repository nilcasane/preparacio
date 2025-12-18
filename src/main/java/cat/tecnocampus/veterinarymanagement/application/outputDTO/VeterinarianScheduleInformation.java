package cat.tecnocampus.veterinarymanagement.application.outputDTO;

import java.util.List;

public record VeterinarianScheduleInformation(
    Long veterinarianId,
    String startDate,
    String endDate,
    List<VisitInformation> visits,
    List<AvailabilityInformation> availabilities
) {}
