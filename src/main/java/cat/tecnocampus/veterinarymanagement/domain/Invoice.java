package cat.tecnocampus.veterinarymanagement.domain;

import cat.tecnocampus.veterinarymanagement.domain.exceptions.InvoiceAlreadyPaidException;
import cat.tecnocampus.veterinarymanagement.domain.exceptions.VisitNotCompletedException;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "invoice")
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate invoiceDate;
    private Double totalAmount;

    @Enumerated(EnumType.STRING)
    private InvoiceStatus status;

    @ManyToOne
    @JoinColumn(name = "pet_owner_id")
    private PetOwner petOwner;

    @OneToOne
    @JoinColumn(name = "visit_id", unique = true)
    private Visit visit;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<InvoiceItem> items;

    @OneToOne
    @JoinColumn(name = "invoice_id", unique = true)
    private Payment payment;

    // Constructors
    protected Invoice() {}

    /**
     * Creates an invoice for the given visit, importing items from the visit.
     *
     * @param visit The visit associated with the invoice
     * @throws VisitNotCompletedException if the visit is not in COMPLETED status
     */
    public Invoice(Visit visit) {
        if (visit.getStatus() != VisitStatus.COMPLETED) {
            throw new VisitNotCompletedException("Cannot create invoice for visit with id " + visit.getId() + " because its status is not COMPLETED");
        }
        this.visit = visit;
        this.petOwner = visit.getPetOwner(); // Link invoice to the pet owner
        this.invoiceDate = LocalDate.now();
        this.status = InvoiceStatus.UNPAID;
        this.items = new ArrayList<>();
        importItemsFromVisit(visit);
    }

    // Getters
    public Long getId() {
        return id;
    }

    public LocalDate getInvoiceDate() {
        return invoiceDate;
    }
    
    public Double getTotalAmount() {
        return totalAmount;
    }

    public InvoiceStatus getStatus() {
        return status;
    }
    
    public PetOwner getPetOwner() {
        return petOwner;
    }

    public Visit getVisit() {
        return visit;
    }

    public List<InvoiceItem> getItems() {
        return items;
    }

    /**
     * Imports invoice items from the associated visit, including treatment and medication prescriptions.
     *
     * @param visit The visit from which to import invoice items
     */
    private void importItemsFromVisit(Visit visit) {
        // Clear existing items to avoid duplicates
        this.items.clear();

        // Add treatment as an invoice item
        Treatment treatment = visit.getTreatment();
        if (treatment != null) {
            InvoiceItem treatItem = new InvoiceItem(this);
            treatItem.importFromTreatment(treatment);
            this.items.add(treatItem);
        }

        // Add medication prescriptions as invoice items
        if (visit.getMedicationPrescriptions() != null) {
            for (MedicationPrescription mp : visit.getMedicationPrescriptions()) {
                if (mp == null) continue;
                InvoiceItem medItem = new InvoiceItem(this);
                medItem.importFromMedicationPrescription(mp);
                this.items.add(medItem);
            }
        }

        // Recalculate total amount
        recalculateTotal();
    }

    private void recalculateTotal() {
        double total = 0.0;
        for (InvoiceItem it : this.items) {
            if (it.getTotalPrice() != null) total += it.getTotalPrice();
        }
        this.totalAmount = total;
    }

    /**
     * Processes payment for the invoice using the specified payment method and transaction reference.
     *
     * @param paymentMethod The method of payment
     * @param transactionReference The transaction reference identifier
     * @throws InvoiceAlreadyPaidException if the invoice is already paid
     */
    public void pay(PaymentMethod paymentMethod, String transactionReference) {
        if (this.payment != null || this.status == InvoiceStatus.PAID) {
            throw new InvoiceAlreadyPaidException("Invoice is already paid.");
        }

        this.payment = new Payment(this, paymentMethod, transactionReference);
        this.status = InvoiceStatus.PAID;
    }
}
