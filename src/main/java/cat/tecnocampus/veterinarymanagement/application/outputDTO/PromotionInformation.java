package cat.tecnocampus.veterinarymanagement.application.outputDTO;

public record PromotionInformation(
    Long id,
    String name,
    String description,
    String discountCode,
    String startDate, // YYYY-MM-DD
    String endDate    // YYYY-MM-DD
) {}

