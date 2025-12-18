package cat.tecnocampus.veterinarymanagement.domain;

import cat.tecnocampus.veterinarymanagement.application.inputDTO.AvailabilityCommand;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "availability")
@NoArgsConstructor
public class Availability {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private LocalDate periodStart;
    private LocalDate periodEnd;

    @ManyToOne
    @JoinColumn(name = "veterinarian_id")
    private Veterinarian veterinarian;

    @OneToMany(mappedBy = "availability", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AvailabilityException> exceptions;

    // Constructor
    public Availability(int dayOfWeek, LocalTime startTime, LocalTime endTime, LocalDate periodStart, LocalDate periodEnd, Veterinarian veterinarian) {
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
        this.veterinarian = veterinarian;
    }

    // Getters y setters
    public Long getId() { return id; }
    public int getDayOfWeek() { return dayOfWeek; }
    private void setDayOfWeek(int dayOfWeek) { this.dayOfWeek = dayOfWeek; }
    public LocalTime getStartTime() { return startTime; }
    private void setStartTime(LocalTime startTime) { this.startTime = startTime; }
    public LocalTime getEndTime() { return endTime; }
    private void setEndTime(LocalTime endTime) { this.endTime = endTime; }
    public LocalDate getPeriodStart() { return periodStart; }
    private void setPeriodStart(LocalDate periodStart) { this.periodStart = periodStart; }
    public LocalDate getPeriodEnd() { return periodEnd; }
    private void setPeriodEnd(LocalDate periodEnd) { this.periodEnd = periodEnd; }
    public Veterinarian getVeterinarian() { return veterinarian; }
    public List<AvailabilityException> getExceptions() { return exceptions; }
    private void setExceptions(List<AvailabilityException> exceptions) { this.exceptions = exceptions; }

    public void updateAvailability(AvailabilityCommand command) {
        setDayOfWeek(command.day_of_week());
        setStartTime(LocalTime.parse(command.start_time()));
        setEndTime(LocalTime.parse(command.end_time()));
        setPeriodStart(LocalDate.parse(command.period_start()));
        setPeriodEnd(LocalDate.parse(command.period_end()));
    }

    public void assignVeterinarian(Veterinarian veterinarian) { 
        this.veterinarian = veterinarian;
    }

    public boolean covers(LocalDate date, LocalTime start, LocalTime end) {
        if (date.isBefore(periodStart) || date.isAfter(periodEnd)) return false;
        if (date.getDayOfWeek().getValue() != dayOfWeek) return false;
        if (start.isBefore(startTime) || end.isAfter(endTime)) return false;
        
        return !hasOverlappingException(date, start, end);
    }

    private boolean hasOverlappingException(LocalDate date, LocalTime start, LocalTime end) {
        if (exceptions == null) return false;
        return exceptions.stream()
                .filter(e -> !date.isBefore(e.getPeriodStart()) && !date.isAfter(e.getPeriodEnd()))
                .filter(e -> e.getDayOfWeek() == date.getDayOfWeek().getValue())
                .anyMatch(e -> timeOverlaps(e.getStartTime(), e.getEndTime(), start, end));
    }

    private boolean timeOverlaps(LocalTime s1, LocalTime e1, LocalTime s2, LocalTime e2) {
        return s1.isBefore(e2) && s2.isBefore(e1);
    }
}

