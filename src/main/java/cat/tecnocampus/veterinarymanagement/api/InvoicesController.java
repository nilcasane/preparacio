package cat.tecnocampus.veterinarymanagement.api;

import cat.tecnocampus.veterinarymanagement.application.InvoicesService;
import cat.tecnocampus.veterinarymanagement.application.inputDTO.InvoiceCommand;
import cat.tecnocampus.veterinarymanagement.application.inputDTO.PaymentCommand;
import cat.tecnocampus.veterinarymanagement.application.outputDTO.InvoiceInformation;
import cat.tecnocampus.veterinarymanagement.domain.InvoiceStatus;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/invoices")
public class InvoicesController {
    private final InvoicesService invoicesService;

    public InvoicesController(InvoicesService invoicesService) {
        this.invoicesService = invoicesService;
    }

    @GetMapping("/{invoice_id}")
    public InvoiceInformation getInvoice(@PathVariable Long invoice_id) {
        return invoicesService.getInvoiceById(invoice_id);
    }

    @GetMapping
    public List<InvoiceInformation> getAllInvoices(
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) InvoiceStatus status,
            @RequestParam(required = false) Long petOwnerId,
            @RequestParam(required = false) Long petId) {
        return invoicesService.getAllInvoices(startDate, endDate, status, petOwnerId, petId);
    }

    @PostMapping
    public ResponseEntity<InvoiceInformation> createInvoice(@RequestBody @Valid InvoiceCommand command, UriComponentsBuilder uriBuilder) {
        Long id = invoicesService.createInvoice(command);
        var location = uriBuilder.path("/invoices/{id}").buildAndExpand(id).toUri();
        InvoiceInformation info = invoicesService.getInvoiceById(id);
        return ResponseEntity.created(location).body(info);
    }

    @DeleteMapping("/{invoice_id}")
    public ResponseEntity<Void> deleteInvoice(@PathVariable Long invoice_id) {
        invoicesService.deleteInvoice(invoice_id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{invoice_id}/pay")
    public InvoiceInformation payInvoice(@PathVariable Long invoice_id, @RequestBody @Valid PaymentCommand paymentCommand) {
        return invoicesService.payInvoice(invoice_id, paymentCommand);
    }
}
