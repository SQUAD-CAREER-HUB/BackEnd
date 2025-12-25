package org.squad.careerhub.infrastructure.s3;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.squad.careerhub.global.error.CareerHubException;
import org.squad.careerhub.global.error.ErrorStatus;

class FileValidatorTest {

    private final FileValidator fileValidator = new FileValidator();

    @Test
    void jpg_이미지_파일_업로드_성공() {
        // given
        byte[] content = new byte[5 * 1024 * 1024]; // 5MB
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                content
        );

        // when & then
        assertThatNoException()
                .isThrownBy(() -> fileValidator.validateImageFile(file));
    }

    @Test
    void jpeg_이미지_파일_업로드_성공() {
        // given
        byte[] content = new byte[5 * 1024 * 1024];
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.jpeg",
                "image/jpeg",
                content
        );

        // when & then
        assertThatNoException()
                .isThrownBy(() -> fileValidator.validateImageFile(file));
    }

    @Test
    void png_이미지_파일_업로드_성공() {
        // given
        byte[] content = new byte[5 * 1024 * 1024];
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.png",
                "image/png",
                content
        );

        // when & then
        assertThatNoException()
                .isThrownBy(() -> fileValidator.validateImageFile(file));
    }

    @Test
    void webp_이미지_파일_업로드_성공() {
        // given
        byte[] content = new byte[5 * 1024 * 1024];
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.webp",
                "image/webp",
                content
        );

        // when & then
        assertThatNoException()
                .isThrownBy(() -> fileValidator.validateImageFile(file));
    }

    @Test
    void heic_이미지_파일_업로드_성공() {
        // given
        byte[] content = new byte[5 * 1024 * 1024];
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.heic",
                "image/heic",
                content
        );

        // when & then
        assertThatNoException()
                .isThrownBy(() -> fileValidator.validateImageFile(file));
    }

    @Test
    void 대문자_확장자도_허용한다() {
        // given
        byte[] content = new byte[5 * 1024 * 1024];
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.JPG",
                "image/jpeg",
                content
        );

        // when & then
        assertThatNoException()
                .isThrownBy(() -> fileValidator.validateImageFile(file));
    }

    @Test
    void 이미지_파일_크기가_10MB를_초과하면_예외_발생() {
        // given
        byte[] content = new byte[11 * 1024 * 1024]; // 11MB
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                content
        );

        // when & then
        assertThatThrownBy(() -> fileValidator.validateImageFile(file))
                .isInstanceOf(CareerHubException.class)
                .hasMessage(ErrorStatus.FILE_SIZE_EXCEEDED.getMessage());
    }

    @Test
    void 허용되지_않은_이미지_확장자는_예외_발생() {
        // given
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.gif",
                "image/gif",
                "content".getBytes()
        );

        // when & then
        assertThatThrownBy(() -> fileValidator.validateImageFile(file))
                .isInstanceOf(CareerHubException.class)
                .hasMessage(ErrorStatus.INVALID_FILE_EXTENSION.getMessage());
    }

    @Test
    void pdf_파일_업로드_성공() {
        // given
        byte[] content = new byte[30 * 1024 * 1024]; // 30MB
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "document.pdf",
                "application/pdf",
                content
        );

        // when & then
        assertThatNoException()
                .isThrownBy(() -> fileValidator.validateDocumentFile(file));
    }

    @Test
    void ppt_파일_업로드_성공() {
        // given
        byte[] content = new byte[30 * 1024 * 1024];
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "presentation.ppt",
                "application/vnd.ms-powerpoint",
                content
        );

        // when & then
        assertThatNoException()
                .isThrownBy(() -> fileValidator.validateDocumentFile(file));
    }

    @Test
    void pptx_파일_업로드_성공() {
        // given
        byte[] content = new byte[30 * 1024 * 1024];
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "presentation.pptx",
                "application/vnd.openxmlformats-officedocument.presentationml.presentation",
                content
        );

        // when & then
        assertThatNoException()
                .isThrownBy(() -> fileValidator.validateDocumentFile(file));
    }

    @Test
    void doc_파일_업로드_성공() {
        // given
        byte[] content = new byte[30 * 1024 * 1024];
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "document.doc",
                "application/msword",
                content
        );

        // when & then
        assertThatNoException()
                .isThrownBy(() -> fileValidator.validateDocumentFile(file));
    }

    @Test
    void docx_파일_업로드_성공() {
        // given
        byte[] content = new byte[30 * 1024 * 1024];
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "document.docx",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                content
        );

        // when & then
        assertThatNoException()
                .isThrownBy(() -> fileValidator.validateDocumentFile(file));
    }

    @Test
    void 문서_파일_크기가_50MB를_초과하면_예외_발생() {
        // given
        byte[] content = new byte[51 * 1024 * 1024]; // 51MB
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "document.pdf",
                "application/pdf",
                content
        );

        // when & then
        assertThatThrownBy(() -> fileValidator.validateDocumentFile(file))
                .isInstanceOf(CareerHubException.class)
                .hasMessage(ErrorStatus.FILE_SIZE_EXCEEDED.getMessage());
    }

    @Test
    void 허용되지_않은_문서_확장자는_예외_발생() {
        // given
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "document.txt",
                "text/plain",
                "content".getBytes()
        );

        // when & then
        assertThatThrownBy(() -> fileValidator.validateDocumentFile(file))
                .isInstanceOf(CareerHubException.class)
                .hasMessage(ErrorStatus.INVALID_FILE_EXTENSION.getMessage());
    }


    @Test
    void 이미지_파일_검증_성공() {
        // given
        byte[] content = new byte[5 * 1024 * 1024];
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                content
        );

        // when & then
        assertThatNoException()
                .isThrownBy(() -> fileValidator.validateFile(file));
    }

    @Test
    void 문서_파일_검증_성공() {
        // given
        byte[] content = new byte[30 * 1024 * 1024];
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "document.pdf",
                "application/pdf",
                content
        );

        // when & then
        assertThatNoException()
                .isThrownBy(() -> fileValidator.validateFile(file));
    }

    @Test
    void 이미지도_문서도_아닌_파일은_예외_발생() {
        // given
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "video.mp4",
                "video/mp4",
                "content".getBytes()
        );

        // when & then
        assertThatThrownBy(() -> fileValidator.validateFile(file))
                .isInstanceOf(CareerHubException.class)
                .hasMessage(ErrorStatus.INVALID_FILE_EXTENSION.getMessage());
    }

    @Test
    void 이미지_파일이_10MB를_초과하면_예외_발생() {
        // given
        byte[] content = new byte[11 * 1024 * 1024];
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                content
        );

        // when & then
        assertThatThrownBy(() -> fileValidator.validateFile(file))
                .isInstanceOf(CareerHubException.class)
                .hasMessage(ErrorStatus.FILE_SIZE_EXCEEDED.getMessage());
    }

    @Test
    void 문서_파일이_50MB를_초과하면_예외_발생() {
        // given
        byte[] content = new byte[51 * 1024 * 1024];
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "document.pdf",
                "application/pdf",
                content
        );

        // when & then
        assertThatThrownBy(() -> fileValidator.validateFile(file))
                .isInstanceOf(CareerHubException.class)
                .hasMessage(ErrorStatus.FILE_SIZE_EXCEEDED.getMessage());
    }

    @Test
    void 빈_파일은_예외_발생() {
        // given
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                new byte[0]
        );

        // when & then
        assertThatThrownBy(() -> fileValidator.validateFile(file))
                .isInstanceOf(CareerHubException.class)
                .hasMessage(ErrorStatus.NOT_FOUND_FILE.getMessage());
    }

    @Test
    void 파일명이_null_이면_예외_발생() {
        // given
        MockMultipartFile file = new MockMultipartFile(
                "file",
                null,
                "image/jpeg",
                "content".getBytes()
        );

        // when & then
        assertThatThrownBy(() -> fileValidator.validateFile(file))
                .isInstanceOf(CareerHubException.class)
                .hasMessage(ErrorStatus.NOT_FOUND_FILE.getMessage());
    }

    @Test
    void 확장자가_없는_파일은_예외_발생() {
        // given
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "testfile",
                "image/jpeg",
                "content".getBytes()
        );

        // when & then
        assertThatThrownBy(() -> fileValidator.validateFile(file))
                .isInstanceOf(CareerHubException.class)
                .hasMessage(ErrorStatus.INVALID_FILE_EXTENSION.getMessage());
    }

    @Test
    void 확장자가_점으로_끝나는_파일은_예외_발생() {
        // given
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "testfile.",
                "image/jpeg",
                "content".getBytes()
        );

        // when & then
        assertThatThrownBy(() -> fileValidator.validateFile(file))
                .isInstanceOf(CareerHubException.class)
                .hasMessage(ErrorStatus.INVALID_FILE_EXTENSION.getMessage());
    }

    @Test
    void 파일명에_여러_점이_있어도_마지막_확장자만_검증한다() {
        // given
        byte[] content = new byte[5 * 1024 * 1024];
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.backup.final.jpg",
                "image/jpeg",
                content
        );

        // when & then
        assertThatNoException()
                .isThrownBy(() -> fileValidator.validateFile(file));
    }

    @Test
    void 이미지_파일_크기가_정확히_10MB이면_성공() {
        // given
        byte[] content = new byte[10 * 1024 * 1024]; // 정확히 10MB
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                content
        );

        // when & then
        assertThatNoException()
                .isThrownBy(() -> fileValidator.validateImageFile(file));
    }

    @Test
    void 이미지_파일_크기가_10MB_초과_1바이트면_예외_발생() {
        // given
        byte[] content = new byte[10 * 1024 * 1024 + 1]; // 10MB + 1byte
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                content
        );

        // when & then
        assertThatThrownBy(() -> fileValidator.validateImageFile(file))
                .isInstanceOf(CareerHubException.class)
                .hasMessage(ErrorStatus.FILE_SIZE_EXCEEDED.getMessage());
    }

    @Test
    void 문서_파일_크기가_정확히_50MB이면_성공() {
        // given
        byte[] content = new byte[50 * 1024 * 1024]; // 정확히 50MB
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "document.pdf",
                "application/pdf",
                content
        );

        // when & then
        assertThatNoException()
                .isThrownBy(() -> fileValidator.validateDocumentFile(file));
    }

    @Test
    void 문서_파일_크기가_50MB_초과_1바이트면_예외_발생() {
        // given
        byte[] content = new byte[50 * 1024 * 1024 + 1]; // 50MB + 1byte
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "document.pdf",
                "application/pdf",
                content
        );

        // when & then
        assertThatThrownBy(() -> fileValidator.validateDocumentFile(file))
                .isInstanceOf(CareerHubException.class)
                .hasMessage(ErrorStatus.FILE_SIZE_EXCEEDED.getMessage());
    }

    @Test
    void 파일_크기가_0바이트면_빈_파일로_예외_발생() {
        // given
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "empty.jpg",
                "image/jpeg",
                new byte[0]
        );

        // when & then
        assertThatThrownBy(() -> fileValidator.validateFile(file))
                .isInstanceOf(CareerHubException.class)
                .hasMessage(ErrorStatus.NOT_FOUND_FILE.getMessage());
    }

    @Test
    void 파일_크기가_1바이트면_성공() {
        // given
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "tiny.jpg",
                "image/jpeg",
                new byte[1]
        );

        // when & then
        assertThatNoException()
                .isThrownBy(() -> fileValidator.validateFile(file));
    }

}
