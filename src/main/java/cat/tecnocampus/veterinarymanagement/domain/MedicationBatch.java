package cat.tecnocampus.veterinarymanagement.domain;

import cat.tecnocampus.veterinarymanagement.application.inputDTO.MedicationBatchCommand;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "medication_batch")
public class MedicationBatch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long lotNumber;
    private LocalDate receivedDate;
    private LocalDate expiryDate;
    private Integer initialQuantity;
    private Integer currentQuantity;
    private Double purchasePricePerUnit;

    @ManyToOne
    @JoinColumn(name = "medication_id")
    private Medication medication;

    public Long getId() { return id; }
    public Long getLotNumber() { return lotNumber; }
    public void setLotNumber(Long lotNumber) { this.lotNumber = lotNumber; }
    public LocalDate getReceivedDate() { return receivedDate; }
    public void setReceivedDate(LocalDate receivedDate) { this.receivedDate = receivedDate; }
    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }
    public Integer getInitialQuantity() { return initialQuantity; }
    public void setInitialQuantity(Integer initialQuantity) { this.initialQuantity = initialQuantity; }
    public Integer getCurrentQuantity() { return currentQuantity; }
    public void setCurrentQuantity(Integer currentQuantity) { this.currentQuantity = currentQuantity; }
    public Double getPurchasePricePerUnit() { return purchasePricePerUnit; }
    public void setPurchasePricePerUnit(Double purchasePricePerUnit) { this.purchasePricePerUnit = purchasePricePerUnit; }
    public Medication getMedication() { return medication; }
    public void setMedication(Medication medication) {
        this.medication = medication;
    }

    public void updateMedicationBatch(MedicationBatchCommand command) {
        setLotNumber(command.lot_number());
        setReceivedDate(LocalDate.parse(command.received_date()));
        setExpiryDate(LocalDate.parse(command.expiry_date()));
        setInitialQuantity(command.initial_quantity());
        setCurrentQuantity(command.current_quantity());
        setPurchasePricePerUnit(command.purchase_price_per_unit());
    }
}


