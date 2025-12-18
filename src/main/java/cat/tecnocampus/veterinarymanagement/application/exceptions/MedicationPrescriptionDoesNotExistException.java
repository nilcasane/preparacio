package cat.tecnocampus.veterinarymanagement.application.exceptions;

public class MedicationPrescriptionDoesNotExistException extends RuntimeException {
    public MedicationPrescriptionDoesNotExistException(String message) {
        super(message);
    }
}
