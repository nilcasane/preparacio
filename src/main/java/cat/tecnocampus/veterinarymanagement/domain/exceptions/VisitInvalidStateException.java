package cat.tecnocampus.veterinarymanagement.domain.exceptions;

public class VisitInvalidStateException extends RuntimeException {
    public VisitInvalidStateException(String message) {
        super(message);
    }
}
