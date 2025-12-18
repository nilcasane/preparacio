package cat.tecnocampus.veterinarymanagement.application.exceptions;

public class VeterinarianDoesNotExistException extends RuntimeException {
    public VeterinarianDoesNotExistException(String message) {
        super(message);
    }
}
