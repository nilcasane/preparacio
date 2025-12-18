package cat.tecnocampus.veterinarymanagement.application.exceptions;

public class MedicationIncompatibilityDoesNotExistException extends RuntimeException {
    public MedicationIncompatibilityDoesNotExistException(String message) {
        super(message);
    }
}

