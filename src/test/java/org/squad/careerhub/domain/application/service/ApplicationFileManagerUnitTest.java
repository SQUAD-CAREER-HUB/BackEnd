package org.squad.careerhub.domain.application.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.web.multipart.MultipartFile;
import org.squad.careerhub.TestDoubleSupport;
import org.squad.careerhub.domain.application.entity.Application;
import org.squad.careerhub.domain.application.repository.ApplicationAttachmentJpaRepository;
import org.squad.careerhub.domain.application.service.dto.response.FileResponse;

class ApplicationFileManagerUnitTest extends TestDoubleSupport {

    @Mock
    FileProvider fileProvider;

    @Mock
    ApplicationAttachmentJpaRepository applicationAttachmentJpaRepository;

    @InjectMocks
    ApplicationFileManager applicationFileManager;

    @Test
    void 지원서_파일을_추가한다() {
        // given
        var application = mock(Application.class);

        var fileInfo = new FileResponse("http://example.com/file1.png", "file1.png", "image/png");
        given(fileProvider.uploadFiles(any())).willReturn(List.of(fileInfo));

        var multipartFile = mock(MultipartFile.class);

        // when
        applicationFileManager.addApplicationFile(application, List.of(multipartFile));

        // then
        verify(applicationAttachmentJpaRepository, times(1)).saveAll(any());
    }

    @Test
    void 첨부_파일이_없을경우_아무_일이_일어나지_않는다() {
        // given
        var application = mock(Application.class);

        // when
        applicationFileManager.addApplicationFile(application, List.of());

        // then
        verify(applicationAttachmentJpaRepository, never()).saveAll(any());
    }

}