package cat.tecnocampus.veterinarymanagement.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "treatment")
public class Treatment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private Double cost;

    protected Treatment() {
    }

    public Treatment(String name, Double cost) {
        setName(name);
        setCost(cost);
    }

    public Treatment(String name, String description, Double cost) {
        setName(name);
        setDescription(description);
        setCost(cost);
    }

    public Long getId() { return id; }
    
    public void update(String name, String description, Double cost) {
        setName(name);
        setDescription(description);
        setCost(cost);
    }

    public String getName() { return name; }
    private void setName(String name) { 
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty"); 
        }
        this.name = name; 
    }
    public String getDescription() { return description; }
    private void setDescription(String description) { 
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Description cannot be null or empty");
        }
        this.description = description; 
    }
    public Double getCost() { return cost; }
    private void setCost(Double cost) { 
        if (cost < 0) {
            throw new IllegalArgumentException("Cost cannot be negative");
        }
        this.cost = cost; 
    }
}
