package cat.tecnocampus.veterinarymanagement.persistence;

import cat.tecnocampus.veterinarymanagement.domain.Availability;
import cat.tecnocampus.veterinarymanagement.domain.AvailabilityException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AvailabilityExceptionRepository extends JpaRepository<AvailabilityException, Long> {

    @Query("""
        SELECT e FROM AvailabilityException e WHERE e.id = :id
        """)
    Optional<AvailabilityException> findExceptionInformationById(Long id);

    List<AvailabilityException> findByAvailability(Availability availability);
}
