package cat.tecnocampus.veterinarymanagement.persistence;

import cat.tecnocampus.veterinarymanagement.domain.Availability;
import cat.tecnocampus.veterinarymanagement.domain.Veterinarian;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AvailabilityRepository extends JpaRepository<Availability, Long> {

    @Query("""
        SELECT a FROM Availability a WHERE a.id = :id
        """)
    Optional<Availability> findAvailabilityById(Long id);

    List<Availability> findByVeterinarian(Veterinarian vet);
}
