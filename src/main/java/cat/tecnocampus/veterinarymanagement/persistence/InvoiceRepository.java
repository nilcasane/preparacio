package cat.tecnocampus.veterinarymanagement.persistence;

import cat.tecnocampus.veterinarymanagement.domain.Invoice;
import cat.tecnocampus.veterinarymanagement.domain.InvoiceStatus;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    @Query("""
      SELECT i FROM Invoice i WHERE
        (:startDate IS NULL OR i.invoiceDate >= :startDate)
        AND (:endDate IS NULL OR i.invoiceDate <= :endDate)
        AND (:status IS NULL OR i.status = :status)
        AND (:petOwnerId IS NULL OR i.petOwner.id = :petOwnerId)
        AND (:petId IS NULL OR i.visit.pet.id = :petId)
      """)
    List<Invoice> findInvoicesByFilters(LocalDate startDate, LocalDate endDate, InvoiceStatus status, Long petOwnerId, Long petId);
}
