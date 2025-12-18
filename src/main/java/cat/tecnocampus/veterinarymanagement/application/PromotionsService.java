package cat.tecnocampus.veterinarymanagement.application;

import cat.tecnocampus.veterinarymanagement.application.exceptions.DiscountDoesNotExistException;
import cat.tecnocampus.veterinarymanagement.application.exceptions.PromotionDoesNotExistException;
import cat.tecnocampus.veterinarymanagement.application.inputDTO.DiscountCommand;
import cat.tecnocampus.veterinarymanagement.application.inputDTO.LoyaltyTierCommand;
import cat.tecnocampus.veterinarymanagement.application.inputDTO.PromotionCommand;
import cat.tecnocampus.veterinarymanagement.application.mappers.DiscountMapper;
import cat.tecnocampus.veterinarymanagement.application.mappers.LoyaltyTierMapper;
import cat.tecnocampus.veterinarymanagement.application.mappers.PromotionMapper;
import cat.tecnocampus.veterinarymanagement.application.outputDTO.DiscountInformation;
import cat.tecnocampus.veterinarymanagement.application.outputDTO.LoyaltyTierInformation;
import cat.tecnocampus.veterinarymanagement.application.outputDTO.PromotionInformation;
import cat.tecnocampus.veterinarymanagement.domain.Discount;
import cat.tecnocampus.veterinarymanagement.domain.LoyaltyTier;
import cat.tecnocampus.veterinarymanagement.domain.Promotion;
import cat.tecnocampus.veterinarymanagement.persistence.DiscountRepository;
import cat.tecnocampus.veterinarymanagement.persistence.LoyaltyTierRepository;
import cat.tecnocampus.veterinarymanagement.persistence.PromotionRepository;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PromotionsService {
    private final PromotionRepository promotionRepository;
    private final DiscountRepository discountRepository;
    private final LoyaltyTierRepository loyaltyTierRepository;

    public PromotionsService(PromotionRepository promotionRepository, DiscountRepository discountRepository,
                             LoyaltyTierRepository loyaltyTierRepository) {
        this.promotionRepository = promotionRepository;
        this.discountRepository = discountRepository;
        this.loyaltyTierRepository = loyaltyTierRepository;
    }

    public Long createPromotion(PromotionCommand input) {
        Promotion promotion = PromotionMapper.inputPromotionToDomain(input);
        promotionRepository.save(promotion);
        return promotion.getId();
    }

    public Optional<PromotionInformation> getPromotionById(Long id) {
        return promotionRepository.findById(id)
                .map(PromotionMapper::toPromotionInformation);
    }

    public Long createDiscount(Long promotionId, DiscountCommand input) {
        try {
            Promotion promotion = promotionRepository.findById(promotionId).orElseThrow(() ->
                    new PromotionDoesNotExistException("Promotion with id " + promotionId + " does not exist"));

            Discount discount = DiscountMapper.inputDiscountToDomain(input, promotion);
            discountRepository.save(discount);
            return discount.getId();
        } catch (InvalidDataAccessApiUsageException e) {
            throw new IllegalArgumentException("Error creating the discount");
        }
    }

    public Optional<DiscountInformation> getDiscountById(Long id) {
        return discountRepository.findDiscountById(id)
                .map(DiscountMapper::toDiscountInformation);
    }

    public List<DiscountInformation> getDiscounts(Long promotionId) {
        Promotion promotion = promotionRepository
                .findById(promotionId)
                .orElseThrow(() -> new PromotionDoesNotExistException("Promotion with id " + promotionId + " does not exist"));

        List<Discount> discounts = discountRepository.findByPromotion(promotion);
        return discounts.stream()
                .map(DiscountMapper::toDiscountInformation)
                .toList();
    }

    public DiscountInformation updateDiscount(Long discountId, DiscountCommand command) {
        try {
            Discount discount = discountRepository.findById(discountId)
                    .orElseThrow(() -> new DiscountDoesNotExistException("Discount with id " + discountId + " does not exist"));
            discount.updateDiscount(command);
            discountRepository.save(discount);
            return DiscountMapper.toDiscountInformation(discount);
        } catch (InvalidDataAccessApiUsageException e) {
            throw new IllegalArgumentException("Error updating the discount");
        }
    }

    public void deleteDiscounts(Long promotionId) {
        Promotion promotion = promotionRepository
                .findById(promotionId)
                .orElseThrow(() -> new PromotionDoesNotExistException("Promotion with id " + promotionId + " does not exist"));
        List<Discount> discounts = discountRepository.findByPromotion(promotion);
        discountRepository.deleteAll(discounts);
    }

    public Long createLoyaltyTier(Long discount_id, LoyaltyTierCommand input) {
        try {
            Discount discount = discountRepository
                    .findById(discount_id)
                    .orElseThrow(() -> new DiscountDoesNotExistException("Discount with id " + discount_id + " does not exist"));

            LoyaltyTier loyaltyTier = LoyaltyTierMapper.inputLoyaltyTierToDomain(input, discount);
            var saved = loyaltyTierRepository.save(loyaltyTier);
            return saved.getId();
        } catch (InvalidDataAccessApiUsageException e) {
            throw new IllegalArgumentException("Error creating the loyalty tier");
        }
    }

    public Optional<LoyaltyTierInformation> getLoyaltyTierById(Long id) {
        return loyaltyTierRepository.findLoyaltyTierInformationById(id);
    }

    public List<LoyaltyTierInformation> getLoyaltyTiers(Long id) {
        Discount discount = discountRepository
                .findById(id)
                .orElseThrow(() -> new DiscountDoesNotExistException("Discount with id " + id + " does not exist"));

        return loyaltyTierRepository.findByDiscount(discount).stream()
                .map(LoyaltyTierMapper::toLoyaltyTierInformation)
                .toList();
    }

    public LoyaltyTierInformation updateLoyaltyTier(Long id, LoyaltyTierCommand command) {
        try {
            LoyaltyTier loyaltyTier = loyaltyTierRepository.findById(id)
                    .orElseThrow(() -> new DiscountDoesNotExistException("Loyalty Tier with id " + id + " does not exist"));

            loyaltyTier.updateLoyaltyTier(command);
            loyaltyTierRepository.save(loyaltyTier);
            return LoyaltyTierMapper.toLoyaltyTierInformation(loyaltyTier);
        } catch (InvalidDataAccessApiUsageException e) {
            throw new IllegalArgumentException("Error updating the loyalty tier with id " + id, e);
        }
    }

    public void deleteLoyaltyTierById(Long id) {
        try {
            LoyaltyTier loyaltyTier = loyaltyTierRepository.findById(id)
                    .orElseThrow(() -> new DiscountDoesNotExistException("Loyalty Tier with id " + id + " does not exist"));

            loyaltyTierRepository.delete(loyaltyTier);
        } catch (InvalidDataAccessApiUsageException e) {
            throw new IllegalArgumentException("Error deleting the loyalty tier with id " + id, e);
        }
    }
}
