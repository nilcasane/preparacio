package cat.tecnocampus.veterinarymanagement.application.exceptions;

public class PetDoesNotExistException extends RuntimeException {
    public PetDoesNotExistException(String message) {
        super(message);
    }
}

