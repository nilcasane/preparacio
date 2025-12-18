package cat.tecnocampus.veterinarymanagement.application.exceptions;

public class PromotionDoesNotExistException extends RuntimeException {
    public PromotionDoesNotExistException(String message) {
        super(message);
    }
}
