package cat.tecnocampus.veterinarymanagement.application.exceptions;

public class TreatmentDoesNotExistException extends RuntimeException {
    public TreatmentDoesNotExistException(String message) {
        super(message);
    }
}
