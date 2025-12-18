package cat.tecnocampus.veterinarymanagement.persistence;

import cat.tecnocampus.veterinarymanagement.domain.MedicationIncompatibility;
import cat.tecnocampus.veterinarymanagement.domain.Medication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MedicationIncompatibilityRepository extends JpaRepository<MedicationIncompatibility, Long> {

    @Query("SELECT m FROM MedicationIncompatibility m WHERE (m.medicationA = :a AND m.medicationB = :b) OR (m.medicationA = :b AND m.medicationB = :a)")
    Optional<MedicationIncompatibility> findByMedications(Medication a, Medication b);

    @Query("SELECT m FROM MedicationIncompatibility m WHERE m.medicationA = :med OR m.medicationB = :med")
    List<MedicationIncompatibility> findByMedication(Medication med);
}

