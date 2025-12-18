package cat.tecnocampus.veterinarymanagement.domain;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "payment")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "invoice_id", nullable = false, unique = true)
    private Invoice invoice;

    private Double amount;
    private LocalDate paymentDate;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    private String transactionReference;

    /**
     *  Default constructor for JPA.
     */
    public Payment() {}

    /**
     *  Constructs a Payment associated with the given Invoice.
     *
     *  @param invoice The invoice being paid
     *  @param paymentMethod The method of payment
     *  @param transactionReference The reference for the transaction
     */
    public Payment(Invoice invoice, PaymentMethod paymentMethod, String transactionReference) {
        this.invoice = invoice;
        this.paymentMethod = paymentMethod;
        this.transactionReference = transactionReference;
        this.paymentDate = LocalDate.now();
        this.amount = invoice.getTotalAmount();
    }

    /**
     * Gets the unique identifier of this payment.
     *
     * @return The payment ID
     */
    public Long getId() {
        return id;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public Double getAmount() {
        return amount;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public Invoice getInvoice() {
        return invoice;
    }

}
