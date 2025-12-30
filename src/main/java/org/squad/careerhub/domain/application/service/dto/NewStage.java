package org.squad.careerhub.domain.application.service.dto;

import java.util.List;
import lombok.Builder;
import org.squad.careerhub.domain.application.entity.StageType;
import org.squad.careerhub.domain.schedule.service.dto.NewDocsSchedule;
import org.squad.careerhub.domain.schedule.service.dto.NewEtcSchedule;
import org.squad.careerhub.domain.schedule.service.dto.NewInterviewSchedule;

@Builder
public record NewStage(
        StageType stageType,
        NewDocsSchedule newDocsSchedule,
        List<NewEtcSchedule> newEtcSchedules,
        List<NewInterviewSchedule> newInterviewSchedules
) {

}