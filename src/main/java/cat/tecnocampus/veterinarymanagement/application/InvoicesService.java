package cat.tecnocampus.veterinarymanagement.application;

import cat.tecnocampus.veterinarymanagement.application.exceptions.*;
import cat.tecnocampus.veterinarymanagement.application.inputDTO.InvoiceCommand;
import cat.tecnocampus.veterinarymanagement.application.inputDTO.PaymentCommand;
import cat.tecnocampus.veterinarymanagement.application.mappers.InvoiceMapper;
import cat.tecnocampus.veterinarymanagement.application.outputDTO.InvoiceInformation;
import cat.tecnocampus.veterinarymanagement.domain.*;
import cat.tecnocampus.veterinarymanagement.persistence.InvoiceRepository;
import cat.tecnocampus.veterinarymanagement.persistence.VisitRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InvoicesService {
    private final InvoiceRepository invoiceRepository;
    private final VisitRepository visitRepository;

    public InvoicesService(InvoiceRepository invoiceRepository, VisitRepository visitRepository) {
        this.invoiceRepository = invoiceRepository;
        this.visitRepository = visitRepository;
    }

    public InvoiceInformation getInvoiceById(Long id) {
        Invoice inv = invoiceRepository.findById(id)
                .orElseThrow(() -> new InvoiceDoesNotExistException("Invoice with id " + id + " does not exist"));
        return InvoiceMapper.toInvoiceInformation(inv);
    }

    public List<InvoiceInformation> getAllInvoices(LocalDate startDate, LocalDate endDate, InvoiceStatus status, Long petOwnerId, Long petId) {
        return invoiceRepository.findInvoicesByFilters(startDate, endDate, status, petOwnerId, petId).stream()
                .map(InvoiceMapper::toInvoiceInformation)
                .collect(Collectors.toList());
    }

    @Transactional
    public Long createInvoice(InvoiceCommand command) {
        // Validate visit exists
        if (command.visit_id() == null) {
            throw new VisitDoesNotExistException("Visit id must be provided to create an invoice");
        }

        Visit visit = visitRepository.findById(command.visit_id())
                .orElseThrow(() -> new VisitDoesNotExistException("Visit with id " + command.visit_id() + " does not exist"));

        // Check if invoice already exists for this visit
        if (visit.getInvoice() != null) {
            throw new InvoiceAlreadyExistsException("Invoice already exists for visit with id " + command.visit_id());
        }

        // Create invoice
        Invoice invoice = new Invoice(visit);

        Invoice saved = invoiceRepository.save(invoice);
        return saved.getId();
    }

    public void deleteInvoice(Long id) {
        if (!invoiceRepository.existsById(id)) {
            throw new InvoiceDoesNotExistException("Invoice with id " + id + " does not exist");
        }
        invoiceRepository.deleteById(id);
    }

    @Transactional
    public InvoiceInformation payInvoice(Long invoiceId, PaymentCommand paymentCommand) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new InvoiceDoesNotExistException("Invoice with id " + invoiceId + " does not exist"));

        invoice.pay(PaymentMethod.valueOf(paymentCommand.payment_method()), paymentCommand.transaction_reference());

        Invoice savedInvoice = invoiceRepository.save(invoice);
        return InvoiceMapper.toInvoiceInformation(savedInvoice);
    }
}
