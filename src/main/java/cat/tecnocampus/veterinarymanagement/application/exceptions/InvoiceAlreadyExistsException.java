package cat.tecnocampus.veterinarymanagement.application.exceptions;

public class InvoiceAlreadyExistsException extends RuntimeException {
    public InvoiceAlreadyExistsException(String message) {
        super(message);
    }
}
