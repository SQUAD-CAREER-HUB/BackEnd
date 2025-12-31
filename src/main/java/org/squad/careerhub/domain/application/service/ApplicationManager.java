package org.squad.careerhub.domain.application.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.squad.careerhub.domain.application.entity.Application;
import org.squad.careerhub.domain.application.repository.ApplicationJpaRepository;
import org.squad.careerhub.domain.application.service.dto.NewApplication;
import org.squad.careerhub.domain.application.service.dto.UpdateApplication;
import org.squad.careerhub.domain.member.entity.Member;
import org.squad.careerhub.domain.member.service.MemberReader;
import org.squad.careerhub.global.error.CareerHubException;
import org.squad.careerhub.global.error.ErrorStatus;

@RequiredArgsConstructor
@Component
public class ApplicationManager {

    private final MemberReader memberReader;
    private final ApplicationFileManager applicationFileManager;
    private final ApplicationJpaRepository applicationJpaRepository;

    /**
     * 지원서와 첨부 파일을 생성합니다.
     **/
    @Transactional
    public Application create(
            NewApplication newApplication,
            List<MultipartFile> files,
            Long authorId
    ) {
        Member author = memberReader.find(authorId);
        Application application = applicationJpaRepository.save(Application.create(
                author,
                newApplication.jobPostingUrl(),
                newApplication.company(),
                newApplication.position(),
                newApplication.jobLocation(),
                newApplication.stageType(),
                newApplication.finalApplicationStatus(),
                newApplication.applicationMethod(),
                newApplication.deadline()
        ));

        applicationFileManager.addApplicationFile(application, files);

        return application;
    }

    @Transactional
    public Application updateApplication(UpdateApplication updateApplication, Long memberId) {
        Application application = applicationJpaRepository.findByIdAndAuthorId(updateApplication.applicationId(), memberId)
                .orElseThrow(() -> new CareerHubException(ErrorStatus.FORBIDDEN_MODIFY));

        application.update(
                updateApplication.jobPostingUrl(),
                updateApplication.company(),
                updateApplication.position(),
                updateApplication.jobLocation(),
                updateApplication.memo()
        );

        return application;
    }

}