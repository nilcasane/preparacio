package cat.tecnocampus.veterinarymanagement.application.exceptions;

public class LowStockAlertDoesAlreadyExistException extends RuntimeException {
    public LowStockAlertDoesAlreadyExistException(String message) {
        super(message);
    }
}
