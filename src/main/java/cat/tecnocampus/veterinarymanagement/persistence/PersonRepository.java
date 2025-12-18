package cat.tecnocampus.veterinarymanagement.persistence;

import cat.tecnocampus.veterinarymanagement.application.outputDTO.VeterinarianDemandInformation;
import cat.tecnocampus.veterinarymanagement.application.outputDTO.VeterinarianInformation;
import cat.tecnocampus.veterinarymanagement.domain.Administrator;
import cat.tecnocampus.veterinarymanagement.domain.Person;
import cat.tecnocampus.veterinarymanagement.domain.PetOwner;
import cat.tecnocampus.veterinarymanagement.domain.Veterinarian;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface PersonRepository extends JpaRepository<Person, Long> {
    // General queries
    Optional<Person> findByUsernameOrEmail(String username, String email);

    // Specific queries by type of person
    @Query("SELECT p FROM Administrator p WHERE p.id = :id")
    Optional<Administrator> findAdministratorById(Long id);

    @Query("SELECT p FROM Veterinarian p WHERE p.id = :id")
    Optional<Veterinarian> findVeterinarianById(Long id);

    @Query("""
        SELECT v.id AS id, v.name AS name, v.lastName AS last_name, v.phoneNumber AS phone_number, v.email AS email, v.address AS address, v.licenseNumber AS license_number, v.yearsOfExperience AS years_of_experience, v.availability AS availability
        FROM Veterinarian v
        WHERE v.id = :id
        """)
    Optional<VeterinarianInformation> findVeterinarianInformationById(Long id);

    @Query("SELECT p FROM PetOwner p WHERE p.id = :id")
    Optional<PetOwner> findPetOwnerById(Long id);

    // List by type of person
    @Query("SELECT p FROM Administrator p")
    List<Administrator> findAllAdministrators();

    @Query("SELECT p FROM Veterinarian p")
    List<Veterinarian> findAllVeterinarians();

    @Query("SELECT p FROM PetOwner p")
    List<PetOwner> findAllPetOwners();

    @Query("""
        SELECT DISTINCT v FROM Veterinarian v
        JOIN v.availability a
        WHERE a.dayOfWeek = :dayOfWeek
        AND :startTime >= a.startTime 
        AND :endTime <= a.endTime
        AND :date >= a.periodStart AND :date <= a.periodEnd
    """)
    List<Veterinarian> findAvailableVeterinarians(int dayOfWeek, LocalTime startTime, LocalTime endTime, LocalDate date);

    @Query("""
        SELECT new cat.tecnocampus.veterinarymanagement.application.outputDTO.VeterinarianDemandInformation(
            v.id,
            v.name,
            v.lastName,
            COUNT(vis)
        )
        FROM Veterinarian v
        LEFT JOIN Visit vis ON vis.veterinarian = v AND vis.visitDate BETWEEN :start AND :end
        GROUP BY v.id, v.name, v.lastName
        ORDER BY COUNT(vis) DESC
    """)
    List<VeterinarianDemandInformation> findVeterinariansDemand(LocalDate start, LocalDate end);
}
