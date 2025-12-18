package cat.tecnocampus.veterinarymanagement.application.exceptions;

public class StockNotAvailableException extends RuntimeException {
    public StockNotAvailableException(String message) {
        super(message);
    }
}

