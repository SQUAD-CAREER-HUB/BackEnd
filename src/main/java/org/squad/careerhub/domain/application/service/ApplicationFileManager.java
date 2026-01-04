package org.squad.careerhub.domain.application.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.squad.careerhub.domain.application.entity.Application;
import org.squad.careerhub.domain.application.entity.ApplicationAttachment;
import org.squad.careerhub.domain.application.repository.ApplicationAttachmentJpaRepository;
import org.squad.careerhub.domain.application.service.dto.response.FileResponse;
import org.squad.careerhub.global.entity.EntityStatus;

@RequiredArgsConstructor
@Slf4j
@Component
public class ApplicationFileManager {

    private final ApplicationAttachmentJpaRepository applicationAttachmentJpaRepository;
    private final FileProvider fileProvider;

    public void addApplicationFile(Application application, List<MultipartFile> multipartFiles) {
        if (multipartFiles == null || multipartFiles.isEmpty()) {
            return;
        }

        List<ApplicationAttachment> newAttachments = uploadS3AndCreateAttachments(application, multipartFiles);
        applicationAttachmentJpaRepository.saveAll(newAttachments);// 추후 성능 문제 시 저장 방법 변경
    }

    @Transactional
    public void updateApplicationFile(Application application, List<MultipartFile> multipartFiles) {
        if (multipartFiles == null || multipartFiles.isEmpty()) {
            return;
        }

        List<ApplicationAttachment> newAttachments = uploadS3AndCreateAttachments(application, multipartFiles);
        applicationAttachmentJpaRepository.saveAll(newAttachments); // 추후 성능 문제 시 저장 방법 변경

        // 새 파일 업로드 후 기존 파일 삭제
        deleteExistingFiles(application.getId(), newAttachments);

        log.debug("[ApplicationFileManager] 파일 업데이트 완료 - applicationId: {}", application.getId());
    }

    private List<ApplicationAttachment> uploadS3AndCreateAttachments(
            Application application,
            List<MultipartFile> files
    ) {
        List<FileResponse> fileResponses = fileProvider.uploadFiles(files);

        return fileResponses.stream()
                .map(response -> ApplicationAttachment.create(
                        application,
                        response.url(),
                        response.fileName(),
                        response.fileType()
                ))
                .toList();
    }

    private void deleteExistingFiles(Long applicationId, List<ApplicationAttachment> newAttachments) {
        List<ApplicationAttachment> existingAttachments = applicationAttachmentJpaRepository.findAllByApplicationIdAndStatus(
                applicationId,
                EntityStatus.ACTIVE
        );

        if (existingAttachments.isEmpty()) {
            return;
        }

        Set<String> newFileUrls = newAttachments.stream()
                .map(ApplicationAttachment::getFileUrl)
                .collect(Collectors.toSet());

        // 기존 파일 중 새 파일 목록에 없는 것만 필터링
        List<ApplicationAttachment> filesToDelete = existingAttachments.stream()
                .filter(existing -> !newFileUrls.contains(existing.getFileUrl()))
                .toList();

        if (filesToDelete.isEmpty()) {
            return;
        }

        List<String> fileUrlsToDelete = filesToDelete.stream()
                .map(ApplicationAttachment::getFileUrl)
                .toList();

        // FIXME: 에러 발생 시 데이터 정합성 문제가 발생함. 해결해야됨.
        fileProvider.deleteFiles(fileUrlsToDelete);
        filesToDelete.forEach(ApplicationAttachment::delete);
    }

}