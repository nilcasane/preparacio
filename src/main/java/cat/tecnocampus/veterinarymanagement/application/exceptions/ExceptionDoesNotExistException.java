package cat.tecnocampus.veterinarymanagement.application.exceptions;

public class ExceptionDoesNotExistException extends RuntimeException {
    public ExceptionDoesNotExistException(String message) {
        super(message);
    }
}
