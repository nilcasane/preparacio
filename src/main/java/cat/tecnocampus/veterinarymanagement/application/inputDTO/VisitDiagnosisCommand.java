package cat.tecnocampus.veterinarymanagement.application.inputDTO;

import jakarta.validation.constraints.NotBlank;

public record VisitDiagnosisCommand(
        @NotBlank(message = "Diagnoses cannot be blank") 
        String diagnoses,
        
        @NotBlank(message = "Notes cannot be blank") 
        String notes
) {}
