package cat.tecnocampus.veterinarymanagement.application.outputDTO;

public record AvailabilityExceptionInformation(
    Long exception_id,
    String reason,
    Integer day_of_week,
    String start_time, // HH:MM
    String end_time,   // HH:MM
    String period_start, // YYYY-MM-DD
    String period_end,   // YYYY-MM-DD
    Long availability_id
) {
}

