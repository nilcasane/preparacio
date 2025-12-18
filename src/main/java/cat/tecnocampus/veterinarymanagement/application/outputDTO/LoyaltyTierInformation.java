package cat.tecnocampus.veterinarymanagement.application.outputDTO;

import cat.tecnocampus.veterinarymanagement.domain.Discount;

import java.util.Set;

public record LoyaltyTierInformation(
    Long id,
    String name,
    String description,
    Integer min_points,
    String benefits,
    Long discount_id
) {}
