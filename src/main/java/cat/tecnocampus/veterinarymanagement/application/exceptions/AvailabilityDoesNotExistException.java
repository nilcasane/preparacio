package cat.tecnocampus.veterinarymanagement.application.exceptions;

public class AvailabilityDoesNotExistException extends RuntimeException {
    public AvailabilityDoesNotExistException(String message) {
        super(message);
    }
}
