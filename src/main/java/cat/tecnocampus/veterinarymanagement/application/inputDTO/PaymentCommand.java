package cat.tecnocampus.veterinarymanagement.application.inputDTO;

import jakarta.validation.constraints.Pattern;

/**
 * Command for processing invoice payment.
 *
 * @param payment_method The method of payment (required)
 * @param transaction_reference The transaction reference identifier (optional)
 */
public record PaymentCommand(
        @Pattern(regexp = "^(CASH|CREDIT_CARD|DEBIT_CARD|BANK_TRANSFER)$", message = "Payment method must be one of: CASH, CREDIT_CARD, DEBIT_CARD, BANK_TRANSFER")
        String payment_method,

        String transaction_reference
) {
}

