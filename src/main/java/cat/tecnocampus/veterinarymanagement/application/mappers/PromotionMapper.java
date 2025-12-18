package cat.tecnocampus.veterinarymanagement.application.mappers;

import cat.tecnocampus.veterinarymanagement.application.inputDTO.PromotionCommand;
import cat.tecnocampus.veterinarymanagement.application.outputDTO.DiscountInformation;
import cat.tecnocampus.veterinarymanagement.application.outputDTO.PromotionInformation;
import cat.tecnocampus.veterinarymanagement.domain.Discount;
import cat.tecnocampus.veterinarymanagement.domain.Promotion;

import java.time.LocalDate;

public class PromotionMapper {
    public static Promotion inputPromotionToDomain(PromotionCommand inputDTO) {
        Promotion promotion  = new Promotion();
        promotion.setName(inputDTO.name());
        promotion.setDiscountCode(inputDTO.discount_code());
        promotion.setDescription(inputDTO.description());
        promotion.setStartDate(LocalDate.parse(inputDTO.start_date()));
        promotion.setEndDate(LocalDate.parse(inputDTO.end_date()));
        return promotion;
    }

    public static PromotionInformation toPromotionInformation(Promotion promotion) {
        return new PromotionInformation(
                promotion.getId(),
                promotion.getName(),
                promotion.getDescription(),
                promotion.getDiscountCode(),
                promotion.getStartDate().toString(),
                promotion.getEndDate().toString()
        );
    }
}
