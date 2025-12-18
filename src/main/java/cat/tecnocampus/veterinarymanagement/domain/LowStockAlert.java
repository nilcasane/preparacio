package cat.tecnocampus.veterinarymanagement.domain;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "low_stock_alert")
public class LowStockAlert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate alertDate;
    private Boolean acknowledged;

    @OneToOne
    @JoinColumn(name = "medication_id")
    private Medication medication;

    public Long getId() { return id; }
    public void setId(Long medicationId) { this.id = medicationId; }
    public LocalDate getAlertDate() { return alertDate; }
    public void setAlertDate(LocalDate alertDate) { this.alertDate = alertDate; }
    public Boolean getAcknowledged() { return acknowledged; }
    public void setAcknowledged(Boolean acknowledged) { this.acknowledged = acknowledged; }
    public Medication getMedication() { return medication; }
    public void setMedication(Medication medication) {
        this.medication = medication;
    }
}