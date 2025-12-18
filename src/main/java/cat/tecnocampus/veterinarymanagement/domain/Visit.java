package cat.tecnocampus.veterinarymanagement.domain;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import cat.tecnocampus.veterinarymanagement.domain.exceptions.VisitInvalidStateException;
import cat.tecnocampus.veterinarymanagement.domain.exceptions.VisitStatusInvalidException;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "visit")
@NoArgsConstructor
public class Visit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate visitDate;
    private LocalTime visitTime;
    private Integer duration;
    @Column(name = "reason", columnDefinition = "text")
    private String reasonForVisit;
    @Column(columnDefinition = "text")
    private String diagnoses;
    @Column(columnDefinition = "text")
    private String notes;
    @Column(name = "pricer_per_fifteen")
    private Double pricerPerFifteen;

    @Enumerated(EnumType.STRING)
    private VisitStatus status;

    @ManyToOne
    @JoinColumn(name = "veterinarian_id")
    private Veterinarian veterinarian;

    @ManyToOne
    @JoinColumn(name = "pet_id")
    private Pet pet;

    @ManyToOne
    @JoinColumn(name = "pet_owner_id")
    private PetOwner petOwner;

    @OneToMany(mappedBy = "visit", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<MedicationPrescription> medicationPrescriptions;

    @ManyToOne
    @JoinColumn(name = "treatment_id")
    private Treatment treatment;

    @OneToOne(mappedBy = "visit", cascade = CascadeType.ALL, orphanRemoval = true)
    private Invoice invoice;

    public Visit(Pet pet, PetOwner petOwner, Veterinarian veterinarian, LocalDate visitDate, LocalTime visitTime, String reasonForVisit, Double pricerPerFifteen, Integer duration) {
        if (!petOwner.owns(pet)) {
            throw new IllegalArgumentException("Pet with id " + pet.getId() + " does not belong to owner with id " + petOwner.getId());
        }
        this.pet = pet;
        this.petOwner = petOwner;
        this.veterinarian = veterinarian;
        this.visitDate = visitDate;
        this.visitTime = visitTime;
        this.reasonForVisit = reasonForVisit;
        this.pricerPerFifteen = pricerPerFifteen;
        this.duration = duration;
        this.status = VisitStatus.SCHEDULED;
        this.medicationPrescriptions = new ArrayList<>();
    }
    
    /**
     * Creates a walk-in visit for the given pet, pet owner, veterinarian, and price per fifteen minutes.
     * 
     * @param pet the pet to be visited
     * @param petOwner the owner of the pet
     * @param veterinarian the veterinarian who will perform the visit
     * @param pricerPerFifteen the price per fifteen minutes of the visit
     * @return the created visit
     */
    public static Visit createWalkIn(Pet pet, PetOwner petOwner, Veterinarian veterinarian, Double pricerPerFifteen) {
        Visit visit = new Visit(pet, petOwner, veterinarian, LocalDate.now(), LocalTime.now(), "Walk-in visit", pricerPerFifteen, 30);
        visit.start();
        return visit;
    }

    /**
     * Starts the visit if it is in SCHEDULED state.
     * 
     * @throws VisitInvalidStateException if the visit is not in SCHEDULED state
     */
    public void start() {
        if (this.status != VisitStatus.SCHEDULED) {
            throw new VisitInvalidStateException("Cannot start a visit that is not scheduled.");
        }
        this.status = VisitStatus.IN_PROGRESS;
    }
    
    /**
     * Completes the visit if it is in IN_PROGRESS state.
     * 
     * @throws VisitInvalidStateException if the visit is not in IN_PROGRESS state
     */
    public void complete() {
        if (this.status != VisitStatus.IN_PROGRESS) {
            throw new VisitInvalidStateException("Cannot complete a visit that is not in progress.");
        }
        this.status = VisitStatus.COMPLETED;
    }
    
    /**
     * Cancels the visit if it is not in COMPLETED state.
     *
     * @return the history record of this action
     * @throws VisitInvalidStateException if the visit is in COMPLETED state
     */
    public VisitHistory cancel() {
        if (this.status != VisitStatus.SCHEDULED && this.status != VisitStatus.IN_PROGRESS) {
            throw new VisitInvalidStateException("Cannot cancel a visit that is not scheduled or in progress.");
        }
        this.status = VisitStatus.CANCELLED;
        
        return new VisitHistory(this.id, this.visitDate, this.visitTime, null, null, "CANCEL");
    }
    
    /**
     * Records the diagnosis and notes for the visit if it is in IN_PROGRESS or COMPLETED state.
     * 
     * @throws VisitInvalidStateException if the visit is not in IN_PROGRESS or COMPLETED state
     * @throws IllegalArgumentException if the diagnoses or notes are null or blank
     */
    public void recordDiagnosisAndNotes(String diagnoses, String notes) {
        if (this.status != VisitStatus.IN_PROGRESS && this.status != VisitStatus.COMPLETED) {
            throw new VisitInvalidStateException("Cannot record diagnosis and notes for a visit that is not in progress or completed.");
        }
        if (diagnoses == null || diagnoses.isBlank()) {
            throw new IllegalArgumentException("Diagnoses cannot be null or blank.");
        }
        if (notes == null || notes.isBlank()) {
            throw new IllegalArgumentException("Notes cannot be null or blank.");
        }
        this.diagnoses = diagnoses;
        this.notes = notes;
    }

    /**
     * Marks the visit as owner not showed up.
     * 
     * @throws VisitInvalidStateException if the visit is not in SCHEDULED state or if it is too early to mark as not showed up
     */
    public void markAsNotShowedUp() {
        if (this.status != VisitStatus.SCHEDULED) {
            throw new VisitInvalidStateException("Only SCHEDULED visits can be marked as not showed up");
        }
        java.time.LocalDateTime scheduled = java.time.LocalDateTime.of(this.visitDate, this.visitTime);
        if (java.time.LocalDateTime.now().isBefore(scheduled.plusMinutes(10))) {
            throw new VisitInvalidStateException("Cannot mark visit as not showed up before 10 minutes after scheduled time");
        }
        this.status = VisitStatus.NOT_SHOWED_UP;
    }

    /**
     * Reschedules the visit to a new date and time, potentially updating the duration.
     * 
     * @param newDate the new date of the visit
     * @param newTime the new time of the visit
     * @param duration the duration of the visit in minutes
     * @param performedBy the user performing the action
     * @return the history record of this action
     * @throws VisitInvalidStateException if the visit is not in SCHEDULED state
     */
    public VisitHistory reschedule(LocalDate newDate, LocalTime newTime, Integer duration, String performedBy) {
        if (this.status != VisitStatus.SCHEDULED) {
            throw new VisitInvalidStateException("Only visits with status 'SCHEDULED' can be rescheduled");
        }
        
        LocalDate oldDate = this.visitDate;
        LocalTime oldTime = this.visitTime;
        
        this.visitDate = newDate;
        this.visitTime = newTime;
        this.duration = duration;
        
        return new VisitHistory(this.id, oldDate, oldTime, newDate, newTime, "RESCHEDULE");
    }
    
    public void addPrescription(MedicationPrescription prescription) {
        this.medicationPrescriptions.add(prescription);
        prescription.setVisit(this);
    }

    /**
     * Assigns a treatment to the visit.
     * 
     * @param treatment the treatment to assign
     * @throws IllegalArgumentException if the treatment is null
     * @throws VisitStatusInvalidException if the visit is not in IN_PROGRESS or COMPLETED state
     */
    public void assignTreatment(Treatment treatment) {
        if (treatment == null) {
            throw new IllegalArgumentException("Treatment cannot be null");
        }

        if (this.status != VisitStatus.IN_PROGRESS && this.status != VisitStatus.COMPLETED) {
            throw new VisitStatusInvalidException("Visit must be IN_PROGRESS or COMPLETED to add treatments");
        }
        this.treatment = treatment;
    }

    /**
     * Unassigns the treatment from the visit.
     * 
     * @throws VisitStatusInvalidException if the visit is not in IN_PROGRESS or COMPLETED state
     */
    public void unassignTreatment() {
        if (this.status != VisitStatus.IN_PROGRESS && this.status != VisitStatus.COMPLETED) {
            throw new VisitStatusInvalidException("Visit must be IN_PROGRESS or COMPLETED to remove treatments");
        }
        this.treatment = null;
    }

    public void generateInvoice() {
        if (this.status != VisitStatus.COMPLETED) {
            throw new IllegalStateException("Visit must be completed before generating invoice.");
        }
        this.invoice = new Invoice(this);
    }

    // Getters
    public Long getId() { return id; }
    public LocalDate getVisitDate() { return visitDate; }
    public LocalTime getVisitTime() { return visitTime; }
    public Integer getDuration() { return duration; }
    public String getReasonForVisit() { return reasonForVisit; }
    public String getDiagnoses() { return diagnoses; }
    public String getNotes() { return notes; }
    public Double getPricerPerFifteen() { return pricerPerFifteen; }
    public VisitStatus getStatus() { return status; }
    public Veterinarian getVeterinarian() { return veterinarian; }
    public Pet getPet() { return pet; }
    public PetOwner getPetOwner() { return petOwner; }
    public List<MedicationPrescription> getMedicationPrescriptions() { return medicationPrescriptions; }
    public Treatment getTreatment() { return treatment; }
    public Invoice getInvoice() { return invoice; }
}
