package cat.tecnocampus.veterinarymanagement.application.outputDTO;

public record TreatmentInformation(
        Long id,
        String name,
        String description,
        Double cost
) {}
