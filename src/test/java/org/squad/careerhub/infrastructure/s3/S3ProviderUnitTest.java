package org.squad.careerhub.infrastructure.s3;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;
import static org.mockito.BDDMockito.never;
import static org.mockito.BDDMockito.times;
import static org.mockito.BDDMockito.verify;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import org.squad.careerhub.TestDoubleSupport;
import org.squad.careerhub.domain.application.service.dto.response.FileResponse;
import org.squad.careerhub.global.error.CareerHubException;
import org.squad.careerhub.global.error.ErrorStatus;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Utilities;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectResponse;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;

class S3ProviderTest extends TestDoubleSupport {

    @Mock
    private S3Client s3Client;

    @Mock
    private FileValidator fileValidator;

    @Mock
    private S3Utilities s3Utilities;

    @InjectMocks
    private S3Provider s3Provider;

    private static final String BUCKET = "test-bucket";
    private static final String DIR = "uploads/";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(s3Provider, "bucket", BUCKET);
        ReflectionTestUtils.setField(s3Provider, "dir", DIR);
    }

    @Test
    void 파일을_S3에_업로드하고_FileResponse를_반환한다() throws Exception {
        // given
        var file = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        var expectedUrl = "https://test-bucket.s3.ap-northeast-2.amazonaws.com/uploads/test-uuid-test.jpg";

        given(s3Client.putObject(any(PutObjectRequest.class), (RequestBody) any()))
                .willReturn(PutObjectResponse.builder().build());
        given(s3Client.utilities()).willReturn(s3Utilities);
        given(s3Utilities.getUrl(any(GetUrlRequest.class)))
                .willReturn(new URL(expectedUrl));

        willDoNothing().given(fileValidator).validateFile(file);

        // when
        var response = s3Provider.uploadFile(file);

        // then
        assertThat(response).isNotNull().extracting(
                FileResponse::url,
                FileResponse::fileName,
                FileResponse::fileType
        ).containsExactly(
                expectedUrl,
                "test.jpg",
                "image/jpeg"
        );

        verify(fileValidator).validateFile(file);
        verify(s3Client).putObject(any(PutObjectRequest.class), (RequestBody) any());
        verify(s3Client.utilities()).getUrl(any(GetUrlRequest.class));
    }

    @Test
    void 파일_검증_실패_시_예외가_발생한다() {
        // given
        var file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "test content".getBytes()
        );

        willThrow(new CareerHubException(ErrorStatus.INVALID_FILE_EXTENSION))
                .given(fileValidator).validateFile(file);

        // when & then
        assertThatThrownBy(() -> s3Provider.uploadFile(file))
                .isInstanceOf(CareerHubException.class)
                .hasMessage(ErrorStatus.INVALID_FILE_EXTENSION.getMessage());

        verify(fileValidator).validateFile(file);
        verify(s3Client, never()).putObject(any(PutObjectRequest.class), (RequestBody) any());
    }

    @Test
    void S3_업로드_중_오류_발생() {
        // given
        var file = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        willDoNothing().given(fileValidator).validateFile(file);
        given(s3Client.putObject(any(PutObjectRequest.class), (RequestBody) any()))
                .willThrow(S3Exception.builder().message("S3 Error").build());

        // when & then
        assertThatThrownBy(() -> s3Provider.uploadFile(file))
                .isInstanceOf(CareerHubException.class)
                .hasMessage(ErrorStatus.FAILED_TO_UPLOAD_FILE.getMessage());

        verify(fileValidator).validateFile(file);
        verify(s3Client).putObject(any(PutObjectRequest.class), (RequestBody) any());
    }

    @Test
    void InputStream_읽기_실패() {
        // given
        var file = mock(MultipartFile.class);

        willDoNothing().given(fileValidator).validateFile(file);

        try {
            given(file.getInputStream()).willThrow(new IOException("IO Error"));
        } catch (IOException e) {
            fail("Should not throw exception in test setup");
        }

        // when & then
        assertThatThrownBy(() -> s3Provider.uploadFile(file))
                .isInstanceOf(CareerHubException.class)
                .hasFieldOrPropertyWithValue("errorStatus", ErrorStatus.FAILED_TO_UPLOAD_FILE);

        verify(fileValidator).validateFile(file);
    }

    @Test
    void 여러_파일을_업로드하고_List_FileResponse_를_반환한다() throws Exception {
        // given
        var file1 = new MockMultipartFile(
                "file1", "test1.jpg", "image/jpeg", "content1".getBytes()
        );
        var file2 = new MockMultipartFile(
                "file2", "test2.png", "image/png", "content2".getBytes()
        );
        List<MultipartFile> files = List.of(file1, file2);

        var url1 = "https://test-bucket.s3.ap-northeast-2.amazonaws.com/uploads/uuid1-test1.jpg";
        var url2 = "https://test-bucket.s3.ap-northeast-2.amazonaws.com/uploads/uuid2-test2.png";

        given(s3Client.putObject(any(PutObjectRequest.class), (RequestBody) any()))
                .willReturn(PutObjectResponse.builder().build());
        given(s3Client.utilities()).willReturn(s3Utilities);
        given(s3Utilities.getUrl(any(GetUrlRequest.class)))
                .willReturn(new URL(url1), new URL(url2));

        willDoNothing().given(fileValidator).validateFile(any());

        // when
        List<FileResponse> responses = s3Provider.uploadFiles(files);

        // then
        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).fileName()).isEqualTo("test1.jpg");
        assertThat(responses.get(0).fileType()).isEqualTo("image/jpeg");
        assertThat(responses.get(1).fileName()).isEqualTo("test2.png");
        assertThat(responses.get(1).fileType()).isEqualTo("image/png");

        verify(fileValidator, times(2)).validateFile(any());
        verify(s3Client, times(2)).putObject(any(PutObjectRequest.class), (RequestBody) any());
    }

    @Test
    void 빈_리스트_전달_시_빈_리스트_반환() {
        // given
        List<MultipartFile> files = List.of();

        // when
        List<FileResponse> responses = s3Provider.uploadFiles(files);

        // then
        assertThat(responses).isEmpty();
        verify(fileValidator, never()).validateFile(any());
        verify(s3Client, never()).putObject(any(PutObjectRequest.class), (RequestBody) any());
    }

    @Test
    void 파일_중_하나라도_검증_실패하면_예외_발생() {
        // given
        var file1 = new MockMultipartFile(
                "file1", "test1.jpg", "image/jpeg", "content1".getBytes()
        );
        var file2 = new MockMultipartFile(
                "file2", "test2.txt", "text/plain", "content2".getBytes()
        );
        List<MultipartFile> files = List.of(file1, file2);

        given(s3Client.utilities()).willReturn(s3Utilities);
        willDoNothing().given(fileValidator).validateFile(file1);
        willThrow(new CareerHubException(ErrorStatus.INVALID_FILE_EXTENSION))
                .given(fileValidator).validateFile(file2);

        // when & then
        assertThatThrownBy(() -> s3Provider.uploadFiles(files))
                .isInstanceOf(CareerHubException.class)
                .hasMessage(ErrorStatus.INVALID_FILE_EXTENSION.getMessage());
    }

    @Test
    void S3에서_파일을_삭제한다() {
        // given
        String fileUrl = "uploads/uuid-test.jpg";

        given(s3Client.deleteObject(any(DeleteObjectRequest.class)))
                .willReturn(DeleteObjectResponse.builder().build());

        // when
        s3Provider.deleteFile(fileUrl);

    }

    @Test
    void S3_삭제_중_S3Exception_발생() {
        // given
        var fileUrl = "uploads/uuid-test.jpg";

        given(s3Client.deleteObject(any(DeleteObjectRequest.class)))
                .willThrow(S3Exception.builder().message("S3 Error").build());

        // when & then
        assertThatThrownBy(() -> s3Provider.deleteFile(fileUrl))
                .isInstanceOf(CareerHubException.class)
                .hasMessage(ErrorStatus.AWS_S3_ERROR.getMessage());

        verify(s3Client).deleteObject(any(DeleteObjectRequest.class));
    }

    @Test
    void 예상치_못한_예외_발생() {
        // given
        var fileUrl = "uploads/uuid-test.jpg";

        given(s3Client.deleteObject(any(DeleteObjectRequest.class)))
                .willThrow(new RuntimeException("Unexpected Error"));

        // when & then
        assertThatThrownBy(() -> s3Provider.deleteFile(fileUrl))
                .isInstanceOf(CareerHubException.class)
                .hasMessage(ErrorStatus.INTERNAL_SERVER_ERROR.getMessage());

        verify(s3Client).deleteObject(any(DeleteObjectRequest.class));
    }


    @Test
    void 여러_파일을_삭제한다() {
        // given
        var fileUrls = List.of(
                "uploads/uuid1-test1.jpg",
                "uploads/uuid2-test2.png"
        );

        given(s3Client.deleteObject(any(DeleteObjectRequest.class)))
                .willReturn(DeleteObjectResponse.builder().build());

        // when
        s3Provider.deleteFiles(fileUrls);

        // then
        verify(s3Client, times(2)).deleteObject(any(DeleteObjectRequest.class));
    }

    @Test
    void 빈_리스트_전달_시_삭제_작업_없음() {
        // given
        List<String> fileUrls = List.of();

        // when
        s3Provider.deleteFiles(fileUrls);

        // then
        verify(s3Client, never()).deleteObject(any(DeleteObjectRequest.class));
    }

    @Test
    void 파일_중_하나_삭제_실패_시_예외_발생() {
        // given
        var fileUrls = List.of(
                "uploads/uuid1-test1.jpg",
                "uploads/uuid2-test2.png"
        );

        given(s3Client.deleteObject(any(DeleteObjectRequest.class)))
                .willReturn(DeleteObjectResponse.builder().build())
                .willThrow(S3Exception.builder().message("S3 Error").build());

        // when & then
        assertThatThrownBy(() -> s3Provider.deleteFiles(fileUrls))
                .isInstanceOf(CareerHubException.class)
                .hasMessage(ErrorStatus.AWS_S3_ERROR.getMessage());

        verify(s3Client, times(2)).deleteObject(any(DeleteObjectRequest.class));
    }

    @Test
    void 생성된_파일명은_UUID와_원본_파일명을_포함한다() throws Exception {
        // given
        var file = new MockMultipartFile(
                "file", "test.jpg", "image/jpeg", "content".getBytes()
        );

        var expectedUrl = "https://test-bucket.s3.ap-northeast-2.amazonaws.com/uploads/uuid-test.jpg";

        given(s3Client.putObject(any(PutObjectRequest.class), (RequestBody) any()))
                .willReturn(PutObjectResponse.builder().build());
        given(s3Client.utilities()).willReturn(s3Utilities);
        given(s3Utilities.getUrl(any(GetUrlRequest.class)))
                .willReturn(new URL(expectedUrl));

        willDoNothing().given(fileValidator).validateFile(file);

        // when
        FileResponse response = s3Provider.uploadFile(file);

        // then
        assertThat(response.url()).contains(DIR);
        assertThat(response.url()).contains("-test.jpg");
    }

}