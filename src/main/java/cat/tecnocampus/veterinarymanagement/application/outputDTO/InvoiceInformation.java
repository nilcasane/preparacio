package cat.tecnocampus.veterinarymanagement.application.outputDTO;

import cat.tecnocampus.veterinarymanagement.domain.InvoiceStatus;

import java.time.LocalDate;
import java.util.List;

public record InvoiceInformation(Long id, LocalDate invoiceDate, Double totalAmount, InvoiceStatus status, Long visitId, Long petOwnerId, List<InvoiceItemInformation> items) {
}
