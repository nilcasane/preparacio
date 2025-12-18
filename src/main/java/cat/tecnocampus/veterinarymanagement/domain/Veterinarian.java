package cat.tecnocampus.veterinarymanagement.domain;

import jakarta.persistence.*;
import java.util.List;

import cat.tecnocampus.veterinarymanagement.application.exceptions.VisitSlotUnavailableException;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.time.LocalDate;

@Entity
@Table(name = "veterinarian")
@PrimaryKeyJoinColumn(name = "person_id")
@NoArgsConstructor
public class Veterinarian extends Person {
    private Integer licenseNumber;
    private Integer yearsOfExperience;

    @OneToMany(mappedBy = "veterinarian", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Availability> availability;

    // Getters y setters
    public Integer getLicenseNumber() { return licenseNumber; }
    private void setLicenseNumber(Integer licenseNumber) {
        this.licenseNumber = licenseNumber; }
    public Integer getYearsOfExperience() { return yearsOfExperience; }
    private void setYearsOfExperience(Integer yearsOfExperience) { this.yearsOfExperience = yearsOfExperience; }
    public List<Availability> getAvailability() { return availability; }
    private void setAvailability(List<Availability> availability) { this.availability = availability; }
    
    public boolean isWorking(LocalDate date, LocalTime start, LocalTime end) {
        if (availability == null) return false;
        return availability.stream().anyMatch(a -> a.covers(date, start, end));
    }

    public void validateAvailability(LocalDate date, LocalTime start, int durationMinutes, List<Visit> existingVisits) {
        LocalTime end = start.plusMinutes(durationMinutes);
        
        if (!isWorking(date, start, end)) {
            throw new VisitSlotUnavailableException("Veterinarian has no available slot for the requested time");
        }

        boolean overlaps = existingVisits.stream()
                .anyMatch(v -> timeOverlaps(v.getVisitTime(), v.getVisitTime().plusMinutes(v.getDuration()), start, end));
        
        if (overlaps) {
            throw new VisitSlotUnavailableException("overlaps with another visit");
        }
    }

    private boolean timeOverlaps(LocalTime s1, LocalTime e1, LocalTime s2, LocalTime e2) {
        return s1.isBefore(e2) && s2.isBefore(e1);
    }
}

