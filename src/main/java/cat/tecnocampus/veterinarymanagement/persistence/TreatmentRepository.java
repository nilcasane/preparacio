package cat.tecnocampus.veterinarymanagement.persistence;

import cat.tecnocampus.veterinarymanagement.application.outputDTO.TreatmentInformation;
import cat.tecnocampus.veterinarymanagement.domain.Treatment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface TreatmentRepository extends JpaRepository<Treatment, Long> {

    @Query("""
        SELECT t.id AS id, t.name AS name, t.description AS description, t.cost AS cost
        FROM Treatment t
        WHERE t.id = :id
        """)
    Optional<TreatmentInformation> findTreatmentInformationById(Long id);
}
