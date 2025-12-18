package cat.tecnocampus.veterinarymanagement.api;

import cat.tecnocampus.veterinarymanagement.application.VisitsService;
import cat.tecnocampus.veterinarymanagement.application.inputDTO.VisitCommand;
import cat.tecnocampus.veterinarymanagement.application.inputDTO.VisitDiagnosisCommand;
import cat.tecnocampus.veterinarymanagement.application.inputDTO.VisitRescheduleCommand;
import cat.tecnocampus.veterinarymanagement.application.inputDTO.MedicationPrescriptionCommand;
import cat.tecnocampus.veterinarymanagement.application.inputDTO.TreatmentCommand;
import cat.tecnocampus.veterinarymanagement.application.outputDTO.MedicationPrescriptionInformation;
import cat.tecnocampus.veterinarymanagement.application.outputDTO.VisitInformation;
import cat.tecnocampus.veterinarymanagement.application.outputDTO.TreatmentInformation;
import cat.tecnocampus.veterinarymanagement.application.outputDTO.VisitHistoryEntryInformation;
import cat.tecnocampus.veterinarymanagement.application.exceptions.TreatmentDoesNotExistException;

import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/visits")
public class VisitsController {
    private final VisitsService visitsService;

    public VisitsController(VisitsService visitsService) {
        this.visitsService = visitsService;
    }

    @GetMapping("/{visit_id}")
    public VisitInformation getVisit(@PathVariable Long visit_id) {
        return visitsService.getVisitById(visit_id);
    }

    @GetMapping
    public List<VisitInformation> getAllVisits() {
        return visitsService.getAllVisits();
    }

    @PostMapping
    public ResponseEntity<VisitInformation> createVisit(@RequestBody @Valid VisitCommand command,
                                                        UriComponentsBuilder uriBuilder) {
        Long id = visitsService.createVisit(command);
        var location = uriBuilder.path("/visits/{id}").buildAndExpand(id).toUri();
        VisitInformation info = visitsService.getVisitById(id);
        return ResponseEntity.created(location).body(info);
    }

    @DeleteMapping("/{visit_id}")
    public ResponseEntity<Void> deleteVisit(@PathVariable Long visit_id) {
        visitsService.deleteVisit(visit_id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{visit_id}/start")
    public VisitInformation startVisit(@PathVariable Long visit_id) {
        return visitsService.startVisit(visit_id);
    }

    @PostMapping("/{visit_id}/complete")
    public VisitInformation completeVisit(@PathVariable Long visit_id) {
        return visitsService.completeVisit(visit_id);
    }

    @PostMapping("/{visit_id}/diagnosis")
    public VisitInformation recordDiagnosisAndNotes(@PathVariable Long visit_id, @RequestBody @Valid VisitDiagnosisCommand command) {
        return visitsService.recordDiagnosisAndNotes(visit_id, command);
    }

    @PutMapping("/{visit_id}/cancel")
    public ResponseEntity<Void> cancelVisit(@PathVariable Long visit_id) {
        visitsService.cancelVisit(visit_id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{visit_id}/reschedule")
    public ResponseEntity<Void> rescheduleVisit(@PathVariable Long visit_id,
                                                @RequestBody @Valid VisitRescheduleCommand command) {
        visitsService.rescheduleVisit(visit_id, command.newDate(), command.newTime(), command.performedBy());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/walk-in")
    public ResponseEntity<VisitInformation> createWalkInVisit(
            @RequestParam Long petId,
            @RequestParam Long petOwnerId,
            UriComponentsBuilder uriBuilder) {
        Long id = visitsService.createWalkInVisit(petId, petOwnerId);
        var location = uriBuilder.path("/visits/{id}").buildAndExpand(id).toUri();
        VisitInformation info = visitsService.getVisitById(id);
        return ResponseEntity.created(location).body(info);
    }

    @PostMapping("/{visit_id}/owner-not-showed-up")
    public VisitInformation ownerDidNotShowUp(@PathVariable Long visit_id) {
        return visitsService.ownerNotShowedUp(visit_id);
    }

    /* Prescriptions */

    @PostMapping("/{visit_id}/prescriptions")
    public ResponseEntity<MedicationPrescriptionInformation> addMedicationPrescription(@PathVariable Long visit_id,
                                                                                       @RequestParam Long medication_id,
                                                                                       @RequestBody @Valid MedicationPrescriptionCommand command,
                                                                                       UriComponentsBuilder uriBuilder) {
        Long prescriptionId = visitsService.addMedicationPrescription(visit_id, medication_id, command);
        var location = uriBuilder.path("/visits/{visitId}/prescriptions/{id}")
                .buildAndExpand(visit_id, prescriptionId).toUri();
        MedicationPrescriptionInformation info = visitsService.getMedicationPrescriptionById(visit_id, prescriptionId);
        return ResponseEntity.created(location).body(info);
    }

    @GetMapping("/{visit_id}/prescriptions")
    public List<MedicationPrescriptionInformation> listMedicationPrescriptions(@PathVariable Long visit_id) {
        return visitsService.listMedicationPrescriptions(visit_id);
    }

    @GetMapping("/{visit_id}/prescriptions/{prescription_id}")
    public MedicationPrescriptionInformation getMedicationPrescription(@PathVariable Long visit_id,
                                                                       @PathVariable Long prescription_id) {
        return visitsService.getMedicationPrescriptionById(visit_id, prescription_id);
    }

    @GetMapping("/prescriptions/report")
    public ResponseEntity<List<Map<String, Object>>> listMedicationPrescriptionsByPeriod(
            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        List<Map<String, Object>> report = visitsService.listMedicationPrescriptionsByPeriod(from, to);
        return ResponseEntity.ok(report);
    }

    /**
    @PutMapping("/{visit_id}/prescriptions/{prescription_id}")
    public ResponseEntity<Void> updateMedicationPrescription(@PathVariable Long visit_id,
                                                             @PathVariable Long prescription_id,
                                                             @RequestBody @Valid MedicationPrescriptionCommand command) {
        visitsService.updateMedicationPrescription(visit_id, prescription_id, command);
        return ResponseEntity.noContent().build();
    }
    @DeleteMapping("/{visit_id}/prescriptions/{prescription_id}")
    public ResponseEntity<Void> deleteMedicationPrescription(@PathVariable Long visit_id,
                                                             @PathVariable Long prescription_id) {
        visitsService.deleteMedicationPrescription(visit_id, prescription_id);
        return ResponseEntity.noContent().build();
    }

    /* Treatments */

    @PostMapping("/treatments")
    public ResponseEntity<Void> createTreatment(@RequestBody @Valid TreatmentCommand command, UriComponentsBuilder uriBuilder) {
        Long id = visitsService.createTreatment(command); // Assume this returns the id
        var location = uriBuilder.path("/treatments/{id}").buildAndExpand(id).toUri();
        return ResponseEntity.created(location).build();
    }
 
    @GetMapping("/treatments")
    public List<TreatmentInformation> getTreatments() {
        return visitsService.getTreatments();
    }

    @GetMapping("/treatments/{treatment_id}")
    public TreatmentInformation getTreatment(@PathVariable Long treatment_id) {
        return visitsService.getTreatmentById(treatment_id)
                .orElseThrow(() -> new TreatmentDoesNotExistException("Treatment with id " + treatment_id + " does not exist"));
    }

    @PutMapping("/treatments/{treatment_id}")
    public TreatmentInformation updateTreatment(@PathVariable Long treatment_id,
                                                @RequestBody @Valid TreatmentCommand command) {
        return visitsService.updateTreatment(treatment_id, command);
    }

    @DeleteMapping("/treatments/{treatment_id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTreatment(@PathVariable Long treatment_id) {
        visitsService.deleteTreatment(treatment_id);
    }

    @PostMapping("/{visit_id}/treatment")
    public VisitInformation assignTreatmentToVisit(@PathVariable Long visit_id, @RequestParam Long treatment_id) {
        return visitsService.assignTreatmentToVisit(visit_id, treatment_id);
    }

    @GetMapping("/{visit_id}/treatment")
    public TreatmentInformation getVisitTreatment(@PathVariable Long visit_id) {
        return visitsService.getTreatmentFromVisit(visit_id);
    }

    @DeleteMapping("/{visit_id}/treatment")
    public VisitInformation unassignTreatmentFromVisit(@PathVariable Long visit_id) {
        return visitsService.unassignTreatmentFromVisit(visit_id);
    }

    @GetMapping("/pets/{pet_id}/medical-history")
    public List<VisitHistoryEntryInformation> getPetMedicalHistory(@PathVariable("pet_id") Long petId) {
        return visitsService.getPetMedicalHistory(petId);
    }

    /**
     * UC2.7 endpoint: List veterinarians ordered by number of prescriptions of a medication in a period.
     * GET /visits/veterinarians/report?medication_id=...&from=YYYY-MM-DD&to=YYYY-MM-DD
     */
    @GetMapping("/veterinarians/report")
    public ResponseEntity<List<Map<String, Object>>> getVeterinariansByMedicationPrescriptions(
            @RequestParam("medication_id") Long medicationId,
            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        List<Map<String, Object>> report = visitsService.listVeterinariansByMedicationPrescriptions(medicationId, from, to);
        return ResponseEntity.ok(report);
    }
}
