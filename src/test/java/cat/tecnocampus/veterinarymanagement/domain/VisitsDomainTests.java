package cat.tecnocampus.veterinarymanagement.domain;

import cat.tecnocampus.veterinarymanagement.domain.exceptions.VisitInvalidStateException;
import cat.tecnocampus.veterinarymanagement.domain.exceptions.VisitStatusInvalidException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class VisitsDomainTests {

    private Pet createPet(Long id) {
        Pet pet = new Pet();
        pet.setId(id);
        return pet;
    }

    private PetOwner createOwnerWithPets(List<Pet> pets) {
        PetOwner owner = new PetOwner();
        owner.setPets(pets);
        return owner;
    }

    private Veterinarian createVet(Long id) {
        Veterinarian vet = new Veterinarian();
        vet.setId(id);
        return vet;
    }

    @Test
    void creatingVisitWhenOwnerDoesNotOwnPetThrows() {
        Pet pet = createPet(10L);
        PetOwner ownerWithoutPet = createOwnerWithPets(List.of());
        Veterinarian vet = createVet(1L);

        assertThrows(IllegalArgumentException.class, () ->
                new Visit(pet, ownerWithoutPet, vet, LocalDate.now(), LocalTime.now(), "Reason", 25.0, 15)
        );
    }

    @Test
    void startAndCompleteVisitHappyPathAndCannotStartAfterComplete() {
        Pet pet = createPet(1L);
        PetOwner owner = createOwnerWithPets(List.of(pet));
        Veterinarian vet = createVet(1L);
        Visit visit = new Visit(pet, owner, vet, LocalDate.now(), LocalTime.now(), "Checkup", 20.0, 15);

        assertEquals(VisitStatus.SCHEDULED, visit.getStatus());

        visit.start();
        assertEquals(VisitStatus.IN_PROGRESS, visit.getStatus());

        visit.complete();
        assertEquals(VisitStatus.COMPLETED, visit.getStatus());

        assertThrows(VisitInvalidStateException.class, visit::start);
    }

    @Test
    void assignTreatmentRequiresInProgressOrCompleted() {
        Pet pet = createPet(2L);
        PetOwner owner = createOwnerWithPets(List.of(pet));
        Veterinarian vet = createVet(2L);
        Visit visit = new Visit(pet, owner, vet, LocalDate.now(), LocalTime.now(), "Vaccination", 18.0, 15);

        Treatment treatment = new Treatment();
        assertThrows(VisitStatusInvalidException.class, () -> visit.assignTreatment(treatment));

        visit.start();
        visit.assignTreatment(treatment);
        assertNotNull(visit.getTreatment());

        visit.unassignTreatment();
        assertNull(visit.getTreatment());
    }
}
