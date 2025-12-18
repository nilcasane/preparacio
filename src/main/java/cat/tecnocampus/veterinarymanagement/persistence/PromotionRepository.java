package cat.tecnocampus.veterinarymanagement.persistence;

import cat.tecnocampus.veterinarymanagement.application.outputDTO.PromotionInformation;
import cat.tecnocampus.veterinarymanagement.domain.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PromotionRepository extends JpaRepository<Promotion, Long> {

    @Query("""
        SELECT p.id AS id, p.name AS name, p.description AS description, p.discountCode AS discount_code, p.startDate AS start_date, p.endDate AS end_date
        FROM Promotion p
        WHERE p.id = :id
        """)
    Optional<PromotionInformation> findPromotionInformationById(Long id);


}


