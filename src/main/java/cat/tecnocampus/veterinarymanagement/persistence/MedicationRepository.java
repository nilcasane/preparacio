package cat.tecnocampus.veterinarymanagement.persistence;

import cat.tecnocampus.veterinarymanagement.application.outputDTO.MedicationInformation;
import cat.tecnocampus.veterinarymanagement.domain.Medication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MedicationRepository extends JpaRepository<Medication, Long> {

    @Query("""
        SELECT m.id AS id, m.name AS name, m.activeIngredient AS active_ingredient,
        m.dosageUnit AS dosage_unit, m.unitPrice AS unit_price, m.reorderThreshold AS reorder_threshold
        FROM Medication m
        WHERE m.id = :id
        """)
    Optional<MedicationInformation> findMedicationInformationById(Long id);
}
