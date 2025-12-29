package org.squad.careerhub.domain.application.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.squad.careerhub.domain.application.entity.Application;
import org.squad.careerhub.domain.application.repository.dto.BeforeDeadlineApplicationResponse;
import org.squad.careerhub.domain.application.service.dto.NewApplication;
import org.squad.careerhub.domain.application.service.dto.NewStage;
import org.squad.careerhub.domain.application.service.dto.SearchCondition;
import org.squad.careerhub.domain.application.service.dto.UpdateApplication;
import org.squad.careerhub.domain.application.service.dto.response.ApplicationDetailPageResponse;
import org.squad.careerhub.domain.application.service.dto.response.ApplicationStatisticsResponse;
import org.squad.careerhub.domain.application.service.dto.response.ApplicationSummaryResponse;
import org.squad.careerhub.global.support.Cursor;
import org.squad.careerhub.global.support.PageResponse;

@Slf4j
@RequiredArgsConstructor
@Service
public class ApplicationService {

    private final ApplicationManager applicationManager;
    private final ApplicationFileManager applicationFileManager;
    private final ApplicationReader applicationReader;
    private final ApplicationPolicyValidator applicationPolicyValidator;
    private final ApplicationStageManager applicationStageManager;

    /**
     * 지원서를 생성합니다.
     *
     * @param newApplication     새로운 지원서 정보
     * @param newStage           새로운 전형 정보
     * @param files              첨부 파일 목록
     * @param authorId           작성자 ID
     */

    @Transactional
    public Long createApplication(
            NewApplication newApplication,
            NewStage newStage,
            List<MultipartFile> files,
            Long authorId
    ) {
        applicationPolicyValidator.validateNewStage(newStage, newApplication.finalApplicationStatus());

        Application application = applicationManager.create(newApplication, authorId);
        applicationFileManager.addApplicationFile(application, files);

        applicationStageManager.createWithSchedule(application, newStage);

        log.info("[Application] 지원서 생성 완료 - applicationId: {}, company: {}", application.getId(), newApplication.company());

        return application.getId();
    }

    public PageResponse<ApplicationSummaryResponse> findApplications(
            SearchCondition searchCondition,
            Cursor cursor,
            Long memberId
    ) {
        log.debug("[Application] 지원서 목록 조회 - memberId: {}, searchCondition: {}", memberId, searchCondition);

        return applicationReader.findApplications(searchCondition, cursor, memberId);
    }

    public ApplicationDetailPageResponse findApplication(Long applicationId, Long memberId) {
        log.debug("[Application] 지원서 상세 조회 - applicationId: {}, memberId: {}", applicationId, memberId);

        return applicationReader.findApplication(applicationId, memberId);
    }

    public ApplicationStatisticsResponse getApplicationStatistics(Long authorId) {
        return applicationReader.getApplicationStatistics(authorId);
    }

    public PageResponse<BeforeDeadlineApplicationResponse> findBeforeDeadlineApplications(Long memberId, Cursor cursor) {
        log.debug("[Application] 마감 임박 지원서 조회 - memberId: {}", memberId);

        return applicationReader.findBeforeDeadlineApplications(memberId, cursor);
    }

    public void updateApplication(UpdateApplication updateApplication, List<MultipartFile> files, Long memberId) {
        Application application = applicationManager.updateApplication(updateApplication, memberId);
        applicationFileManager.updateApplicationFile(application, files);

        log.info("[Application] 지원서 수정 완료 - applicationId: {}", updateApplication.applicationId());
    }

}