package cat.tecnocampus.veterinarymanagement.domain;

import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;

import java.util.List;

@Entity
@Table(name = "medication")
public class Medication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String activeIngredient;
    private Integer dosageUnit; // In milligrams
    private Double unitPrice;
    private Integer reorderThreshold;

    @OneToMany(mappedBy = "medication", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MedicationBatch> medicationBatches;

    @OneToMany(mappedBy = "medication", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MedicationPrescription> medicationPrescriptions;

    public Long getId() { return id; }
    public void setId(Long medicationId) { this.id = medicationId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getActiveIngredient() { return activeIngredient; }
    public void setActiveIngredient(String activeIngredient) { this.activeIngredient = activeIngredient; }
    public Integer getDosageUnit() { return dosageUnit; }
    public void setDosageUnit(int dosageUnit) { this.dosageUnit = dosageUnit; }
    public Double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }
    public Integer getReorderThreshold() { return reorderThreshold; }
    public void setReorderThreshold(int reorderThreshold) { this.reorderThreshold = reorderThreshold; }
    public List<MedicationPrescription> getMedicationPrescriptions() { return medicationPrescriptions; }
    public void setMedicationPrescriptions(List<MedicationPrescription> medicationPrescriptions) { this.medicationPrescriptions = medicationPrescriptions; }
}