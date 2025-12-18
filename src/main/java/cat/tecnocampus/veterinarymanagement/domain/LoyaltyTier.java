package cat.tecnocampus.veterinarymanagement.domain;

import cat.tecnocampus.veterinarymanagement.application.inputDTO.LoyaltyTierCommand;
import jakarta.persistence.*;

@Entity
@Table(name = "loyalty_tier")
public class LoyaltyTier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private int minPoints;
    private String benefits;

    @ManyToOne
    @JoinColumn(name = "discount_id")
    private Discount discount;

    public Discount getDiscount() {
        return discount;
    }

    public void setDiscount(Discount discount) {
        this.discount = discount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getMinPoints() {
        return minPoints;
    }

    public void setMinPoints(int minPoints) {
        this.minPoints = minPoints;
    }

    public String getBenefits() {
        return benefits;
    }

    public void setBenefits(String benefits) {
        this.benefits = benefits;
    }


    public void updateLoyaltyTier(LoyaltyTierCommand command) {
        setName(command.name());
        setDescription(command.description());
        setMinPoints(command.min_points());
        setBenefits(command.benefits());
    }
}
