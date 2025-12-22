package org.squad.careerhub.domain.application.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.squad.careerhub.domain.application.entity.Application;
import org.squad.careerhub.domain.application.entity.ApplicationAttachment;
import org.squad.careerhub.domain.application.repository.ApplicationAttachmentJpaRepository;
import org.squad.careerhub.domain.application.service.dto.response.FileResponse;

@RequiredArgsConstructor
@Component
public class ApplicationFileManager {

    private final ApplicationAttachmentJpaRepository applicationAttachmentJpaRepository;
    private final FileProvider fileProvider;

    public void addApplicationFile(Application application, List<MultipartFile> multipartFiles) {
        if (multipartFiles == null || multipartFiles.isEmpty()) {
            return;
        }

        List<FileResponse> fileResponses = fileProvider.uploadFiles(multipartFiles);

        List<ApplicationAttachment> attachments = fileResponses.stream()
                .map(fileResponse -> ApplicationAttachment.create(
                        application,
                        fileResponse.url(),
                        fileResponse.fileName(),
                        fileResponse.fileType())
                ).toList();

        // 추후 성능 문제 시 저장 방법 변경
        applicationAttachmentJpaRepository.saveAll(attachments);
    }

}