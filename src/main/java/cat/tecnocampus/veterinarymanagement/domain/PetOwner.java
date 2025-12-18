package cat.tecnocampus.veterinarymanagement.domain;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "pet_owner")
@PrimaryKeyJoinColumn(name = "person_id")
public class PetOwner extends Person {

    @ManyToMany
    @JoinTable(
            name = "pet_owner_pet",
            joinColumns = @JoinColumn(name = "pet_owner_id"),
            inverseJoinColumns = @JoinColumn(name = "pet_id")
    )
    private List<Pet> pets;

    @OneToMany
    @JoinColumn(name = "pet_owner_id")
    private List<Visit> visits;

    @OneToMany(mappedBy = "petOwner", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Invoice> invoices;

    // Getters y setters
    public List<Pet> getPets() { return pets; }
    public void setPets(List<Pet> pets) { this.pets = pets; }

    public List<Visit> getVisits() { return visits; }
    public void setVisits(List<Visit> visits) { this.visits = visits; }

    public List<Invoice> getInvoices() { return invoices; }
    public void setInvoices(List<Invoice> invoices) { this.invoices = invoices; }

    /**
     * Checks if this pet owner owns the specified pet.
     *
     * @param pet The pet to check
     * @return true if the pet belongs to this owner, false otherwise
     */
    public boolean owns(Pet pet) {
        if (pets == null || pet == null || pet.getId() == null) {
            return false;
        }
        return pets.stream().anyMatch(p -> p.getId().equals(pet.getId()));
    }
}
