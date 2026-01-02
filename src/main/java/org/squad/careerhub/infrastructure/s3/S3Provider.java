package org.squad.careerhub.infrastructure.s3;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.squad.careerhub.domain.application.service.FileProvider;
import org.squad.careerhub.domain.application.service.dto.response.FileResponse;
import org.squad.careerhub.global.error.CareerHubException;
import org.squad.careerhub.global.error.ErrorStatus;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Slf4j
@RequiredArgsConstructor
@Component
public class S3Provider implements FileProvider {

    private final S3Client s3Client;
    private final FileValidator fileValidator;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.s3.dir}")
    private String dir;
    // NOTE: 추후 보안 문제로 pre-signed URL 방식 || CloudFront + S3 방식으로 변경 고려
    public List<FileResponse> uploadFiles(List<MultipartFile> files) {
        List<FileResponse> uploadedFiles = new ArrayList<>();
        for (MultipartFile file : files) {
            FileResponse uploadFile = uploadFile(file);
            uploadedFiles.add(uploadFile);
        }

        return uploadedFiles;
    }

    public FileResponse uploadFile(MultipartFile file) {
        fileValidator.validateFile(file);

        String fileName = createFileName(file);
        uploadFileToS3(file, fileName, s3Client);

        return FileResponse.builder()
                .url(getFileUrl(fileName))
                .fileName(file.getOriginalFilename())
                .fileType(file.getContentType())
                .build();
    }

    public void deleteFiles(List<String> fileUrls) {
        fileUrls.forEach(this::deleteFile);
    }

    public void deleteFile(String fileUrl) {
        try {
            String key = extractKeyFromUrl(fileUrl);

            DeleteObjectRequest request = DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(fileUrl)
                    .build();
            s3Client.deleteObject(request);

            log.info("[AWS] S3 파일 삭제 - bucket: {}, key: [{}]", bucket, key);
        } catch (S3Exception e) {
            throw new CareerHubException(ErrorStatus.AWS_S3_ERROR);
        } catch (Exception ex) {
            throw new CareerHubException(ErrorStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void uploadFileToS3(MultipartFile file, String fileName, S3Client s3Client) {
        // 항상 InputStream이 닫히도록 try-with-resources를 사용합니다
        try (InputStream is = file.getInputStream()) {
            RequestBody requestBody = RequestBody.fromInputStream(is, file.getSize());

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(fileName)
                    .contentType(file.getContentType())
                    .contentLength(file.getSize())
                    .build();

            s3Client.putObject(putObjectRequest, requestBody);

        } catch (IOException | S3Exception e) {
            log.error("[AWS] S3 파일 업로드 실패: {}", fileName, e);
            throw new CareerHubException(ErrorStatus.FAILED_TO_UPLOAD_FILE);
        }
    }

    private String createFileName(MultipartFile file) {
        return dir + UUID.randomUUID() + "-" + file.getOriginalFilename();
    }

    private String getFileUrl(String filename) {
        GetUrlRequest getUrlRequest = GetUrlRequest.builder()
                .bucket(bucket)
                .key(filename)
                .build();

        return String.valueOf(s3Client.utilities().getUrl(getUrlRequest));
    }

    private String extractKeyFromUrl(String imageUrl) {
        try {
            URI uri = new URI(imageUrl);
            String encodedPath = uri.getPath();
            String decodedPath = URLDecoder.decode(encodedPath, StandardCharsets.UTF_8);
            String keyWithSpaces = decodedPath.replace("+", " "); // "+"를 공백으로 변환

            return keyWithSpaces.substring(1); // 맨 앞 "/" 제거
        } catch (Exception e) {
            log.warn("[AWS] 유효하지 않은 URL: {}", imageUrl);
            throw new CareerHubException(ErrorStatus.INVALID_S3_URL);
        }
    }

}