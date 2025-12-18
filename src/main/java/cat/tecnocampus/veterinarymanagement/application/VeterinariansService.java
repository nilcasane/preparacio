package cat.tecnocampus.veterinarymanagement.application;

import cat.tecnocampus.veterinarymanagement.application.exceptions.AvailabilityDoesNotExistException;
import cat.tecnocampus.veterinarymanagement.application.exceptions.ExceptionDoesNotExistException;
import cat.tecnocampus.veterinarymanagement.application.exceptions.VeterinarianDoesNotExistException;
import cat.tecnocampus.veterinarymanagement.application.inputDTO.AvailabilityCommand;
import cat.tecnocampus.veterinarymanagement.application.inputDTO.ExceptionCommand;
import cat.tecnocampus.veterinarymanagement.application.mappers.ExceptionMapper;
import cat.tecnocampus.veterinarymanagement.application.outputDTO.AvailabilityExceptionInformation;
import cat.tecnocampus.veterinarymanagement.application.outputDTO.AvailabilityInformation;
import cat.tecnocampus.veterinarymanagement.application.outputDTO.VeterinarianDemandInformation;
import cat.tecnocampus.veterinarymanagement.application.mappers.AvailabilityMapper;
import cat.tecnocampus.veterinarymanagement.domain.Availability;
import cat.tecnocampus.veterinarymanagement.domain.AvailabilityException;
import cat.tecnocampus.veterinarymanagement.domain.Visit;
import cat.tecnocampus.veterinarymanagement.domain.Veterinarian;
import cat.tecnocampus.veterinarymanagement.persistence.AvailabilityRepository;
import cat.tecnocampus.veterinarymanagement.persistence.PersonRepository;
import cat.tecnocampus.veterinarymanagement.persistence.VisitRepository;
import cat.tecnocampus.veterinarymanagement.persistence.AvailabilityExceptionRepository;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

@Service
public class VeterinariansService {
    private final PersonRepository personRepository;
    private final AvailabilityRepository availabilityRepository;
    private final AvailabilityExceptionRepository availabilityExceptionRepository;
    private final VisitRepository visitRepository;

    public VeterinariansService(PersonRepository personRepository,
                                AvailabilityRepository availabilityRepository,
                                AvailabilityExceptionRepository availabilityExceptionRepository,
                                VisitRepository visitRepository) {
        this.personRepository = personRepository;
        this.availabilityRepository = availabilityRepository;
        this.availabilityExceptionRepository = availabilityExceptionRepository;
        this.visitRepository = visitRepository;
    }

    public Optional<AvailabilityInformation> getAvailabilityById(Long id) {
        return availabilityRepository
                .findAvailabilityById(id)
                .map(AvailabilityMapper::toAvailabilityInformation);
    }

    public Long createAvailability(Long vetId, AvailabilityCommand command) {
        try {
            Veterinarian vet = personRepository
                    .findVeterinarianById(vetId)
                    .orElseThrow(() -> new VeterinarianDoesNotExistException("Veterinarian with id " + vetId + " does not exist"));

            Availability availability = AvailabilityMapper.inputAvailabilityToDomain(command, vet);
            var saved = availabilityRepository.save(availability);
            return saved.getId();
        } catch (org.springframework.dao.InvalidDataAccessApiUsageException e) {
            throw new IllegalArgumentException("Error creating the availability");
        }
    }

    public List<AvailabilityInformation> getAvailabilities(Long vetId) {
        Veterinarian vet = personRepository
                .findVeterinarianById(vetId)
                .orElseThrow(() -> new VeterinarianDoesNotExistException("Veterinarian with id " + vetId + " does not exist"));

        List<Availability> availabilities = availabilityRepository.findByVeterinarian(vet);
        return availabilities.stream()
                .map(AvailabilityMapper::toAvailabilityInformation)
                .toList();
    }

    public void deleteAvailabilities(Long vetId) {
        Veterinarian vet = personRepository
                .findVeterinarianById(vetId)
                .orElseThrow(() -> new VeterinarianDoesNotExistException("Veterinarian with id " + vetId + " does not exist"));
        List<Availability> availabilities = availabilityRepository.findByVeterinarian(vet);
        availabilityRepository.deleteAll(availabilities);
    }

    public AvailabilityInformation updateAvailability(Long availabilityId, AvailabilityCommand command) {
        try {
            Availability availability = availabilityRepository.findById(availabilityId)
                    .orElseThrow(() -> new AvailabilityDoesNotExistException("Availability with id " + availabilityId + " does not exist"));
            availability.updateAvailability(command);
            availabilityRepository.save(availability);
            return AvailabilityMapper.toAvailabilityInformation(availability);
        } catch (InvalidDataAccessApiUsageException e) {
            throw new IllegalArgumentException("Error updating availability with id " + availabilityId + " does not exist", e);
        }
    }

    public void deleteAvailability(Long availabilityId) {
        try {
            Availability availability = availabilityRepository.findById(availabilityId)
                    .orElseThrow(() -> new AvailabilityDoesNotExistException("Availability with id " + availabilityId + " does not exist"));

            availabilityRepository.delete(availability);
        } catch (InvalidDataAccessApiUsageException e) {
            throw new IllegalArgumentException("Error deleting availability with id " + availabilityId, e);
        }
    }

    /**
     * Comprueba si un slot está disponible para un veterinario en una fecha/hora concreta.
     * Este método centraliza la lógica anteriormente en DefaultSlotService.
     */
    public boolean isSlotAvailable(Long veterinarianId, LocalDate date, LocalTime start, LocalTime end, Long excludingVisitId) {
        java.util.Objects.requireNonNull(date, "date");
        java.util.Objects.requireNonNull(start, "start");
        java.util.Objects.requireNonNull(end, "end");

        Veterinarian vet = personRepository.findVeterinarianById(veterinarianId)
                .orElseThrow(() -> new VeterinarianDoesNotExistException("Veterinarian with id " + veterinarianId + " does not exist"));

        if (!vet.isWorking(date, start, end)) return false;

        List<Visit> sameDayVisits = visitRepository.findByVeterinarianAndVisitDate(vet, date);
        boolean overlaps = sameDayVisits.stream()
                .filter(v -> !java.util.Objects.equals(v.getId(), excludingVisitId))
                .anyMatch(v -> timeOverlaps(v.getVisitTime(), v.getVisitTime().plusMinutes(v.getDuration()), start, end));
        return !overlaps;
    }

    private boolean timeOverlaps(LocalTime s1, LocalTime e1, LocalTime s2, LocalTime e2) {
        return s1.isBefore(e2) && s2.isBefore(e1);
    }

    //AvailabilityException methods
    public Long createException(Long availabilityId, ExceptionCommand command) {
        try {
            Availability availability = availabilityRepository
                    .findById(availabilityId)
                    .orElseThrow(() -> new AvailabilityDoesNotExistException("Availability with id " + availabilityId + " does not exist"));

            AvailabilityException exception = ExceptionMapper.inputExceptionToDomain(command, availability);
            var saved = availabilityExceptionRepository.save(exception);
            return saved.getId();
        } catch (InvalidDataAccessApiUsageException e) {
            throw new IllegalArgumentException("Error creating the exception");
        }
    }

    public Optional<AvailabilityExceptionInformation> getExceptionById(Long exceptionId) {
        return availabilityExceptionRepository
                .findExceptionInformationById(exceptionId)
                .map(ExceptionMapper::toExceptionInformation);
    }

    public List<AvailabilityExceptionInformation> getExceptions(Long availabilityId) {
        Availability availability = availabilityRepository
                .findById(availabilityId)
                .orElseThrow(() -> new AvailabilityDoesNotExistException("Availability with id " + availabilityId + " does not exist"));

        List<AvailabilityException> exceptions = availabilityExceptionRepository.findByAvailability(availability);
        return exceptions.stream()
                .map(ExceptionMapper::toExceptionInformation)
                .toList();
    }


    public AvailabilityExceptionInformation updateException(Long exceptionId, ExceptionCommand command) {
        try {
            AvailabilityException exception = availabilityExceptionRepository.findById(exceptionId)
                    .orElseThrow(() -> new ExceptionDoesNotExistException("Exception with id " + exceptionId + " does not exist"));
            exception.updateException(command);
            availabilityExceptionRepository.save(exception);
            return ExceptionMapper.toExceptionInformation(exception);
        } catch (InvalidDataAccessApiUsageException e) {
            throw new IllegalArgumentException("Error updating exception with id " + exceptionId, e);
        }
    }

    public void deleteException(Long exceptionId) {
        try {
            AvailabilityException exception = availabilityExceptionRepository.findById(exceptionId)
                    .orElseThrow(() -> new ExceptionDoesNotExistException("Exception with id " + exceptionId + " does not exist"));
            availabilityExceptionRepository.delete(exception);
        } catch (InvalidDataAccessApiUsageException e) {
            throw new IllegalArgumentException("Error deleting exception with id " + exceptionId, e);
        }
    }

    public void deleteExceptions(Long availabilityId) {
        Availability availability = availabilityRepository
                .findById(availabilityId)
                .orElseThrow(() -> new AvailabilityDoesNotExistException("Availability with id " + availabilityId + " does not exist"));
        List<AvailabilityException> exceptions = availabilityExceptionRepository.findByAvailability(availability);
        availabilityExceptionRepository.deleteAll(exceptions);
    }

    public List<VeterinarianDemandInformation> getVeterinariansByDemand(LocalDate startDate, LocalDate endDate) {
        return personRepository.findVeterinariansDemand(startDate, endDate);
    }

    public List<VeterinarianDemandInformation> getVeterinariansDemand(String start, String end) {
        try {
            LocalDate startDate = LocalDate.parse(start);
            LocalDate endDate = LocalDate.parse(end);
            if (endDate.isBefore(startDate)) {
                throw new IllegalArgumentException("Invalid date range: end before start");
            }
            return getVeterinariansByDemand(startDate, endDate);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format", e);
        }
    }
}
