package cat.tecnocampus.veterinarymanagement.application.exceptions;

public class NotEnoughMedicationInBatchesException extends RuntimeException {
    public NotEnoughMedicationInBatchesException(String message) {
        super(message);
    }
}
