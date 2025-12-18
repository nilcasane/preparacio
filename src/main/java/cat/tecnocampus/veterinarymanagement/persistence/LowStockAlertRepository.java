package cat.tecnocampus.veterinarymanagement.persistence;

import cat.tecnocampus.veterinarymanagement.domain.LowStockAlert;
import cat.tecnocampus.veterinarymanagement.domain.Medication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LowStockAlertRepository extends JpaRepository<LowStockAlert, Long> {
    List<LowStockAlert> findByMedication(Medication med);
}
