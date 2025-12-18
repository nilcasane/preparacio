package cat.tecnocampus.veterinarymanagement.domain.exceptions;

public class InvoiceAlreadyPaidException extends RuntimeException {
    public InvoiceAlreadyPaidException(String message) {
        super(message);
    }
}
