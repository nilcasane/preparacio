package cat.tecnocampus.veterinarymanagement.application.exceptions;

public class DiscountDoesNotExistException extends RuntimeException {
    public DiscountDoesNotExistException(String message) {
        super(message);
    }
}
