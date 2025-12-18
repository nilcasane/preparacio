package cat.tecnocampus.veterinarymanagement.persistence;

import cat.tecnocampus.veterinarymanagement.application.outputDTO.LoyaltyTierInformation;
import cat.tecnocampus.veterinarymanagement.domain.Discount;
import cat.tecnocampus.veterinarymanagement.domain.LoyaltyTier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LoyaltyTierRepository extends JpaRepository<LoyaltyTier, Long> {
    @Query("""
        SELECT lt.id AS id, lt.name AS name, lt.description AS description, lt.minPoints AS min_points,
        lt.benefits AS benefits, lt.discount.id AS discount_id
        FROM LoyaltyTier lt
        WHERE lt.id = :id
        """)
    Optional<LoyaltyTierInformation> findLoyaltyTierInformationById(Long id);

    List<LoyaltyTier> findByDiscount(Discount discount);

}
