package cat.tecnocampus.veterinarymanagement.application.outputDTO;

public record VeterinarianDemandInformation(
        Long veterinarian_id,
        String first_name,
        String last_name,
        long scheduled_visits
) {}

