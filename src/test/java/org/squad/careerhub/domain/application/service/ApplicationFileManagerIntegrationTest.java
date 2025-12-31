package org.squad.careerhub.domain.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.multipart.MultipartFile;
import org.squad.careerhub.IntegrationTestSupport;
import org.squad.careerhub.domain.application.entity.Application;
import org.squad.careerhub.domain.application.entity.ApplicationAttachment;
import org.squad.careerhub.domain.application.entity.ApplicationMethod;
import org.squad.careerhub.domain.application.entity.ApplicationStatus;
import org.squad.careerhub.domain.application.entity.StageType;
import org.squad.careerhub.domain.application.repository.ApplicationAttachmentJpaRepository;
import org.squad.careerhub.domain.application.repository.ApplicationJpaRepository;
import org.squad.careerhub.domain.application.service.dto.response.FileResponse;
import org.squad.careerhub.domain.member.entity.Member;
import org.squad.careerhub.domain.member.entity.SocialProvider;
import org.squad.careerhub.domain.member.repository.MemberJpaRepository;
import org.squad.careerhub.global.entity.EntityStatus;

@RequiredArgsConstructor
@Transactional
class ApplicationFileManagerIntegrationTest extends IntegrationTestSupport {

    final ApplicationFileManager applicationFileManager;
    final ApplicationAttachmentJpaRepository attachmentRepository;
    final ApplicationJpaRepository applicationRepository;
    final MemberJpaRepository memberJpaRepository;

    @MockitoBean
    private FileProvider fileProvider;  // S3는 Mock 처리

    @Test
    void 파일_업데이트_시_기존_파일_삭제_후_새_파일_업로드() {
        // given
        var member = createMember();
        var application = createApplication(member);

        // 기존 파일 2개 저장
        var attachment1 = attachmentRepository.save(ApplicationAttachment.create(application, "old-url-1", "old1.pdf", "PDF"));
        var attachment2 = attachmentRepository.save(ApplicationAttachment.create(application, "old-url-2", "old2.pdf", "PDF"));

        // 새 파일 준비
        var file1 = new MockMultipartFile("files", "new1.pdf", "application/pdf", "content1".getBytes());
        var file2 = new MockMultipartFile("files", "new2.pdf", "application/pdf", "content2".getBytes());
        List<MultipartFile> newFiles = List.of(file1, file2);

        // S3 Mock 설정
        when(fileProvider.uploadFiles(newFiles))
                .thenReturn(List.of(
                        new FileResponse("new-url-1", "new1.pdf", "PDF"),
                        new FileResponse("new-url-2", "new2.pdf", "PDF")
                ));

        // when
        applicationFileManager.updateApplicationFile(application, newFiles);

        // then
        // 1. 기존 파일 S3 삭제 호출 검증
        verify(fileProvider).deleteFiles(List.of("old-url-1", "old-url-2"));

        // 2. 새 파일 S3 업로드 호출 검증
        verify(fileProvider).uploadFiles(newFiles);

        // 3. DB에 기존 파일은 삭제되고 새 파일만 존재
        List<ApplicationAttachment> attachments = attachmentRepository.findAllByApplicationIdAndStatus(
                application.getId(),
                EntityStatus.ACTIVE
        );

        assertThat(attachments).hasSize(2);
        assertThat(attachments)
                .extracting("fileUrl")
                .containsExactlyInAnyOrder("new-url-1", "new-url-2");
        assertThat(attachments)
                .extracting("fileName")
                .containsExactlyInAnyOrder("new1.pdf", "new2.pdf");

        // 4. 기존 파일 삭제 확인 (soft delete인 경우)
        assertThat(attachment1.isDeleted()).isTrue();
        assertThat(attachment2.isDeleted()).isTrue();
    }

    @Test
    void 파일이_null_이거나_비어있으면_아무_작업_안함() {
        // given
        var member = createMember();
        var application = createApplication(member);

        // when
        applicationFileManager.updateApplicationFile(application, null);

        // then
        verify(fileProvider, never()).uploadFiles(any());
        verify(fileProvider, never()).deleteFiles(any());
    }

    @Test
    void 기존_파일이_없어도_새_파일은_정상_업로드() {
        // given
        var member = createMember();
        var application = createApplication(member);

        var file = new MockMultipartFile(
                "files", "new.pdf", "application/pdf", "content".getBytes()
        );

        when(fileProvider.uploadFiles(List.of(file)))
                .thenReturn(List.of(new FileResponse("new-url", "new.pdf", "PDF")));

        // when
        applicationFileManager.updateApplicationFile(application, List.of(file));

        // then
        verify(fileProvider, never()).deleteFiles(any());  // 삭제 호출 없음
        verify(fileProvider).uploadFiles(List.of(file));

        List<ApplicationAttachment> attachments = attachmentRepository.findAllByApplicationIdAndStatus(
                application.getId(),
                EntityStatus.ACTIVE
        );
        assertThat(attachments).hasSize(1);
    }

    private Member createMember() {
        return memberJpaRepository.save(Member.create(
                "email",
                SocialProvider.KAKAO,
                "socialId",
                "nickname",
                "profileImageUrl"
        ));
    }

    private Application createApplication(Member member) {
        return applicationRepository.save(Application.create(
                member,
                "https://naver.com",
                "네이버",
                "백엔드",
                "서울",
                StageType.INTERVIEW,
                ApplicationStatus.IN_PROGRESS,
                ApplicationMethod.EMAIL,
                LocalDateTime.now()
        ));
    }

}