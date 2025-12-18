package cat.tecnocampus.veterinarymanagement.api;

import cat.tecnocampus.veterinarymanagement.application.exceptions.*;
import cat.tecnocampus.veterinarymanagement.domain.exceptions.VisitInvalidStateException;
import cat.tecnocampus.veterinarymanagement.domain.exceptions.VisitStatusInvalidException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.Instant;

@ControllerAdvice
public class ApplicationLevelExceptionsHandler {

    @ExceptionHandler(VeterinarianDoesNotExistException.class)
    @ResponseBody
    public ProblemDetail handleVeterinarianDoesNotExistException(VeterinarianDoesNotExistException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        pd.setTitle("Veterinarian Not Found");
        pd.setProperty("timestamp", Instant.now());
        return pd;
    }

    @ExceptionHandler(AvailabilityDoesNotExistException.class)
    @ResponseBody
    public ProblemDetail handleAvailabilityDoesNotExistException(AvailabilityDoesNotExistException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        pd.setTitle("Availability Not Found");
        pd.setProperty("timestamp", Instant.now());
        return pd;
    }

    @ExceptionHandler(ExceptionDoesNotExistException.class)
    @ResponseBody
    public ProblemDetail handleExceptionDoesNotExistException(ExceptionDoesNotExistException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        pd.setTitle("Exception Not Found");
        pd.setProperty("timestamp", Instant.now());
        return pd;
    }

    @ExceptionHandler(PromotionDoesNotExistException.class)
    @ResponseBody
    public ProblemDetail handlePromotionDoesNotExistException(PromotionDoesNotExistException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        pd.setTitle("Promotion Not Found");
        pd.setProperty("timestamp", Instant.now());
        return pd;
    }

    @ExceptionHandler(DiscountDoesNotExistException.class)
    @ResponseBody
    public ProblemDetail handleDiscountDoesNotExistException(DiscountDoesNotExistException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        pd.setTitle("Discount Not Found");
        pd.setProperty("timestamp", Instant.now());
        return pd;
    }

    @ExceptionHandler(MedicationDoesNotExistException.class)
    @ResponseBody
    public ProblemDetail handleMedicationDoesNotExistException(MedicationDoesNotExistException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        pd.setTitle("Medication Not Found");
        pd.setProperty("timestamp", Instant.now());
        return pd;
    }

    @ExceptionHandler(MedicationBatchDoesNotExistException.class)
    @ResponseBody
    public ProblemDetail handleMedicationBatchDoesNotExistException(MedicationBatchDoesNotExistException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        pd.setTitle("Medication batch Not Found");
        pd.setProperty("timestamp", Instant.now());
        return pd;
    }

    @ExceptionHandler(LoyaltyTierDoesNotExistException.class)
    @ResponseBody
    public ProblemDetail handleLoyaltyTierDoesNotExistException(LoyaltyTierDoesNotExistException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        pd.setTitle("Loyalty tier Not Found");
        pd.setProperty("timestamp", Instant.now());
        return pd;
    }

    @ExceptionHandler(LowStockAlertDoesNotExistException.class)
    @ResponseBody
    public ProblemDetail handleLowStockAlertDoesNotExistException(LowStockAlertDoesNotExistException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        pd.setTitle("Alert not found");
        pd.setProperty("timestamp", Instant.now());
        return pd;
    }

    @ExceptionHandler(LowStockAlertDoesAlreadyExistException.class)
    @ResponseBody
    public ProblemDetail LowStockAlertDoesAlreadyExistException(LowStockAlertDoesAlreadyExistException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        pd.setTitle("Alert already exists");
        pd.setProperty("timestamp", Instant.now());
        return pd;
    }

    @ExceptionHandler(VisitDoesNotExistException.class)
    @ResponseBody
    public ProblemDetail handleVisitDoesNotExistException(VisitDoesNotExistException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        pd.setTitle("Visit Not Found");
        pd.setProperty("timestamp", Instant.now());
        return pd;
    }

    @ExceptionHandler(VisitSlotUnavailableException.class)
    @ResponseBody
    public ProblemDetail handleVisitSlotUnavailableException(VisitSlotUnavailableException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        pd.setTitle("Visit slot unavailable");
        pd.setProperty("timestamp", Instant.now());
        return pd;
    }

    @ExceptionHandler(MedicationPrescriptionDoesNotExistException.class)
    @ResponseBody
    public ProblemDetail MedicationPrescriptionDoesNotExistException(MedicationPrescriptionDoesNotExistException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        pd.setTitle("Medication prescription Not Found");
        pd.setProperty("timestamp", Instant.now());
        return pd;
    }

    @ExceptionHandler(StockNotAvailableException.class)
    @ResponseBody
    public ProblemDetail handleStockNotAvailableException(StockNotAvailableException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        pd.setTitle("Insufficient stock");
        pd.setProperty("timestamp", Instant.now());
        return pd;
    }

    @ExceptionHandler(PetDoesNotExistException.class)
    @ResponseBody
    public ProblemDetail handlePetDoesNotExistException(PetDoesNotExistException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        pd.setTitle("Pet Not Found");
        pd.setProperty("timestamp", Instant.now());
        return pd;
    }

    @ExceptionHandler(MedicationIncompatibilityExistsException.class)
    @ResponseBody
    public ProblemDetail handleMedicationIncompatibilityExistsException(MedicationIncompatibilityExistsException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        pd.setTitle("Medication incompatibility");
        pd.setProperty("timestamp", Instant.now());
        return pd;
    }

    @ExceptionHandler(TreatmentDoesNotExistException.class)
    @ResponseBody
    public ProblemDetail handleTreatmentDoesNotExistException(TreatmentDoesNotExistException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        pd.setTitle("Treatment Not Found");
        pd.setProperty("timestamp", Instant.now());
        return pd;
    }

    @ExceptionHandler(NotEnoughMedicationInBatchesException.class)
    @ResponseBody
    public ProblemDetail handleNotEnoughMedicationInBatches(NotEnoughMedicationInBatchesException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        pd.setTitle("Not enough medication in batches");
        pd.setProperty("timestamp", Instant.now());
        return pd;
    }

    @ExceptionHandler(InvoiceDoesNotExistException.class)
    @ResponseBody
    public ProblemDetail handleInvoiceDoesNotExistException(InvoiceDoesNotExistException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        pd.setTitle("Invoice Not Found");
        pd.setProperty("timestamp", Instant.now());
        return pd;
    }

    @ExceptionHandler(InvoiceAlreadyExistsException.class)
    @ResponseBody
    public ProblemDetail handleInvoiceAlreadyExistsException(InvoiceAlreadyExistsException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        pd.setTitle("Invoice Already Exists");
        pd.setProperty("timestamp", Instant.now());
        return pd;
    }

}
