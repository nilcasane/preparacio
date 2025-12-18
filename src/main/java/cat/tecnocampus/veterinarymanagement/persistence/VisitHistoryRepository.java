package cat.tecnocampus.veterinarymanagement.persistence;

import cat.tecnocampus.veterinarymanagement.domain.VisitHistory;
import org.springframework.data.repository.CrudRepository;

public interface VisitHistoryRepository extends CrudRepository<VisitHistory, Long> {
}

