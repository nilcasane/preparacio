package cat.tecnocampus.veterinarymanagement.application.exceptions;

public class InvoiceDoesNotExistException extends RuntimeException {
    public InvoiceDoesNotExistException(String message) {
        super(message);
    }
}
