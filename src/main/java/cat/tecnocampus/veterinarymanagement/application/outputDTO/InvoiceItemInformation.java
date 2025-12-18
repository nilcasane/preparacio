package cat.tecnocampus.veterinarymanagement.application.outputDTO;

public record InvoiceItemInformation(Long id, String description, Integer quantity, Double unitPrice, Double totalPrice, Long medicationId, Long treatmentId) {
}
