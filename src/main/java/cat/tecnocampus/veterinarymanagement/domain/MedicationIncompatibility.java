package cat.tecnocampus.veterinarymanagement.domain;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "medication_incompatibility", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"medication_a_id", "medication_b_id"})
})
public class MedicationIncompatibility {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "medication_a_id", nullable = false)
    private Medication medicationA;

    @ManyToOne
    @JoinColumn(name = "medication_b_id", nullable = false)
    private Medication medicationB;

    // Optional persisting period end date (null = indefinite)
    private LocalDate persistsUntil;

    // Description of the incompatibility
    private String description;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Medication getMedicationA() { return medicationA; }
    public void setMedicationA(Medication medicationA) { this.medicationA = medicationA; }

    public Medication getMedicationB() { return medicationB; }
    public void setMedicationB(Medication medicationB) { this.medicationB = medicationB; }

    public LocalDate getPersistsUntil() { return persistsUntil; }
    public void setPersistsUntil(LocalDate persistsUntil) { this.persistsUntil = persistsUntil; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
