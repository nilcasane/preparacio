package cat.tecnocampus.veterinarymanagement.domain.exceptions;

public class VisitStatusInvalidException extends RuntimeException {
    public VisitStatusInvalidException(String message) {
        super(message);
    }
}

