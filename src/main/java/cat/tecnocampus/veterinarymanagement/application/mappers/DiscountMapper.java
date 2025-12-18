package cat.tecnocampus.veterinarymanagement.application.mappers;

import cat.tecnocampus.veterinarymanagement.application.inputDTO.DiscountCommand;
import cat.tecnocampus.veterinarymanagement.application.outputDTO.DiscountInformation;
import cat.tecnocampus.veterinarymanagement.domain.Discount;
import cat.tecnocampus.veterinarymanagement.domain.DiscountType;
import cat.tecnocampus.veterinarymanagement.domain.Promotion;

import java.time.LocalDate;

public class DiscountMapper {
    public static Discount inputDiscountToDomain(DiscountCommand inputDTO, Promotion promotion) {
        Discount discount = new Discount();
        discount.setCode(inputDTO.code());
        discount.setDiscountType(DiscountType.valueOf(inputDTO.discount_type()));
        discount.setValueAmount(inputDTO.value_amount());
        discount.setStartDate(LocalDate.parse(inputDTO.start_date()));
        discount.setEndDate(LocalDate.parse(inputDTO.end_date()));
        discount.setMaxUses(inputDTO.max_uses());
        discount.setUsesCount(inputDTO.uses_count());
        discount.setPromotion(promotion);
        return discount;
    }

    public static DiscountInformation toDiscountInformation(Discount discount) {
        return new DiscountInformation(
                discount.getId(),
                discount.getCode(),
                discount.getDiscountType().name(),
                discount.getValueAmount(),
                discount.getStartDate().toString(),
                discount.getEndDate().toString(),
                discount.getMaxUses(),
                discount.getUsesCount(),
                discount.getPromotion().getId()
        );
    }
}
