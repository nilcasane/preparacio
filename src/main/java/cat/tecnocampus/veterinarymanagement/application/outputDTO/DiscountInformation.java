package cat.tecnocampus.veterinarymanagement.application.outputDTO;

public record DiscountInformation(
    Long discount_id,
    String code,
    String discount_type,
    Double value_amount,
    String start_date,
    String end_date,
    Integer max_uses,
    Integer uses_count,
    Long promotion_id
) {}
