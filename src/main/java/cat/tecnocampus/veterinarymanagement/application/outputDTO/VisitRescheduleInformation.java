package cat.tecnocampus.veterinarymanagement.application.outputDTO;

public record VisitRescheduleInformation(
        Long history_id,
        Long visit_id,
        String oldDate,    // YYYY-MM-DD
        String oldTime,    // HH:MM
        String newDate,    // YYYY-MM-DD
        String newTime,    // HH:MM
        String action,
        String performedBy,
        String createdAt   // ISO date-time
) {}

