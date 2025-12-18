package cat.tecnocampus.veterinarymanagement.application.exceptions;

public class LoyaltyTierDoesNotExistException extends RuntimeException {
    public LoyaltyTierDoesNotExistException(String message) {
        super(message);
    }
}
