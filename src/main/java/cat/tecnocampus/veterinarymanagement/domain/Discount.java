package cat.tecnocampus.veterinarymanagement.domain;

import cat.tecnocampus.veterinarymanagement.application.inputDTO.DiscountCommand;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "discount")
public class Discount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String code;
    @Enumerated(EnumType.STRING)
    private DiscountType discountType;
    private Double valueAmount;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer maxUses;
    private Integer usesCount;

    @ManyToOne
    @JoinColumn(name = "promotion_id")
    private Promotion promotion;

    @OneToMany(mappedBy = "discount")
    private Set<LoyaltyTier> loyaltyTiers = new HashSet<>();

    // Getters y setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public DiscountType getDiscountType() {
        return discountType;
    }
    public void setDiscountType(DiscountType type) {
        this.discountType = type;
    }
    public Double getValueAmount() {
        return valueAmount;
    }
    public void setValueAmount(Double value) {
        this.valueAmount = value;
    }
    public LocalDate getStartDate() {
        return startDate;
    }
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    public LocalDate getEndDate() {
        return endDate;
    }
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
    public Integer getMaxUses() {
        return maxUses;
    }
    public void setMaxUses(Integer maxUses) {
        this.maxUses = maxUses;
    }
    public Integer getUsesCount() {
        return usesCount;
    }
    public void setUsesCount(Integer usesCount) {
        this.usesCount = usesCount;
    }
    public Promotion getPromotion() {
        return promotion;
    }
    public void setPromotion(Promotion promotion) {
        this.promotion = promotion;
    }

    public Set<LoyaltyTier> getLoyaltyTiers() {
        return loyaltyTiers;
    }

    public void setLoyaltyTiers(Set<LoyaltyTier> loyaltyTiers) {
        this.loyaltyTiers = loyaltyTiers;
    }

    public void updateDiscount(DiscountCommand command) {
        setCode(command.code());
        setDiscountType(DiscountType.valueOf(command.discount_type()));
        setValueAmount(command.value_amount());
        setStartDate(LocalDate.parse(command.start_date()));
        setEndDate(LocalDate.parse(command.end_date()));
        setMaxUses(command.max_uses());
        setUsesCount(command.uses_count());
    }
}
