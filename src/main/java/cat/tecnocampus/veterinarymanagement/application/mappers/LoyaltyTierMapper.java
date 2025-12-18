package cat.tecnocampus.veterinarymanagement.application.mappers;

import cat.tecnocampus.veterinarymanagement.application.inputDTO.LoyaltyTierCommand;
import cat.tecnocampus.veterinarymanagement.application.outputDTO.LoyaltyTierInformation;
import cat.tecnocampus.veterinarymanagement.domain.Discount;
import cat.tecnocampus.veterinarymanagement.domain.LoyaltyTier;

import java.util.Set;

public class LoyaltyTierMapper {
    public static LoyaltyTier inputLoyaltyTierToDomain(LoyaltyTierCommand command, Discount discount) {
        LoyaltyTier loyaltyTier = new LoyaltyTier();
        loyaltyTier.setName(command.name());
        loyaltyTier.setDescription(command.description());
        loyaltyTier.setMinPoints(command.min_points());
        loyaltyTier.setBenefits(command.benefits());
        loyaltyTier.setDiscount(discount);
        return loyaltyTier;
    }

    public static LoyaltyTierInformation toLoyaltyTierInformation(LoyaltyTier loyaltyTier) {
        Long discountId = loyaltyTier.getDiscount() != null ? loyaltyTier.getDiscount().getId() : null;
        return new LoyaltyTierInformation(
            loyaltyTier.getId(),
            loyaltyTier.getName(),
            loyaltyTier.getDescription(),
            loyaltyTier.getMinPoints(),
            loyaltyTier.getBenefits(),
            discountId
        );
    }
}
