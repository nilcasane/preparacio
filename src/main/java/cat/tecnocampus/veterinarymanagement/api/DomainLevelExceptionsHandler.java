package cat.tecnocampus.veterinarymanagement.api;

import cat.tecnocampus.veterinarymanagement.domain.exceptions.VisitInvalidStateException;
import cat.tecnocampus.veterinarymanagement.domain.exceptions.VisitNotCompletedException;
import cat.tecnocampus.veterinarymanagement.domain.exceptions.VisitStatusInvalidException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import cat.tecnocampus.veterinarymanagement.domain.exceptions.InvoiceAlreadyPaidException;

import java.time.Instant;

@ControllerAdvice
public class DomainLevelExceptionsHandler {

    @ExceptionHandler(VisitNotCompletedException.class)
    @ResponseBody
    public ProblemDetail handleVisitNotCompletedException(VisitNotCompletedException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        pd.setTitle("Visit Not Completed");
        pd.setProperty("timestamp", Instant.now());
        return pd;
    }

    @ExceptionHandler(VisitStatusInvalidException.class)
    @ResponseBody
    public ProblemDetail handleVisitStatusInvalidException(VisitStatusInvalidException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        pd.setTitle("Invalid Visit Status");
        pd.setProperty("timestamp", Instant.now());
        return pd;
    }

    @ExceptionHandler(VisitInvalidStateException.class)
    @ResponseBody
    public ProblemDetail handleVisitInvalidStateException(VisitInvalidStateException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        pd.setTitle("Invalid visit state");
        pd.setProperty("timestamp", Instant.now());
        return pd;
    }

    @ExceptionHandler(InvoiceAlreadyPaidException.class)
    @ResponseBody
    public ProblemDetail handleInvoiceAlreadyPaidException(InvoiceAlreadyPaidException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        pd.setTitle("Invoice Already Paid");
        pd.setProperty("timestamp", Instant.now());
        return pd;
    }

}
