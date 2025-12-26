package org.squad.careerhub.domain.application.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.squad.careerhub.domain.application.entity.Application;
import org.squad.careerhub.domain.application.repository.dto.BeforeDeadlineApplicationResponse;
import org.squad.careerhub.domain.application.service.dto.NewApplicationInfo;
import org.squad.careerhub.domain.application.service.dto.NewJobPosting;
import org.squad.careerhub.domain.application.service.dto.NewStage;
import org.squad.careerhub.domain.application.repository.dto.BeforeDeadlineApplicationResponse;
import org.squad.careerhub.domain.application.service.dto.SearchCondition;
import org.squad.careerhub.domain.application.service.dto.response.ApplicationStatisticsResponse;
import org.squad.careerhub.domain.application.service.dto.response.ApplicationSummaryResponse;
import org.squad.careerhub.domain.schedule.service.ScheduleManager;
import org.squad.careerhub.global.support.Cursor;
import org.squad.careerhub.global.support.PageResponse;

@RequiredArgsConstructor
@Service
public class ApplicationService {

    private final ApplicationManager applicationManager;
    private final ApplicationReader applicationReader;
    private final ApplicationPolicyValidator applicationPolicyValidator;
    private final ApplicationFileManager applicationFileManager;
    private final ScheduleManager scheduleManager;
    private final ApplicationStageManager applicationStageManager;

    /**
     * 지원서를 생성합니다.
     * @param newJobPosting      새로운 채용 공고 정보
     * @param newApplicationInfo 새로운 지원서 정보
     * @param newStage           새로운 전형 정보
     * @param files              첨부 파일 목록
     * @param authorId           작성자 ID
     */
    @Transactional
    public Long createApplication(
            NewJobPosting newJobPosting,
            NewApplicationInfo newApplicationInfo,
            NewStage newStage,
            List<MultipartFile> files,
            Long authorId
    ) {
        applicationPolicyValidator.validateNewStage(newStage);

        Application application = applicationManager.create(
                newJobPosting,
                newApplicationInfo,
                newStage,
                files,
                authorId
        );

        applicationStageManager.createWithSchedule(application, newStage);

        return application.getId();
    }

    public PageResponse<ApplicationSummaryResponse> findApplications(
            SearchCondition searchCondition,
            Cursor cursor,
            Long memberId
    ) {
       return applicationReader.findApplications(searchCondition, cursor, memberId);
    }

    public ApplicationStatisticsResponse getApplicationStatic(Long authorId) {
        return applicationReader.getApplicationStatistics(authorId);
    }

    public PageResponse<BeforeDeadlineApplicationResponse> findBeforeDeadlineApplications(Long memberId, Cursor cursor) {
        return applicationReader.findBeforeDeadlineApplications(memberId, cursor);
    }

}