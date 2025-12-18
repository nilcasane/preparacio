package cat.tecnocampus.veterinarymanagement.application.outputDTO;

public record VeterinarianInformation (
    Long vet_id,
    String username,
    String first_name,
    String last_name,
    String phone_number,
    String email,
    String address,
    String license_number,
    Integer years_of_experience
) {
}

