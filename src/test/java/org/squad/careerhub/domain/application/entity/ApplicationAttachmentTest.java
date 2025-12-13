package org.squad.careerhub.domain.application.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;

class ApplicationAttachmentTest {

    @Test
    void create로_ApplicationAttachment를_생성한다() {
        // given
        Application application = mock(Application.class);
        String fileUrl = "https://example.com/file.pdf";
        String fileName = "resume.pdf";
        String fileType = "application/pdf";

        // when
        ApplicationAttachment attachment = ApplicationAttachment.create(
                application,
                fileUrl,
                fileName,
                fileType
        );

        // then
        assertThat(attachment).isNotNull();
        assertThat(attachment.getApplication()).isEqualTo(application);
        assertThat(attachment.getFileUrl()).isEqualTo(fileUrl);
        assertThat(attachment.getFileName()).isEqualTo(fileName);
        assertThat(attachment.getFileType()).isEqualTo(fileType);
    }

    @Test
    void application이_null이면_예외가_발생한다() {
        // when & then
        assertThatThrownBy(() ->
                ApplicationAttachment.create(
                        null,
                        "https://example.com/file.pdf",
                        "resume.pdf",
                        "application/pdf"
                )
        ).isInstanceOf(NullPointerException.class);
    }

    @Test
    void fileUrl이_null이면_예외가_발생한다() {
        // given
        Application application = mock(Application.class);

        // when & then
        assertThatThrownBy(() ->
                ApplicationAttachment.create(
                        application,
                        null,
                        "resume.pdf",
                        "application/pdf"
                )
        ).isInstanceOf(NullPointerException.class);
    }

    @Test
    void fileName이_null이면_예외가_발생한다() {
        // given
        Application application = mock(Application.class);

        // when & then
        assertThatThrownBy(() ->
                ApplicationAttachment.create(
                        application,
                        "https://example.com/file.pdf",
                        null,
                        "application/pdf"
                )
        ).isInstanceOf(NullPointerException.class);
    }

    @Test
    void fileType이_null이면_예외가_발생한다() {
        // given
        Application application = mock(Application.class);

        // when & then
        assertThatThrownBy(() ->
                ApplicationAttachment.create(
                        application,
                        "https://example.com/file.pdf",
                        "resume.pdf",
                        null
                )
        ).isInstanceOf(NullPointerException.class);
    }

    @Test
    void 이미지_파일_타입으로_첨부파일을_생성한다() {
        // given
        Application application = mock(Application.class);
        String fileUrl = "https://s3.amazonaws.com/bucket/image.png";
        String fileName = "portfolio_screenshot.png";
        String fileType = "image/png";

        // when
        ApplicationAttachment attachment = ApplicationAttachment.create(
                application,
                fileUrl,
                fileName,
                fileType
        );

        // then
        assertThat(attachment.getFileType()).isEqualTo(fileType);
        assertThat(attachment.getFileName()).endsWith(".png");
    }

    @Test
    void PDF_파일_타입으로_첨부파일을_생성한다() {
        // given
        Application application = mock(Application.class);
        String fileUrl = "https://storage.com/documents/cv.pdf";
        String fileName = "curriculum_vitae.pdf";
        String fileType = "application/pdf";

        // when
        ApplicationAttachment attachment = ApplicationAttachment.create(
                application,
                fileUrl,
                fileName,
                fileType
        );

        // then
        assertThat(attachment.getFileType()).isEqualTo(fileType);
        assertThat(attachment.getFileName()).endsWith(".pdf");
    }

    @Test
    void 긴_파일_이름으로_첨부파일을_생성한다() {
        // given
        Application application = mock(Application.class);
        String longFileName = "매우_긴_파일_이름_테스트_202501_개인정보_포함_이력서_최종_수정본_v3.pdf";

        // when
        ApplicationAttachment attachment = ApplicationAttachment.create(
                application,
                "https://example.com/file.pdf",
                longFileName,
                "application/pdf"
        );

        // then
        assertThat(attachment.getFileName()).isEqualTo(longFileName);
    }

    @Test
    void 특수문자가_포함된_파일_이름으로_첨부파일을_생성한다() {
        // given
        Application application = mock(Application.class);
        String fileName = "파일@#$%명.pdf";

        // when
        ApplicationAttachment attachment = ApplicationAttachment.create(
                application,
                "https://example.com/file.pdf",
                fileName,
                "application/pdf"
        );

        // then
        assertThat(attachment.getFileName()).isEqualTo(fileName);
    }

}