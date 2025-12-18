package cat.tecnocampus.veterinarymanagement.application.mappers;

import cat.tecnocampus.veterinarymanagement.application.outputDTO.VisitRescheduleInformation;
import cat.tecnocampus.veterinarymanagement.domain.VisitHistory;

import java.util.List;

public class VisitRescheduleMapper {

    public static VisitRescheduleInformation toVisitRescheduleInformation(VisitHistory h) {
        if (h == null) return null;

        return new VisitRescheduleInformation(
                h.getId(),
                h.getVisitId(),
                h.getOldDate() != null ? h.getOldDate().toString() : null,
                h.getOldTime() != null ? h.getOldTime().toString() : null,
                h.getNewDate() != null ? h.getNewDate().toString() : null,
                h.getNewTime() != null ? h.getNewTime().toString() : null,
                h.getAction(),
                h.getPerformedBy(),
                h.getCreatedAt() != null ? h.getCreatedAt().toString() : null
        );
    }

    public static List<VisitRescheduleInformation> toVisitRescheduleInformationList(List<VisitHistory> historyList) {
        if (historyList == null) return List.of();
        return historyList.stream()
                .map(VisitRescheduleMapper::toVisitRescheduleInformation)
                .toList();
    }
}

