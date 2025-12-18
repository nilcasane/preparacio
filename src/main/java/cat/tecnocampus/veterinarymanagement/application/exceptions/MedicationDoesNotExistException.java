package cat.tecnocampus.veterinarymanagement.application.exceptions;

public class MedicationDoesNotExistException extends RuntimeException {
    public MedicationDoesNotExistException(String message) {
        super(message);
    }
}
