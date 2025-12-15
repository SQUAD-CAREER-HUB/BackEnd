package org.squad.careerhub.domain.application.service.dto;

import java.util.List;
import lombok.Builder;
import org.squad.careerhub.domain.application.entity.StageResult;
import org.squad.careerhub.domain.application.entity.StageType;
import org.squad.careerhub.domain.application.entity.SubmissionStatus;

@Builder
public record SearchCondition(
        String query,
        List<StageType> stageTypes,
        SubmissionStatus submissionStatus,
        StageResult stageResult
) {

}