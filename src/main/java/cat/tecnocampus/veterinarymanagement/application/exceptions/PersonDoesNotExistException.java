package cat.tecnocampus.veterinarymanagement.application.exceptions;

public class PersonDoesNotExistException extends RuntimeException {
    public PersonDoesNotExistException(String message) {
        super(message);
    }
}
