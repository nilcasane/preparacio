package cat.tecnocampus.veterinarymanagement.application.exceptions;

public class LowStockAlertDoesNotExistException extends RuntimeException {
    public LowStockAlertDoesNotExistException(String message) {
        super(message);
    }
}
