package cat.tecnocampus.veterinarymanagement.domain.exceptions;

public class VisitNotCompletedException extends RuntimeException {
    public VisitNotCompletedException(String message) {
        super(message);
    }
}
