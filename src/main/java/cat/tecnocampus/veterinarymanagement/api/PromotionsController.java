package cat.tecnocampus.veterinarymanagement.api;

import cat.tecnocampus.veterinarymanagement.application.PromotionsService;
import cat.tecnocampus.veterinarymanagement.application.exceptions.DiscountDoesNotExistException;
import cat.tecnocampus.veterinarymanagement.application.exceptions.LoyaltyTierDoesNotExistException;
import cat.tecnocampus.veterinarymanagement.application.exceptions.PromotionDoesNotExistException;
import cat.tecnocampus.veterinarymanagement.application.inputDTO.DiscountCommand;
import cat.tecnocampus.veterinarymanagement.application.inputDTO.LoyaltyTierCommand;
import cat.tecnocampus.veterinarymanagement.application.inputDTO.PromotionCommand;
import cat.tecnocampus.veterinarymanagement.application.outputDTO.DiscountInformation;
import cat.tecnocampus.veterinarymanagement.application.outputDTO.LoyaltyTierInformation;
import cat.tecnocampus.veterinarymanagement.application.outputDTO.PromotionInformation;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/promotions")
public class PromotionsController {
    private final PromotionsService promotionsService;

    public PromotionsController(PromotionsService promotionsService) {
        this.promotionsService = promotionsService;
    }


    @PostMapping
    public ResponseEntity<PromotionInformation> createPromotion(@RequestBody PromotionCommand input,
                                                                UriComponentsBuilder uriBuilder) {
        Long id = promotionsService.createPromotion(input);
        var location = uriBuilder.path("/promotions/{id}").buildAndExpand(id).toUri();
        PromotionInformation info = promotionsService.getPromotionById(id)
                .orElseThrow(() -> new PromotionDoesNotExistException("Promotion with id " + id + " does not exist"));
        return ResponseEntity.created(location).body(info);
    }

    @GetMapping("/{promotion_id}")
    public PromotionInformation getPromotion(@PathVariable Long promotion_id) {
        return promotionsService.getPromotionById(promotion_id)
                .orElseThrow(() -> new PromotionDoesNotExistException("Promotion with id " + promotion_id + " does not exist"));
    }

    @PostMapping("{promotion_id}/discounts")
    public ResponseEntity<DiscountInformation> createDiscount(@PathVariable Long promotion_id,
                                                              @RequestBody @Valid DiscountCommand input,
                                                              UriComponentsBuilder uriBuilder) {
        Long discount_id = promotionsService.createDiscount(promotion_id, input);
        var location = uriBuilder.path("/discounts/{id}").buildAndExpand(discount_id).toUri();
        DiscountInformation info = promotionsService.getDiscountById(discount_id)
                .orElseThrow(() -> new DiscountDoesNotExistException("Discount with id " + discount_id + " does not exist"));
        return ResponseEntity.created(location).body(info);
    }

    /**
     * Get all discounts from a Promotion
     * @param promotion_id Promotion ID
     * @return List of Discounts
     */
    @GetMapping("/{promotion_id}/discounts")
    public List<DiscountInformation> getDiscounts(
            @PathVariable Long promotion_id) {
        return promotionsService.getDiscounts(promotion_id);
    }

    /**
     * Delete all discounts from an Promotion
     * @param promotion_id Promotion ID
     */
    @DeleteMapping("/{promotion_id}/discounts")
    public ResponseEntity<Void> deleteDiscounts(
            @PathVariable Long promotion_id) {
        promotionsService.deleteDiscounts(promotion_id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{promotion_id}/discounts/{discount_id}")
    public DiscountInformation getDiscount(@PathVariable Long discount_id) {
        return promotionsService.getDiscountById(discount_id)
                .orElseThrow(() -> new DiscountDoesNotExistException("Discount with id " + discount_id + " does not exist"));
    }

    @PutMapping("/{promotion_id}/discounts/{discount_id}")
    public ResponseEntity<DiscountInformation> updateDiscount(@PathVariable Long discount_id,
                                                              @RequestBody @Valid DiscountCommand input) {
        DiscountInformation discountInformation = promotionsService.updateDiscount(discount_id, input);
        return ResponseEntity.ok(discountInformation);
    }

    @PostMapping("/{promotion_id}/discounts/{discount_id}/loyaltyTiers")
    public ResponseEntity<LoyaltyTierInformation> createLoyaltyTiers(@RequestBody LoyaltyTierCommand input,
                                                                     UriComponentsBuilder uriBuilder,
                                                                     @PathVariable Long discount_id) {
        Long id = promotionsService.createLoyaltyTier(discount_id, input);
        var location = uriBuilder.path("/loyaltyTiers/{id}").buildAndExpand(id).toUri();
        LoyaltyTierInformation info = promotionsService.getLoyaltyTierById(id)
                .orElseThrow(() -> new LoyaltyTierDoesNotExistException("Loyalty Tier with id " + id + " does not exist"));
        return ResponseEntity.created(location).body(info);
    }

    @GetMapping("/{promotion_id}/discounts/{discount_id}/loyaltyTiers")
    public List<LoyaltyTierInformation> getLoyaltyTiers(@PathVariable Long discount_id) {
        return promotionsService.getLoyaltyTiers(discount_id);
    }

    @GetMapping("/{promotion_id}/discounts/{discount_id}/{loyalty_tier_id}")
    public LoyaltyTierInformation getLoyaltyTierById(@PathVariable Long loyalty_tier_id) {
        return promotionsService.getLoyaltyTierById(loyalty_tier_id)
                .orElseThrow(() -> new LoyaltyTierDoesNotExistException("Loyalty Tier with id " + loyalty_tier_id + " does not exist"));

    }

    @PutMapping("/{promotion_id}/discounts/{discount_id}/loyaltyTiers{loyalty_tier_id}")
    public ResponseEntity<LoyaltyTierInformation> updateLoyaltyTier(@PathVariable Long loyalty_tier_id,
                                                                    @RequestBody LoyaltyTierCommand input) {
        LoyaltyTierInformation info = promotionsService.updateLoyaltyTier(loyalty_tier_id, input);
        return ResponseEntity.ok(info);
    }

    @DeleteMapping("/{promotion_id}/discounts/{discount_id}/loyaltyTiers/{loyalty_tier_id}")
    public ResponseEntity<Void> deleteLoyaltyTierById(@PathVariable Long loyalty_tier_id) {
        promotionsService.deleteLoyaltyTierById(loyalty_tier_id);
        return ResponseEntity.noContent().build();
    }
}
