package cat.tecnocampus.veterinarymanagement.application.mappers;

import cat.tecnocampus.veterinarymanagement.application.outputDTO.VisitInformation;
import cat.tecnocampus.veterinarymanagement.domain.Visit;

public class VisitMapper {
    public static VisitInformation toVisitInformation(Visit visit) {
        return new VisitInformation(
                visit.getId(),
                visit.getVisitDate() != null ? visit.getVisitDate().toString() : null,
                visit.getVisitTime() != null ? visit.getVisitTime().toString() : null,
                visit.getDuration(),
                visit.getReasonForVisit(),
                visit.getPricerPerFifteen(),
                visit.getStatus(),
                visit.getVeterinarian() != null ? visit.getVeterinarian().getId() : null,
                visit.getPet() != null ? visit.getPet().getId() : null,
                visit.getPetOwner() != null ? visit.getPetOwner().getId() : null,
                TreatmentMapper.toTreatmentInformation(visit.getTreatment())
        );
    }
}
