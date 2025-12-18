package cat.tecnocampus.veterinarymanagement.application.exceptions;

public class VisitDoesNotExistException extends RuntimeException {
    public VisitDoesNotExistException(String message) {
        super(message);
    }
}

