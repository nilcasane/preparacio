package cat.tecnocampus.veterinarymanagement.domain;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Entity
@Table(name = "visit_history")
public class VisitHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long visitId;

    private LocalDate oldDate;
    private LocalTime oldTime;

    private LocalDate newDate;
    private LocalTime newTime;

    private String action; // e.g. RESCHEDULE
    private String performedBy;
    private LocalDateTime createdAt;

    protected VisitHistory() {
        // Default constructor for JPA
    }

    public VisitHistory(Long visitId, LocalDate oldDate, LocalTime oldTime, LocalDate newDate, LocalTime newTime, String action) {
        this.visitId = visitId;
        this.oldDate = oldDate;
        this.oldTime = oldTime;
        this.newDate = newDate;
        this.newTime = newTime;
        this.action = action;
        this.createdAt = LocalDateTime.now();
    }

    // getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getVisitId() { return visitId; }
    public void setVisitId(Long visitId) { this.visitId = visitId; }

    public LocalDate getOldDate() { return oldDate; }
    public void setOldDate(LocalDate oldDate) { this.oldDate = oldDate; }

    public LocalTime getOldTime() { return oldTime; }
    public void setOldTime(LocalTime oldTime) { this.oldTime = oldTime; }

    public LocalDate getNewDate() { return newDate; }
    public void setNewDate(LocalDate newDate) { this.newDate = newDate; }

    public LocalTime getNewTime() { return newTime; }
    public void setNewTime(LocalTime newTime) { this.newTime = newTime; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getPerformedBy() { return performedBy; }
    public void setPerformedBy(String performedBy) { this.performedBy = performedBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

