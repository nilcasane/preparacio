package cat.tecnocampus.veterinarymanagement.domain;

import cat.tecnocampus.veterinarymanagement.application.inputDTO.ExceptionCommand;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "availability_exception")
public class AvailabilityException {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String reason;
    private int dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private LocalDate periodStart;
    private LocalDate periodEnd;

    @ManyToOne
    @JoinColumn(name = "availability_id")
    private Availability availability;

    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public int getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(int dayOfWeek) { this.dayOfWeek = dayOfWeek; }
    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
    public LocalDate getPeriodStart() { return periodStart; }
    public void setPeriodStart(LocalDate periodStart) { this.periodStart = periodStart; }
    public LocalDate getPeriodEnd() { return periodEnd; }
    public void setPeriodEnd(LocalDate periodEnd) { this.periodEnd = periodEnd; }
    public Availability getAvailability() { return availability; }
    public void setAvailability(Availability availability) { this.availability = availability; }

    public void updateException(ExceptionCommand command) {
        setReason(command.reason());
        setDayOfWeek(command.day_of_week());
        setStartTime(LocalTime.parse(command.start_time()));
        setEndTime(LocalTime.parse(command.end_time()));
        setPeriodStart(LocalDate.parse(command.period_start()));
        setPeriodEnd(LocalDate.parse(command.period_end()));
    }
}

