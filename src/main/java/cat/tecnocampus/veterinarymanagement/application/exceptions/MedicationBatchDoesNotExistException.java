package cat.tecnocampus.veterinarymanagement.application.exceptions;

public class MedicationBatchDoesNotExistException extends RuntimeException {
    public MedicationBatchDoesNotExistException(String message) {
        super(message);
    }
}
