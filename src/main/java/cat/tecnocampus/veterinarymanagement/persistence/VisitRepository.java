package cat.tecnocampus.veterinarymanagement.persistence;

import cat.tecnocampus.veterinarymanagement.domain.Visit;
import cat.tecnocampus.veterinarymanagement.domain.Veterinarian;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface VisitRepository extends CrudRepository<Visit, Long> {
    List<Visit> findByVeterinarianAndVisitDate(Veterinarian veterinarian, LocalDate visitDate);
    @Query("select distinct v from Visit v " +
           "left join fetch v.treatment t " +
           "left join fetch v.medicationPrescriptions mp " +
           "where v.pet.id = :petId " +
           "order by v.visitDate asc, v.visitTime asc")
    List<Visit> findMedicalHistoryByPet(@Param("petId") Long petId);

    @Query("""
        SELECT m.id, m.name, COUNT(mp)
        FROM Visit v
        JOIN v.medicationPrescriptions mp
        JOIN mp.medication m
        WHERE v.visitDate BETWEEN :from AND :to
        GROUP BY m.id, m.name
        ORDER BY COUNT(mp) DESC
        """)
    List<Object[]> findMedicationPrescriptionCounts(LocalDate from, LocalDate to);

    @Query("""
        SELECT vet.id, vet.name, vet.lastName, COUNT(mp)
        FROM Visit v
        JOIN v.veterinarian vet
        JOIN v.medicationPrescriptions mp
        JOIN mp.medication m
        WHERE m.id = :medicationId
          AND v.visitDate BETWEEN :from AND :to
        GROUP BY vet.id, vet.name, vet.lastName
        ORDER BY COUNT(mp) DESC
        """)
    List<Object[]> findVeterinarianPrescriptionCounts(Long medicationId, LocalDate from, LocalDate to);
}
