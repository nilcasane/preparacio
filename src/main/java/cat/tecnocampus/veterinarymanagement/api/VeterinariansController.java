package cat.tecnocampus.veterinarymanagement.api;

import cat.tecnocampus.veterinarymanagement.application.VeterinariansService;
import cat.tecnocampus.veterinarymanagement.application.VisitsService;
import cat.tecnocampus.veterinarymanagement.application.exceptions.AvailabilityDoesNotExistException;
import cat.tecnocampus.veterinarymanagement.application.exceptions.ExceptionDoesNotExistException;
import cat.tecnocampus.veterinarymanagement.application.inputDTO.ExceptionCommand;
import cat.tecnocampus.veterinarymanagement.application.outputDTO.AvailabilityExceptionInformation;
import cat.tecnocampus.veterinarymanagement.application.outputDTO.AvailabilityInformation;
import cat.tecnocampus.veterinarymanagement.application.outputDTO.VeterinarianDemandInformation;
import cat.tecnocampus.veterinarymanagement.application.outputDTO.VeterinarianScheduleInformation;
import cat.tecnocampus.veterinarymanagement.application.outputDTO.VisitInformation;
import cat.tecnocampus.veterinarymanagement.application.inputDTO.AvailabilityCommand;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/veterinarians")
public class VeterinariansController {
    private final VeterinariansService veterinariansService;
    private final VisitsService visitsService;

    public VeterinariansController(VeterinariansService veterinariansService, VisitsService visitsService) {
        this.veterinariansService = veterinariansService;
        this.visitsService = visitsService;
    }

    /**
     * Create a new availability for a Veterinarian
     * @param vet_id Veterinarian ID
     * @param input Availability input
     * @return The created Availability
     */
    @PostMapping("/{vet_id}/availabilities")
    public ResponseEntity<AvailabilityInformation> createAvailability(
            @PathVariable Long vet_id,
            @RequestBody @Valid AvailabilityCommand input,
            UriComponentsBuilder uriBuilder) {
        Long id = veterinariansService.createAvailability(vet_id, input);
        var location = uriBuilder.path("/availabilities/{id}").buildAndExpand(id).toUri();
        AvailabilityInformation info = veterinariansService.getAvailabilityById(id).orElseThrow();
        return ResponseEntity.created(location).body(info);
    }

    /**
     * Get all availabilities from a Veterinarian
     * @param vet_id Veterinarian ID
     * @return List of Availabilities
     */
    @GetMapping("/{vet_id}/availabilities")
    public List<AvailabilityInformation> getAvailabilities(
            @PathVariable Long vet_id) {
        return veterinariansService.getAvailabilities(vet_id);
    }

    /**
     * Delete all availabilities from a Veterinarian
     * @param vet_id Veterinarian ID
     */
    @DeleteMapping("/{vet_id}/availabilities")
    public ResponseEntity<Void> deleteAvailabilities(
            @PathVariable Long vet_id) {
        veterinariansService.deleteAvailabilities(vet_id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get schedule for a veterinarian between start and end dates (inclusive)
     */
    @GetMapping("/{vet_id}/schedule")
    public ResponseEntity<VeterinarianScheduleInformation> getSchedule(
            @PathVariable Long vet_id,
            @RequestParam String start,
            @RequestParam String end) {
        LocalDate startDate;
        LocalDate endDate;
        try {
            startDate = LocalDate.parse(start);
            endDate = LocalDate.parse(end);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
        if (endDate.isBefore(startDate)) {
            return ResponseEntity.badRequest().build();
        }

        // Get visits and availabilities
        List<VisitInformation> visits = visitsService.getVisitsForVeterinarianAndRange(vet_id, startDate, endDate);
        List<AvailabilityInformation> availabilities = veterinariansService.getAvailabilities(vet_id);

        VeterinarianScheduleInformation schedule = new VeterinarianScheduleInformation(vet_id, startDate.toString(), endDate.toString(), visits, availabilities);
        return ResponseEntity.ok(schedule);
    }


    // Availabilities
    /**
     * Get an availability
     * @param availability_id Availability ID
     * @return The Availability
     */
    @GetMapping("/{vet_id}/availabilities/{availability_id}")
    public AvailabilityInformation getAvailability(
            @PathVariable Long availability_id) {
        return veterinariansService
                .getAvailabilityById(availability_id)
                .orElseThrow(() -> new AvailabilityDoesNotExistException("Availability with id " + availability_id + " does not exist"));
    }

    /**
     * Update an availability
     * @param availability_id Availability ID
     * @param input Availability input
     * @return The updated Availability
     */
    @PutMapping("/{vet_id}/availabilities/{availability_id}")
    public ResponseEntity<AvailabilityInformation> updateAvailability(
            @PathVariable Long availability_id,
            @RequestBody AvailabilityCommand input) {
        AvailabilityInformation info = veterinariansService.updateAvailability(availability_id, input);
        return ResponseEntity.ok(info);
    }

    /**
     * Delete an availability
     * @param availability_id Availability ID
     */
    @DeleteMapping("/{vet_id}/availabilities/{availability_id}")
    public ResponseEntity<Void> deleteAvailability(
            @PathVariable Long availability_id) {
        veterinariansService.deleteAvailability(availability_id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Create a new exception for an availability
     * @param availability_id Availability ID
     * @param input Exception input
     * @return The created Exception
     */
    @PostMapping("/{vet_id}/availabilities/{availability_id}/exceptions")
    public ResponseEntity<AvailabilityExceptionInformation> createException(
            @PathVariable Long availability_id,
            @RequestBody ExceptionCommand input,
            UriComponentsBuilder uriBuilder) {
        Long id = veterinariansService.createException(availability_id, input);
        var location = uriBuilder.path("/exceptions/{id}").buildAndExpand(id).toUri();
        AvailabilityExceptionInformation info = veterinariansService.getExceptionById(id)
                .orElseThrow(() -> new ExceptionDoesNotExistException("Exception with id " + id + " does not exist"));
        return ResponseEntity.created(location).body(info);
    }

    /**
     * Get all exceptions from an Availability
     * @param availability_id Availability ID
     * @return List of Exceptions
     */
    @GetMapping("/{vet_id}/availabilities/{availability_id}/exceptions")
    public List<AvailabilityExceptionInformation> getExceptions(
            @PathVariable Long availability_id) {
        return veterinariansService.getExceptions(availability_id);
    }

    /**
     * Delete all exceptions from an Availability
     * @param availability_id Availability ID
     */
    @DeleteMapping("/{vet_id}/availabilities/{availability_id}/exceptions")
    public ResponseEntity<Void> deleteExceptions(
            @PathVariable Long availability_id) {
        veterinariansService.deleteExceptions(availability_id);
        return ResponseEntity.noContent().build();
    }
    
    // Availability Exceptions
    /**
     * Get an exception
     * @param exception_id Exception ID
     * @return The Exception
     */
    @GetMapping("/{vet_id}/availabilities/{availability_id}/exceptions/{exception_id}")
    public AvailabilityExceptionInformation getException(
            @PathVariable Long exception_id) {
        return veterinariansService
                .getExceptionById(exception_id)
                .orElseThrow(() -> new ExceptionDoesNotExistException("Exception with id " + exception_id + " does not exist"));
    }

    /**
     * Update an exception
     * @param exception_id Exception ID
     * @param input Exception input
     * @return The updated Exception
     */
    @PutMapping("/{vet_id}/availabilities/{availability_id}/exceptions/{exception_id}")
    public ResponseEntity<AvailabilityExceptionInformation> updateException(
            @PathVariable Long exception_id,
            @RequestBody ExceptionCommand input) {
        AvailabilityExceptionInformation info = veterinariansService.updateException(exception_id, input);
        return ResponseEntity.ok(info);
    }

    /**
     * Delete an exception
     * @param exception_id Exception ID
     */
    @DeleteMapping("/{vet_id}/availabilities/{availability_id}/exceptions/{exception_id}")
    public ResponseEntity<Void> deleteException(
            @PathVariable Long exception_id) {
        veterinariansService.deleteException(exception_id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/demand")
    public ResponseEntity<List<VeterinarianDemandInformation>> getVeterinariansDemand(
            @RequestParam String start,
            @RequestParam String end) {
        List<VeterinarianDemandInformation> result = veterinariansService.getVeterinariansDemand(start, end);
        return ResponseEntity.ok(result);
    }

}
