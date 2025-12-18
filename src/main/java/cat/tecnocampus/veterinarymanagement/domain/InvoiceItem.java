package cat.tecnocampus.veterinarymanagement.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "invoice_item")
public class InvoiceItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "invoice_id")
    private Invoice invoice;

    private String description;
    private Integer quantity;
    private Double unitPrice;
    private Double totalPrice;

    @ManyToOne
    @JoinColumn(name = "medication_prescription_id")
    private MedicationPrescription medicationPrescription;

    @ManyToOne
    @JoinColumn(name = "treatment_id")
    private Treatment treatment;

    // Constructors
    public InvoiceItem() {}

    public InvoiceItem(Invoice invoice) {
        this.invoice = invoice;
    }

    public InvoiceItem(Invoice invoice, String description, Integer quantity, Double unitPrice, Double totalPrice) {
        this.invoice = invoice;
        this.description = description;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = totalPrice;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public String getDescription() {
        return description;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Double getUnitPrice() {
        return unitPrice;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public MedicationPrescription getMedicationPrescription() {
        return medicationPrescription;
    }

    public Treatment getTreatment() {
        return treatment;
    }

    /**
     * Set medication prescription and automatically import its data (name, price, quantity).
     * This ensures medication and treatment are mutually exclusive.
     */
    public void importFromMedicationPrescription(MedicationPrescription medicationPrescription) {
        this.medicationPrescription = medicationPrescription;
        this.treatment = null; // Ensure mutual exclusivity

        if (medicationPrescription != null) {
            Medication medication = medicationPrescription.getMedication();
            this.description = medication.getName() != null ? medication.getName() : "Medication Prescription";
            this.unitPrice = medication.getUnitPrice() != null ? medication.getUnitPrice() : 0.0;
            this.quantity = medicationPrescription.getQuantityPrescribed() != null ? medicationPrescription.getQuantityPrescribed() : 1;
            this.totalPrice = this.unitPrice * this.quantity;
        }
    }

    /**
     * Set treatment and automatically import its data (name, cost).
     * This ensures treatment and medication are mutually exclusive.
     */
    public void importFromTreatment(Treatment treatment) {
        this.treatment = treatment;
        this.medicationPrescription = null; // Ensure mutual exclusivity

        if (treatment != null) {
            this.description = treatment.getName() != null ? treatment.getName() : "Treatment";
            this.quantity = 1;
            this.unitPrice = treatment.getCost() != null ? treatment.getCost() : 0.0;
            this.totalPrice = this.unitPrice;
        }
    }
}
