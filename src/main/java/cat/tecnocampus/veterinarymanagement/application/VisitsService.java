package cat.tecnocampus.veterinarymanagement.application;

import cat.tecnocampus.veterinarymanagement.application.exceptions.*;
import cat.tecnocampus.veterinarymanagement.application.inputDTO.MedicationPrescriptionCommand;
import cat.tecnocampus.veterinarymanagement.application.inputDTO.VisitCommand;
import cat.tecnocampus.veterinarymanagement.application.inputDTO.VisitDiagnosisCommand;
import cat.tecnocampus.veterinarymanagement.application.inputDTO.TreatmentCommand;
import cat.tecnocampus.veterinarymanagement.application.mappers.VisitMapper;
import cat.tecnocampus.veterinarymanagement.application.mappers.VisitRescheduleMapper;
import cat.tecnocampus.veterinarymanagement.application.mappers.MedicationPrescriptionMapper;
import cat.tecnocampus.veterinarymanagement.application.mappers.TreatmentMapper;
import cat.tecnocampus.veterinarymanagement.application.outputDTO.MedicationPrescriptionInformation;
import cat.tecnocampus.veterinarymanagement.application.outputDTO.VisitInformation;
import cat.tecnocampus.veterinarymanagement.application.outputDTO.TreatmentInformation;
import cat.tecnocampus.veterinarymanagement.application.outputDTO.VisitRescheduleInformation;
import cat.tecnocampus.veterinarymanagement.application.outputDTO.VisitHistoryEntryInformation;
import cat.tecnocampus.veterinarymanagement.domain.*;
import cat.tecnocampus.veterinarymanagement.domain.exceptions.VisitStatusInvalidException;
import cat.tecnocampus.veterinarymanagement.persistence.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessApiUsageException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@Service
public class VisitsService {
    private final VisitRepository visitRepository;
    private final PersonRepository personRepository;
    private final MedicationRepository medicationRepository;
    private final PetRepository petRepository;
    private final VisitHistoryRepository visitHistoryRepository;
    private final MedicationsService medicationsService;
    private final VeterinariansService veterinariansService;
    private final MedicationIncompatibilityRepository medicationIncompatibilityRepository;
    private final TreatmentRepository treatmentRepository;

    @Autowired
    public VisitsService(VisitRepository visitRepository,
                         PersonRepository personRepository,
                         MedicationRepository medicationRepository,
                         PetRepository petRepository,
                         VisitHistoryRepository visitHistoryRepository,
                         VeterinariansService veterinariansService,
                         MedicationsService medicationsService,
                         MedicationIncompatibilityRepository medicationIncompatibilityRepository,
                         TreatmentRepository treatmentRepository) {
        this.visitRepository = visitRepository;
        this.personRepository = personRepository;
        this.medicationRepository = medicationRepository;
        this.petRepository = petRepository;
        this.visitHistoryRepository = visitHistoryRepository;
        this.medicationsService = medicationsService;
        this.veterinariansService = veterinariansService;
        this.medicationIncompatibilityRepository = medicationIncompatibilityRepository;
        this.treatmentRepository = treatmentRepository;
    }

    public VisitInformation getVisitById(Long visitId) {
        Visit visit = visitRepository.findById(visitId)
                .orElseThrow(() -> new VisitDoesNotExistException("Visit with id " + visitId + " does not exist"));
        return VisitMapper.toVisitInformation(visit);
    }

    public List<VisitInformation> getAllVisits() {
        return ((List<Visit>) visitRepository.findAll()).stream()
                .map(VisitMapper::toVisitInformation)
                .toList();
    }

    @Transactional
    public Long createVisit(VisitCommand command) {
        Veterinarian vet = personRepository.findVeterinarianById(command.veterinarian_id())
                .orElseThrow(() -> new VeterinarianDoesNotExistException("Veterinarian with id " + command.veterinarian_id() + " does not exist"));
        // validate pet and pet owner: fetch pet owner entity explicitly and check relation
        Pet pet = petRepository.findById(command.pet_id())
                .orElseThrow(() -> new PetDoesNotExistException("Pet with id " + command.pet_id() + " does not exist"));
        PetOwner petOwner = personRepository.findPetOwnerById(command.pet_owner_id())
                .orElseThrow(() -> new PetDoesNotExistException("Pet owner with id " + command.pet_owner_id() + " does not exist"));

        LocalDate date = LocalDate.parse(command.visit_date());
        LocalTime time = LocalTime.parse(command.visit_time());
        int duration = command.duration() != null ? command.duration() : 15;

        // Check availability and overlaps using domain logic
        List<Visit> visitList = visitRepository.findByVeterinarianAndVisitDate(vet, date);
        vet.validateAvailability(date, time, duration, visitList);

        Visit visit = new Visit(pet, petOwner, vet, date, time, command.reasonForVisit(), command.price_per_fifteen(), duration);
        visitRepository.save(visit);
        return visit.getId();
    }

    @Transactional
    public void deleteVisit(Long visitId) {
        if (!visitRepository.existsById(visitId)) {
            throw new VisitDoesNotExistException("Visit with id " + visitId + " does not exist");
        }
        visitRepository.deleteById(visitId);
    }

    @Transactional
    public Long createWalkInVisit(Long petId, Long petOwnerId) {
        var pet = petRepository.findById(petId)
                .orElseThrow(() -> new PetDoesNotExistException("Pet with id " + petId + " does not exist"));
        var owner = personRepository.findPetOwnerById(petOwnerId)
                .orElseThrow(() -> new PetDoesNotExistException("Pet owner with id " + petOwnerId + " does not exist"));
        int duration = 30; // duración por defecto para walk-in

        Veterinarian assigned = findAvailableVeterinarianNow(duration)
                .orElseThrow(() -> new VisitSlotUnavailableException("No veterinarian available now for walk-in"));

        Double pricerPerFifteen = 0.0;
        Visit visit = Visit.createWalkIn(pet, owner, assigned, pricerPerFifteen);
        var saved = visitRepository.save(visit);
        return saved.getId();
    }

    @Transactional
    public VisitInformation startVisit(Long visitId) {
        try {
            Visit visit = visitRepository.findById(visitId)
                    .orElseThrow(() -> new VisitDoesNotExistException("Visit with id " + visitId + " does not exist"));
            
            visit.start();
            visitRepository.save(visit);
            return VisitMapper.toVisitInformation(visit);
        } catch (InvalidDataAccessApiUsageException e) {
            throw new IllegalArgumentException("Error starting visit with id " + visitId + " does not exist");
        }
    }

    @Transactional
    public VisitInformation completeVisit(Long visitId) {
        try {
            Visit visit = visitRepository.findById(visitId)
                    .orElseThrow(() -> new VisitDoesNotExistException("Visit with id " + visitId + " does not exist"));
            
            visit.complete();
            visitRepository.save(visit);
            return VisitMapper.toVisitInformation(visit);
        } catch (InvalidDataAccessApiUsageException e) {
            throw new IllegalArgumentException("Error starting visit with id " + visitId + " does not exist");
        }
    }

    @Transactional
    public void cancelVisit(Long visitId) {
        Visit visit = visitRepository.findById(visitId)
                .orElseThrow(() -> new VisitDoesNotExistException("Visit with id " + visitId + " does not exist"));
        VisitHistory history = visit.cancel();
        visitRepository.save(visit);
        visitHistoryRepository.save(history);
    }

    @Transactional
    public VisitInformation recordDiagnosisAndNotes(Long visitId, VisitDiagnosisCommand command) {
        Visit visit = visitRepository.findById(visitId)
                .orElseThrow(() -> new VisitDoesNotExistException("Visit with id " + visitId + " does not exist"));
        visit.recordDiagnosisAndNotes(command.diagnoses(), command.notes());
        visitRepository.save(visit);
        return VisitMapper.toVisitInformation(visit);
    }

    @Transactional
    public VisitInformation ownerNotShowedUp(Long visitId) {
        Visit visit = visitRepository.findById(visitId)
                .orElseThrow(() -> new VisitDoesNotExistException("Visit with id " + visitId + " does not exist"));

        visit.markAsNotShowedUp();
        visitRepository.save(visit);
        return VisitMapper.toVisitInformation(visit);
    }

    @Transactional
    public VisitRescheduleInformation rescheduleVisit(Long visitId, String newDateStr, String newTimeStr, String performedBy) {
        Visit visit = visitRepository.findById(visitId)
                .orElseThrow(() -> new VisitDoesNotExistException("Visit with id " + visitId + " does not exist"));

        LocalDate newDate;
        LocalTime newTime;
        try {
            newDate = LocalDate.parse(newDateStr);
            newTime = LocalTime.parse(newTimeStr);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid date/time format for reschedule request", e);
        }

        Veterinarian vet = visit.getVeterinarian();
        if (vet == null) {
            throw new VeterinarianDoesNotExistException("Visit has no assigned veterinarian");
        }

        int duration = visit.getDuration() != null ? visit.getDuration() : 15;

        // Reutilizar la lógica existente en VisitsService para validar disponibilidad
        List<Visit> visits = visitRepository.findByVeterinarianAndVisitDate(vet, newDate);
        List<Visit> otherVisits = visits.stream()
                .filter(v -> !v.getId().equals(visitId))
                .toList();
        vet.validateAvailability(newDate, newTime, duration, otherVisits);

        // Update visit using domain method
        VisitHistory history = visit.reschedule(newDate, newTime, duration, performedBy);
        visitRepository.save(visit);
        visitHistoryRepository.save(history);

        return VisitRescheduleMapper.toVisitRescheduleInformation(history);
    }

    /**
     * Veterinarian Visit Management
     */

    public List<VisitInformation> getVisitsForVeterinarianAndRange(Long vetId, LocalDate start, LocalDate end)  {
        var vet = personRepository.findVeterinarianById(vetId)
                .orElseThrow(() -> new VeterinarianDoesNotExistException("Veterinarian with id " + vetId + " does not exist"));

        List<VisitInformation> result = new ArrayList<>();
        LocalDate date = start;
        while (!date.isAfter(end)) {
            var visits = visitRepository.findByVeterinarianAndVisitDate(vet, date);
            visits.stream().map(VisitMapper::toVisitInformation).forEach(result::add);
            date = date.plusDays(1);
        }
        // sort by date then time
        result.sort(Comparator.comparing(VisitInformation::visitDate).thenComparing(VisitInformation::visitTime));
        return result;
    }

    public Optional<Veterinarian> findAvailableVeterinarianNow() {
        return findAvailableVeterinarianNow(15);
    }

    public Optional<Veterinarian> findAvailableVeterinarianNow(int durationMinutes) {
        LocalDate now = LocalDate.now();
        LocalTime time = LocalTime.now();
        int dayOfWeek = now.getDayOfWeek().getValue();

        // Optimización: filtrar primero por disponibilidad base en DB
        List<Veterinarian> candidates = personRepository.findAvailableVeterinarians(dayOfWeek, time, time.plusMinutes(durationMinutes), now);
        
        return candidates.stream()
                .filter(v -> veterinariansService.isSlotAvailable(
                        v.getId(),
                        now,
                        time,
                        time.plusMinutes(durationMinutes),
                        null))
                .findFirst();
    }

    /**
     *  Medication Prescription Management 
     */

    @Transactional
    public Long addMedicationPrescription(Long visitId, Long medicationId, MedicationPrescriptionCommand command) {
        // Obtener la visita para deducir el petId y delegar en la versión existente
        Visit visit = visitRepository.findById(visitId)
                .orElseThrow(() -> new VisitDoesNotExistException("Visit with id " + visitId + " does not exist"));
        Long petId = visit.getPet() != null ? visit.getPet().getId() : null;

        if (visit.getStatus() != VisitStatus.IN_PROGRESS && visit.getStatus() != VisitStatus.COMPLETED) {
            throw new VisitStatusInvalidException("Visit must be IN_PROGRESS or COMPLETED to prescribe medication");
        }

        if (petId == null) {
            throw new PetDoesNotExistException("Visit has no pet assigned");
        }

        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new PetDoesNotExistException("Pet with id " + petId + " does not exist"));

        if (!visit.getPet().getId().equals(pet.getId())) {
            throw new PetDoesNotExistException("Pet with id " + petId + " does not belong to visit " + visitId);
        }

        Medication medication = medicationRepository.findById(medicationId)
                .orElseThrow(() -> new MedicationDoesNotExistException("Medication with id " + medicationId + " does not exist"));

        // Check medication incompatibilities with pet's medications (including recent ones)
        if (this.medicationIncompatibilityRepository != null && hasMedicationIncompatibility(pet, medication)) {
            throw new MedicationIncompatibilityExistsException("Medication with id " + medicationId + " is incompatible with medications the pet is taking or has taken recently");
        }

        int qty = command.quantity();

        medicationsService.updateMedicationStock(medicationId, qty);

        MedicationPrescription prescription = MedicationPrescriptionMapper.inputMedicationPrescriptionToDomain(
                command, visit, medication);

        visit.getMedicationPrescriptions().add(prescription);
        Visit saved = visitRepository.save(visit);
        return saved.getMedicationPrescriptions().stream()
                .map(MedicationPrescription::getId)
                .max(Long::compareTo)
                .orElse(null);
    }

    /**
     * Checks whether the provided medication is incompatible with any medication the pet is taking or has taken recently.
     * Heuristic: consider visits in the last 30 days as "recent". An incompatibility record is considered active if
     * its persistsUntil is null (indefinite) or not before today.
     *
     * @param pet the pet to check
     * @param medication the medication to check
     * @return true if the medication is incompatible with any medication the pet is taking or has taken recently
     */
    private boolean hasMedicationIncompatibility(Pet pet, Medication medication) {
        if (pet == null || pet.getVisits() == null) return false;
        LocalDate cutoff = LocalDate.now().minusDays(30);
        for (Visit v : pet.getVisits()) {
            if (v.getVisitDate() == null) continue;
            if (v.getVisitDate().isBefore(cutoff)) continue; // only recent visits
            if (v.getMedicationPrescriptions() == null) continue;
            for (MedicationPrescription mp : v.getMedicationPrescriptions()) {
                Medication other = mp.getMedication();
                if (other == null || other.getId() == null) continue;
                var opt = medicationIncompatibilityRepository.findByMedications(medication, other);
                if (opt.isPresent()) {
                    var mi = opt.get();
                    LocalDate persists = mi.getPersistsUntil();
                    if (persists == null || !persists.isBefore(LocalDate.now())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public List<MedicationPrescriptionInformation> listMedicationPrescriptions(Long visitId) {
        Visit visit = visitRepository.findById(visitId)
                .orElseThrow(() -> new VisitDoesNotExistException("Visit with id " + visitId + " does not exist"));
        return visit.getMedicationPrescriptions().stream()
                .map(MedicationPrescriptionMapper::toMedicationPrescriptionInformation)
                .toList();
    }

    public MedicationPrescriptionInformation getMedicationPrescriptionById(Long visitId, Long prescriptionId) {
        Visit visit = visitRepository.findById(visitId)
                .orElseThrow(() -> new VisitDoesNotExistException("Visit with id " + visitId + " does not exist"));
        MedicationPrescription mp = visit.getMedicationPrescriptions().stream()
                .filter(p -> p.getId().equals(prescriptionId))
                .findFirst()
                .orElseThrow(() -> new MedicationPrescriptionDoesNotExistException(
                        "Medication Prescription with id " + prescriptionId + " does not exist for this visit"));
        return MedicationPrescriptionMapper.toMedicationPrescriptionInformation(mp);
    }

    /**
     * UC2.6: List medication ordered by prescription in a given period.
     * Implementation note: do not create new classes. We delegate aggregation to the DB via VisitRepository.
     * Returns a list of maps with keys: medicationId (Long), medicationName (String), prescriptionCount (Long)
     */
    public List<Map<String, Object>> listMedicationPrescriptionsByPeriod(LocalDate from, LocalDate to) {
        if (from == null || to == null) throw new IllegalArgumentException("from and to dates must be provided");
        if (from.isAfter(to)) throw new IllegalArgumentException("from date must be before or equal to to date");

        List<Object[]> rows = visitRepository.findMedicationPrescriptionCounts(from, to);

        return rows.stream().map(r -> {
            // r[0] = m.id, r[1] = m.name, r[2] = COUNT(mp)
            Object idObj = r[0];
            Object nameObj = r[1];
            Object countObj = r[2];

            Long medicationId = null;
            if (idObj instanceof Number) medicationId = ((Number) idObj).longValue();
            else if (idObj != null) medicationId = Long.parseLong(idObj.toString());

            String medicationName = nameObj != null ? nameObj.toString() : null;

            long prescriptionCount = 0;
            if (countObj instanceof Number) prescriptionCount = ((Number) countObj).longValue();
            else if (countObj != null) prescriptionCount = Long.parseLong(countObj.toString());

            Map<String, Object> map = new HashMap<>();
            map.put("medicationId", medicationId);
            map.put("medicationName", medicationName);
            map.put("prescriptionCount", prescriptionCount);
            return map;
        }).collect(Collectors.toList());
    }

    @Transactional
    public void updateMedicationPrescription(Long visitId, Long prescriptionId, MedicationPrescriptionCommand command) {
        Visit visit = visitRepository.findById(visitId)
                .orElseThrow(() -> new VisitDoesNotExistException("Visit with id " + visitId + " does not exist"));
        MedicationPrescription mp = visit.getMedicationPrescriptions().stream()
                .filter(p -> p.getId().equals(prescriptionId))
                .findFirst()
                .orElseThrow(() -> new MedicationPrescriptionDoesNotExistException(
                        "Medication Prescription with id " + prescriptionId + " does not exist for this visit"));

        int oldQty = mp.getQuantityPrescribed() != null ? mp.getQuantityPrescribed() : 0;
        int newQty = command.quantity();
        int delta = newQty - oldQty;
        if (delta > 0) {
            // Deduct additional stock needed
            medicationsService.updateMedicationStock(mp.getMedication().getId(), delta);
        }
        // If delta < 0 we are not returning stock to batches by design

        mp.setQuantityPrescribed(newQty);
        mp.setDosageInstructions(command.dosage_instructions());
        mp.setDurationInDays(command.duration_in_days());
        visitRepository.save(visit);
    }

    @Transactional
    public void deleteMedicationPrescription(Long visitId, Long prescriptionId) {
        Visit visit = visitRepository.findById(visitId)
                .orElseThrow(() -> new VisitDoesNotExistException("Visit with id " + visitId + " does not exist"));
        boolean removed = visit.getMedicationPrescriptions().removeIf(p -> p.getId().equals(prescriptionId));
        if (!removed) {
            throw new MedicationPrescriptionDoesNotExistException(
                    "Medication Prescription with id " + prescriptionId + " does not exist for this visit");
        }
        visitRepository.save(visit);
    }

    /**
     * Treatment Management
     */

    @Transactional
    public Long createTreatment(TreatmentCommand command) {
        Treatment treatment = TreatmentMapper.inputTreatmentToDomain(command);
        treatmentRepository.save(treatment);
        return treatment.getId();
    }

    public List<TreatmentInformation> getTreatments() {
        return treatmentRepository.findAll().stream()
                .map(TreatmentMapper::toTreatmentInformation)
                .toList();
    }

    public Optional<TreatmentInformation> getTreatmentById(Long treatmentId) {
        return treatmentRepository.findTreatmentInformationById(treatmentId);
    }

    @Transactional
    public TreatmentInformation updateTreatment(Long treatmentId, TreatmentCommand command) {
        Treatment treatment = treatmentRepository.findById(treatmentId)
                .orElseThrow(() -> new TreatmentDoesNotExistException("Treatment with id " + treatmentId + " does not exist"));
        treatment.update(command.name(), command.description(), command.cost());
        treatmentRepository.save(treatment);
        return TreatmentMapper.toTreatmentInformation(treatment);
    }

    public void deleteTreatment(Long treatmentId) {
        treatmentRepository.deleteById(treatmentId);
    }

    @Transactional
    public VisitInformation assignTreatmentToVisit(Long visitId, Long treatmentId) {
        Visit visit = visitRepository.findById(visitId)
                .orElseThrow(() -> new VisitDoesNotExistException("Visit with id " + visitId + " does not exist"));
        Treatment treatment = treatmentRepository.findById(treatmentId)
                .orElseThrow(() -> new TreatmentDoesNotExistException("Treatment with id " + treatmentId + " does not exist"));
        visit.assignTreatment(treatment);
        visitRepository.save(visit);
        return VisitMapper.toVisitInformation(visit);
    }

    public TreatmentInformation getTreatmentFromVisit(Long visitId) {
        Visit visit = visitRepository.findById(visitId)
                .orElseThrow(() -> new VisitDoesNotExistException("Visit with id " + visitId + " does not exist"));
        Treatment treatment = visit.getTreatment();
        return TreatmentMapper.toTreatmentInformation(treatment);
    }

    @Transactional
    public VisitInformation unassignTreatmentFromVisit(Long visitId) {
        Visit visit = visitRepository.findById(visitId)
                .orElseThrow(() -> new VisitDoesNotExistException("Visit with id " + visitId + " does not exist"));
        visit.unassignTreatment();
        visitRepository.save(visit);
        return VisitMapper.toVisitInformation(visit);
    }

    public List<VisitHistoryEntryInformation> getPetMedicalHistory(Long petId) {
        petRepository.findById(petId)
                .orElseThrow(() -> new PetDoesNotExistException("Pet with id " + petId + " does not exist"));

        List<Visit> visits = visitRepository.findMedicalHistoryByPet(petId);

        return visits.stream().map(v -> {
            List<MedicationPrescriptionInformation> prescInfos = (v.getMedicationPrescriptions() == null)
                    ? List.of()
                    : v.getMedicationPrescriptions().stream()
                        .map(MedicationPrescriptionMapper::toMedicationPrescriptionInformation)
                        .toList();
            return new VisitHistoryEntryInformation(
                    v.getVisitDate() != null ? v.getVisitDate().toString() : null,
                    VisitMapper.toVisitInformation(v),
                    TreatmentMapper.toTreatmentInformation(v.getTreatment()),
                    prescInfos
            );
        }).toList();
    }

    /**
     * UC2.7: List veterinarians ordered by prescription count of a given medication in a period.
     * Returns a list of maps with keys: veterinarianId (Long), veterinarianName (String), veterinarianLastName (String), prescriptionCount (Long)
     */
    public List<Map<String, Object>> listVeterinariansByMedicationPrescriptions(Long medicationId, LocalDate from, LocalDate to) {
        if (medicationId == null) throw new IllegalArgumentException("medicationId must be provided");
        if (from == null || to == null) throw new IllegalArgumentException("from and to dates must be provided");
        if (from.isAfter(to)) throw new IllegalArgumentException("from date must be before or equal to to date");

        List<Object[]> rows = visitRepository.findVeterinarianPrescriptionCounts(medicationId, from, to);

        return rows.stream().map(r -> {
            // r[0] = vet.id, r[1] = vet.name, r[2] = vet.lastName, r[3] = COUNT(mp)
            Object idObj = r[0];
            Object nameObj = r[1];
            Object lastNameObj = r[2];
            Object countObj = r[3];

            Long vetId = null;
            if (idObj instanceof Number) vetId = ((Number) idObj).longValue();
            else if (idObj != null) vetId = Long.parseLong(idObj.toString());

            String vetName = nameObj != null ? nameObj.toString() : null;
            String vetLastName = lastNameObj != null ? lastNameObj.toString() : null;

            long prescriptionCount = 0;
            if (countObj instanceof Number) prescriptionCount = ((Number) countObj).longValue();
            else if (countObj != null) prescriptionCount = Long.parseLong(countObj.toString());

            Map<String, Object> map = new HashMap<>();
            map.put("veterinarianId", vetId);
            map.put("veterinarianName", vetName);
            map.put("veterinarianLastName", vetLastName);
            map.put("prescriptionCount", prescriptionCount);
            return map;
        }).collect(Collectors.toList());
    }

}
