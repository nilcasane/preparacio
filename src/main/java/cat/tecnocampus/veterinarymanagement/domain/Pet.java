package cat.tecnocampus.veterinarymanagement.domain;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "pet")
public class Pet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private LocalDate dateOfBirth;
    private String gender;
    private String breed;
    private Double weight;
    private Long microchipNumber;

    @ManyToMany(mappedBy = "pets")
    private List<PetOwner> petOwners;

    @OneToMany(mappedBy = "pet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Visit> visits;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getBreed() { return breed; }
    public void setBreed(String breed) { this.breed = breed; }
    public Double getWeight() { return weight; }
    public void setWeight(Double weight) { this.weight = weight; }
    public Long getMicrochipNumber() { return microchipNumber; }
    public void setMicrochipNumber(Long microchipNumber) { this.microchipNumber = microchipNumber; }

    public List<PetOwner> getPetOwners() { return petOwners; }
    public void setPetOwners(List<PetOwner> petOwners) { this.petOwners = petOwners; }

    public List<Visit> getVisits() { return visits; }
    public void setVisits(List<Visit> visits) { this.visits = visits; }

    // Helper method to get the primary pet owner (for backward compatibility)
    public PetOwner getPetOwner() {
        return petOwners != null && !petOwners.isEmpty() ? petOwners.get(0) : null;
    }
}
