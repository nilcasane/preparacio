package cat.tecnocampus.veterinarymanagement.application.exceptions;

public class VisitSlotUnavailableException extends RuntimeException {
    public VisitSlotUnavailableException(String message) {
        super(message);
    }
}

