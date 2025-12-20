package org.squad.careerhub.infrastructure.s3;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
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

@RequiredArgsConstructor
@Component
public class S3Provider implements FileProvider {

    private final S3Client s3Client;
    private final FileValidator fileValidator;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.s3.dir}")
    private String dir;

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

    public void deleteFiles(List<String> imageUrls) {
        imageUrls.forEach(this::deleteFile);
    }

    public void deleteFile(String imageUrl) {
        try {
            DeleteObjectRequest request = DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(imageUrl)
                    .build();
            s3Client.deleteObject(request);
        } catch (S3Exception e) {
            throw new CareerHubException(ErrorStatus.AWS_S3_ERROR);
        } catch (Exception ex) {
            throw new CareerHubException(ErrorStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void uploadFileToS3(MultipartFile file, String fileName, S3Client s3Client) {
        try {
            InputStream is = file.getInputStream();
            RequestBody requestBody = RequestBody.fromInputStream(is, file.getSize());

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(fileName)
                    .contentType(file.getContentType())
                    .contentLength(file.getSize())
                    .build();
            s3Client.putObject(putObjectRequest, requestBody);

            is.close();
        } catch (IOException | S3Exception e) {
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

}