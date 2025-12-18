package cat.tecnocampus.veterinarymanagement.service;

import cat.tecnocampus.veterinarymanagement.application.PromotionsService;
import cat.tecnocampus.veterinarymanagement.application.inputDTO.DiscountCommand;
import cat.tecnocampus.veterinarymanagement.application.inputDTO.LoyaltyTierCommand;
import cat.tecnocampus.veterinarymanagement.application.inputDTO.PromotionCommand;
import cat.tecnocampus.veterinarymanagement.application.outputDTO.DiscountInformation;
import cat.tecnocampus.veterinarymanagement.application.outputDTO.LoyaltyTierInformation;
import cat.tecnocampus.veterinarymanagement.application.outputDTO.PromotionInformation;
import cat.tecnocampus.veterinarymanagement.application.exceptions.DiscountDoesNotExistException;
import cat.tecnocampus.veterinarymanagement.application.exceptions.PromotionDoesNotExistException;
import cat.tecnocampus.veterinarymanagement.domain.Discount;
import cat.tecnocampus.veterinarymanagement.domain.LoyaltyTier;
import cat.tecnocampus.veterinarymanagement.domain.Promotion;
import cat.tecnocampus.veterinarymanagement.persistence.DiscountRepository;
import cat.tecnocampus.veterinarymanagement.persistence.LoyaltyTierRepository;
import cat.tecnocampus.veterinarymanagement.persistence.PromotionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.context.jdbc.Sql;

import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Sql(scripts = "classpath:cleanup-test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:data-test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class PromotionsServiceTest {
    @Autowired
    private PromotionsService promotionsService;

    @Autowired
    private PromotionRepository promotionRepository;

    @Autowired
    private DiscountRepository discountRepository;

    @Autowired
    private LoyaltyTierRepository loyaltyTierRepository;

    // Helper to create a promotion if none exists (data-test.sql doesn't insert promotions)
    private Long ensurePromotionExists() {
        if (promotionRepository.count() == 0) {
            Promotion p = new Promotion();
            p.setName("Summer Promo");
            p.setDescription("Summer discounts");
            p.setDiscountCode("SUMMER2025");
            p.setStartDate(java.time.LocalDate.parse("2025-01-01"));
            p.setEndDate(java.time.LocalDate.parse("2025-12-31"));
            promotionRepository.save(p);
            return p.getId();
        }
        return promotionRepository.findAll().getFirst().getId();
    }

    // Helper to ensure a discount exists (creates a promotion + discount if needed)
    private Long ensureDiscountExists() {
        // create promotion if none
        if (promotionRepository.count() == 0) {
            Promotion p = new Promotion();
            p.setName("Default Promo");
            p.setDescription("Default promotion");
            p.setDiscountCode("DEF2025");
            p.setStartDate(java.time.LocalDate.parse("2025-01-01"));
            p.setEndDate(java.time.LocalDate.parse("2025-12-31"));
            promotionRepository.save(p);
        }
        Promotion promo = promotionRepository.findAll().getFirst();

        // create discount if none
        if (discountRepository.count() == 0) {
            Discount d = new Discount();
            d.setCode("D100");
            d.setDiscountType(cat.tecnocampus.veterinarymanagement.domain.DiscountType.PERCENTAGE);
            d.setValueAmount(10.0);
            d.setStartDate(java.time.LocalDate.parse("2025-01-01"));
            d.setEndDate(java.time.LocalDate.parse("2025-12-31"));
            d.setMaxUses(100);
            d.setUsesCount(0);
            d.setPromotion(promo);
            discountRepository.save(d);
            return d.getId();
        }
        return discountRepository.findAll().getFirst().getId();
    }

    // ========== Promotion tests (from original PromotionsServiceTest) ==========

    @Test
    public void createPromotionHappyTest() {
        PromotionCommand command = new PromotionCommand("Black Friday", "Huge discounts", "BF2025", "2025-11-01", "2025-11-30");

        long before = promotionRepository.count();
        Long id = promotionsService.createPromotion(command);
        long after = promotionRepository.count();

        assertEquals(before + 1, after);
        assertNotNull(id);

        Promotion saved = promotionRepository.findById(id).orElseThrow();
        assertEquals("Black Friday", saved.getName());
        assertEquals("Huge discounts", saved.getDescription());
        assertEquals("BF2025", saved.getDiscountCode());
        assertEquals("2025-11-01", saved.getStartDate().toString());
        assertEquals("2025-11-30", saved.getEndDate().toString());
    }

    @Test
    public void createPromotionInvalidCommandTest() {
        // invalid date format in command should cause DateTimeParseException during mapping
        PromotionCommand invalid = new PromotionCommand("Xmas", "desc", "XMAS", "invalid", "2025-12-31");
        assertThrows(DateTimeParseException.class, () -> {
            promotionsService.createPromotion(invalid);
        });
    }

    @Test
    public void createPromotionNullCommandTest() {
        // Passing null should cause a NullPointerException (no explicit handling in service)
        assertThrows(NullPointerException.class, () -> {
            promotionsService.createPromotion(null);
        });
    }

    @Test
    public void getPromotionByExistingIdTest() {
        PromotionCommand command = new PromotionCommand("Summer", "Summer deals", "SUM2025", "2025-06-01", "2025-06-30");
        Long id = promotionsService.createPromotion(command);

        Optional<PromotionInformation> infoOpt = promotionsService.getPromotionById(id);
        assertTrue(infoOpt.isPresent());
        PromotionInformation info = infoOpt.orElseThrow();

        assertEquals(id, info.id());
        assertEquals("Summer", info.name());
        assertEquals("Summer deals", info.description());
        assertEquals("SUM2025", info.discountCode());
        assertEquals("2025-06-01", info.startDate());
        assertEquals("2025-06-30", info.endDate());
    }

    @Test
    public void getPromotionByNonExistingIdTest() {
        Optional<PromotionInformation> infoOpt = promotionsService.getPromotionById(99999L);
        assertTrue(infoOpt.isEmpty());
    }

    @Test
    public void getPromotionByNullIdTest() {
        assertThrows(InvalidDataAccessApiUsageException.class, () -> {
            promotionsService.getPromotionById(null);
        });
    }

    // ========== Discount tests (from original DiscountsServiceTest) ==========

    @Test
    public void createDiscountHappyTest() {
        Long promoId = ensurePromotionExists();

        DiscountCommand command = new DiscountCommand("CODE1", "PERCENTAGE", 10.0, "2025-04-01", "2025-09-30", 100, 0);
        long before = discountRepository.count();
        Long id = promotionsService.createDiscount(promoId, command);
        long after = discountRepository.count();

        assertEquals(before + 1, after);
        assertNotNull(id);

        Discount saved = discountRepository.findById(id).orElseThrow();
        assertEquals("CODE1", saved.getCode());
        assertEquals("PERCENTAGE", saved.getDiscountType().name());
        assertEquals(10.0, saved.getValueAmount());
        assertEquals("2025-04-01", saved.getStartDate().toString());
        assertEquals("2025-09-30", saved.getEndDate().toString());
        assertEquals(100, saved.getMaxUses());
        assertEquals(0, saved.getUsesCount());
        assertEquals(promoId, saved.getPromotion().getId());
    }

    @Test
    public void createDiscountNonExistingPromotionTest() {
        DiscountCommand command = new DiscountCommand("CODE1", "PERCENTAGE", 10.0, "2025-04-01", "2025-09-30", 100, 0);
        Exception exception = assertThrows(PromotionDoesNotExistException.class, () -> {
            promotionsService.createDiscount(999L, command);
        });

        assertTrue(exception.getMessage().contains("Promotion with id 999 does not exist"));
    }

    @Test
    public void createDiscountNullPromotionIdTest() {
        DiscountCommand command = new DiscountCommand("CODE1", "PERCENTAGE", 10.0, "2025-04-01", "2025-09-30", 100, 0);
        assertThrows(IllegalArgumentException.class, () -> {
            promotionsService.createDiscount(null, command);
        });
    }

    @Test
    public void getDiscountByExistingIdTest() {
        Long promoId = ensurePromotionExists();
        DiscountCommand command = new DiscountCommand("CODE2", "FIXED_AMOUNT", 5.0, "2025-05-01", "2025-05-31", 10, 0);
        Long id = promotionsService.createDiscount(promoId, command);

        Optional<DiscountInformation> infoOpt = promotionsService.getDiscountById(id);
        assertTrue(infoOpt.isPresent());
        DiscountInformation info = infoOpt.orElseThrow();

        assertEquals(id, info.discount_id());
        assertEquals("CODE2", info.code());
        assertEquals("FIXED_AMOUNT", info.discount_type());
        assertEquals(5.0, info.value_amount());
        assertEquals("2025-05-01", info.start_date());
        assertEquals("2025-05-31", info.end_date());
        assertEquals(10, info.max_uses());
        assertEquals(0, info.uses_count());
        assertEquals(promoId, info.promotion_id());
    }

    @Test
    public void getDiscountByNonExistingIdTest() {
        Optional<DiscountInformation> infoOpt = promotionsService.getDiscountById(555L);
        assertTrue(infoOpt.isEmpty());
    }

    @Test
    public void getDiscountsHappyTest() {
        Long promoId = ensurePromotionExists();
        DiscountCommand a = new DiscountCommand("A", "PERCENTAGE", 10.0, "2025-06-01", "2025-06-30", 10, 0);
        DiscountCommand b = new DiscountCommand("B", "FIXED_AMOUNT", 3.0, "2025-06-01", "2025-06-30", 5, 0);
        Long idA = promotionsService.createDiscount(promoId, a);
        Long idB = promotionsService.createDiscount(promoId, b);

        List<DiscountInformation> discounts = promotionsService.getDiscounts(promoId);
        List<Long> ids = discounts.stream().map(DiscountInformation::discount_id).toList();

        assertEquals(2, discounts.size());
        assertThat(ids, containsInAnyOrder(idA, idB));
    }

    @Test
    public void getDiscountsNonExistingPromotionTest() {
        Exception exception = assertThrows(PromotionDoesNotExistException.class, () -> {
            promotionsService.getDiscounts(777L);
        });

        assertTrue(exception.getMessage().contains("Promotion with id 777 does not exist"));
    }

    @Test
    public void updateDiscountHappyTest() {
        Long promoId = ensurePromotionExists();
        DiscountCommand create = new DiscountCommand("UPD1", "PERCENTAGE", 15.0, "2025-07-01", "2025-07-31", 50, 0);
        Long id = promotionsService.createDiscount(promoId, create);

        DiscountCommand update = new DiscountCommand("UPD1-NEW", "FIXED_AMOUNT", 7.5, "2025-08-01", "2025-08-31", 60, 1);
        var updated = promotionsService.updateDiscount(id, update);

        assertEquals(id, updated.discount_id());
        assertEquals("UPD1-NEW", updated.code());
        assertEquals("FIXED_AMOUNT", updated.discount_type());
        assertEquals(7.5, updated.value_amount());
        assertEquals("2025-08-01", updated.start_date());
        assertEquals("2025-08-31", updated.end_date());
        assertEquals(60, updated.max_uses());
        assertEquals(1, updated.uses_count());
    }

    @Test
    public void updateDiscountNonExistingTest() {
        DiscountCommand update = new DiscountCommand("X", "PERCENTAGE", 1.0, "2025-01-01", "2025-01-02", 1, 0);
        Exception exception = assertThrows(DiscountDoesNotExistException.class, () -> {
            promotionsService.updateDiscount(9999L, update);
        });

        assertTrue(exception.getMessage().contains("Discount with id 9999 does not exist"));
    }

    @Test
    public void updateDiscountInvalidCommandTest() {
        Long promoId = ensurePromotionExists();
        DiscountCommand create = new DiscountCommand("BAD1", "PERCENTAGE", 2.0, "2025-09-01", "2025-09-30", 5, 0);
        Long id = promotionsService.createDiscount(promoId, create);

        // invalid date format will cause DateTimeParseException in update
        DiscountCommand invalid = new DiscountCommand("BAD1", "PERCENTAGE", 2.0, "invalid-date", "2025-09-30", 5, 0);
        assertThrows(DateTimeParseException.class, () -> {
            promotionsService.updateDiscount(id, invalid);
        });
    }

    @Test
    public void updateDiscountInvalidTypeTest() {
        Long promoId = ensurePromotionExists();
        DiscountCommand create = new DiscountCommand("TYPE1", "PERCENTAGE", 2.0, "2025-10-01", "2025-10-31", 5, 0);
        Long id = promotionsService.createDiscount(promoId, create);

        // invalid enum will cause IllegalArgumentException when mapping
        DiscountCommand invalidType = new DiscountCommand("TYPE1", "UNKNOWN", 2.0, "2025-10-01", "2025-10-31", 5, 0);
        assertThrows(IllegalArgumentException.class, () -> {
            promotionsService.updateDiscount(id, invalidType);
        });
    }

    @Test
    public void deleteDiscountsHappyTest() {
        Long promoId = ensurePromotionExists();
        DiscountCommand a = new DiscountCommand("DEL1", "PERCENTAGE", 1.0, "2025-11-01", "2025-11-30", 2, 0);
        DiscountCommand b = new DiscountCommand("DEL2", "FIXED_AMOUNT", 2.0, "2025-11-01", "2025-11-30", 3, 0);
        promotionsService.createDiscount(promoId, a);
        promotionsService.createDiscount(promoId, b);

        Promotion promotion = promotionRepository.findById(promoId).orElseThrow();
        List<Discount> found = discountRepository.findByPromotion(promotion);
        assertEquals(2, found.size());

        promotionsService.deleteDiscounts(promoId);

        assertEquals(0, discountRepository.findByPromotion(promotion).size());
    }

    @Test
    public void deleteDiscountsNonExistingPromotionTest() {
        Exception exception = assertThrows(PromotionDoesNotExistException.class, () -> {
            promotionsService.deleteDiscounts(888L);
        });

        assertTrue(exception.getMessage().contains("Promotion with id 888 does not exist"));
    }

    // ========== Loyalty Tier tests (from original LoyaltyTiersServiceTest) ==========

    @Test
    public void getLoyaltyTierByExistingIdTest() {
        Long discountId = ensureDiscountExists();
        LoyaltyTierCommand command = new LoyaltyTierCommand("Gold", "Top tier", 1000, "Free exam");
        Long id = promotionsService.createLoyaltyTier(discountId, command);

        Optional<LoyaltyTierInformation> opt = promotionsService.getLoyaltyTierById(id);
        assertTrue(opt.isPresent());
        LoyaltyTierInformation info = opt.orElseThrow();

        assertEquals(id, info.id());
        assertEquals("Gold", info.name());
        assertEquals("Top tier", info.description());
        assertEquals(1000, info.min_points());
        assertEquals("Free exam", info.benefits());
        assertEquals(discountId, info.discount_id());
    }

    @Test
    public void getLoyaltyTierByNonExistingIdTest() {
        Optional<LoyaltyTierInformation> infoOpt = promotionsService.getLoyaltyTierById(9999L);
        assertTrue(infoOpt.isEmpty());
    }

    @Test
    public void createLoyaltyTierHappyTest() {
        Long discountId = ensureDiscountExists();
        LoyaltyTierCommand command = new LoyaltyTierCommand("Silver", "Mid tier", 500, "10% off");

        long before = loyaltyTierRepository.count();
        Long id = promotionsService.createLoyaltyTier(discountId, command);
        long after = loyaltyTierRepository.count();

        assertEquals(before + 1, after);
        assertNotNull(id);

        LoyaltyTier saved = loyaltyTierRepository.findById(id).orElseThrow();
        assertEquals("Silver", saved.getName());
        assertEquals("Mid tier", saved.getDescription());
        assertEquals(500, saved.getMinPoints());
        assertEquals("10% off", saved.getBenefits());
        assertEquals(discountId, saved.getDiscount().getId());
    }

    @Test
    public void createLoyaltyTierNonExistingDiscountTest() {
        LoyaltyTierCommand command = new LoyaltyTierCommand("Bronze", "Low tier", 100, "5% off");
        Exception exception = assertThrows(DiscountDoesNotExistException.class, () -> {
            promotionsService.createLoyaltyTier(12345L, command);
        });

        assertTrue(exception.getMessage().contains("Discount with id 12345 does not exist"));
    }

    @Test
    public void createLoyaltyTierNullDiscountIdTest() {
        LoyaltyTierCommand command = new LoyaltyTierCommand("X", "Y", 1, "Z");
        assertThrows(IllegalArgumentException.class, () -> {
            promotionsService.createLoyaltyTier(null, command);
        });
    }

    @Test
    public void getLoyaltyTiersHappyTest() {
        Long discountId = ensureDiscountExists();
        LoyaltyTierCommand a = new LoyaltyTierCommand("A", "a", 10, "b");
        LoyaltyTierCommand b = new LoyaltyTierCommand("B", "b", 20, "c");
        Long idA = promotionsService.createLoyaltyTier(discountId, a);
        Long idB = promotionsService.createLoyaltyTier(discountId, b);

        List<LoyaltyTierInformation> tiers = promotionsService.getLoyaltyTiers(discountId);
        List<Long> ids = tiers.stream().map(LoyaltyTierInformation::id).toList();

        assertEquals(2, tiers.size());
        assertThat(ids, containsInAnyOrder(idA, idB));
    }

    @Test
    public void getLoyaltyTiersNonExistingDiscountTest() {
        Exception exception = assertThrows(DiscountDoesNotExistException.class, () -> {
            promotionsService.getLoyaltyTiers(7777L);
        });

        assertTrue(exception.getMessage().contains("Discount with id 7777 does not exist"));
    }

    @Test
    public void updateLoyaltyTierHappyTest() {
        Long discountId = ensureDiscountExists();
        LoyaltyTierCommand create = new LoyaltyTierCommand("Starter", "desc", 50, "benefit");
        Long id = promotionsService.createLoyaltyTier(discountId, create);

        LoyaltyTierCommand update = new LoyaltyTierCommand("StarterPLUS", "descNEW", 75, "benefitNEW");
        var updated = promotionsService.updateLoyaltyTier(id, update);

        assertEquals(id, updated.id());
        assertEquals("StarterPLUS", updated.name());
        assertEquals("descNEW", updated.description());
        assertEquals(75, updated.min_points());
        assertEquals("benefitNEW", updated.benefits());
    }

    @Test
    public void updateLoyaltyTierNonExistingTest() {
        LoyaltyTierCommand update = new LoyaltyTierCommand("X", "Y", 1, "Z");
        Exception exception = assertThrows(DiscountDoesNotExistException.class, () -> {
            promotionsService.updateLoyaltyTier(99999L, update);
        });

        assertTrue(exception.getMessage().contains("Loyalty Tier with id 99999 does not exist"));
    }

    @Test
    public void updateLoyaltyTierNullIdTest() {
        LoyaltyTierCommand update = new LoyaltyTierCommand("X", "Y", 1, "Z");
        assertThrows(IllegalArgumentException.class, () -> {
            promotionsService.updateLoyaltyTier(null, update);
        });
    }

    @Test
    public void deleteLoyaltyTierHappyTest() {
        Long discountId = ensureDiscountExists();
        LoyaltyTierCommand command = new LoyaltyTierCommand("Del", "to delete", 0, "none");
        Long id = promotionsService.createLoyaltyTier(discountId, command);

        long before = loyaltyTierRepository.count();
        promotionsService.deleteLoyaltyTierById(id);
        long after = loyaltyTierRepository.count();

        assertEquals(before - 1, after);
        assertTrue(loyaltyTierRepository.findById(id).isEmpty());
    }

    @Test
    public void deleteLoyaltyTierNonExistingTest() {
        Exception exception = assertThrows(DiscountDoesNotExistException.class, () -> {
            promotionsService.deleteLoyaltyTierById(55555L);
        });

        assertTrue(exception.getMessage().contains("Loyalty Tier with id 55555 does not exist"));
    }

    @Test
    public void deleteLoyaltyTierNullIdTest() {
        assertThrows(IllegalArgumentException.class, () -> {
            promotionsService.deleteLoyaltyTierById(null);
        });
    }
}
