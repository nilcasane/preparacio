package cat.tecnocampus.veterinarymanagement.persistence;

import cat.tecnocampus.veterinarymanagement.domain.Medication;
import cat.tecnocampus.veterinarymanagement.domain.MedicationBatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MedicationBatchRepository extends JpaRepository<MedicationBatch, Long> {

    @Query("""
        SELECT b
        FROM MedicationBatch b
        WHERE b.id = :id
        """)
    Optional<MedicationBatch> findMedicationBatchById(Long id);

    List<MedicationBatch> findByMedication(Medication med);

    @Query("""
        SELECT b
        FROM MedicationBatch b
        WHERE b.medication = :medication
          AND b.expiryDate > CURRENT_DATE
          AND b.currentQuantity > 0
        ORDER BY b.expiryDate ASC
        """)
    List<MedicationBatch> findAvailableBatchesByMedication(Medication medication);
}