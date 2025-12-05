package org.squad.careerhub.domain.jobposting.service.dto;

import java.util.List;
import lombok.Builder;
import org.squad.careerhub.domain.jobposting.enums.JobPostingExtractStatus;

@Builder
public record JobPostingSnapshotResponse(
    String company,
    String position,
    String deadline,
    List<String> recruitmentProcess,
    List<String> mainTasks,
    List<String> requiredQualifications,
    List<String> preferredQualifications,
    JobPostingExtractStatus status
) {

    public static JobPostingSnapshotResponse of(
        String company,
        String position,
        String deadline,
        List<String> recruitmentProcess,
        List<String> mainTasks,
        List<String> requiredQualifications,
        List<String> preferredQualifications,
        JobPostingExtractStatus status
    ) {
        return JobPostingSnapshotResponse.builder()
            .company(company)
            .position(position)
            .deadline(deadline)
            .recruitmentProcess(recruitmentProcess)
            .mainTasks(mainTasks)
            .requiredQualifications(requiredQualifications)
            .preferredQualifications(preferredQualifications)
            .status(status)
            .build();
    }
}