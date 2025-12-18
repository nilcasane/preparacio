package cat.tecnocampus.veterinarymanagement.application.mappers;

import cat.tecnocampus.veterinarymanagement.application.outputDTO.InvoiceInformation;
import cat.tecnocampus.veterinarymanagement.application.outputDTO.InvoiceItemInformation;
import cat.tecnocampus.veterinarymanagement.domain.*;

import java.util.List;
import java.util.stream.Collectors;

public class InvoiceMapper {
    public static InvoiceInformation toInvoiceInformation(Invoice invoice) {
        if (invoice == null) return null;
        List<InvoiceItemInformation> items = invoice.getItems() == null ? List.of() :
                invoice.getItems().stream().map(InvoiceMapper::toInvoiceItemInformation).collect(Collectors.toList());
        return new InvoiceInformation(invoice.getId(), invoice.getInvoiceDate(), invoice.getTotalAmount(), invoice.getStatus(),
                invoice.getVisit() != null ? invoice.getVisit().getId() : null,
                invoice.getPetOwner() != null ? invoice.getPetOwner().getId() : null,
                items);
    }

    public static InvoiceItemInformation toInvoiceItemInformation(InvoiceItem item) {
        if (item == null) return null;
        Long medId = item.getMedicationPrescription() != null ? item.getMedicationPrescription().getId() : null;
        Long treatmentId = item.getTreatment() != null ? item.getTreatment().getId() : null;
        return new InvoiceItemInformation(item.getId(), item.getDescription(), item.getQuantity(), item.getUnitPrice(), item.getTotalPrice(), medId, treatmentId);
    }
}
