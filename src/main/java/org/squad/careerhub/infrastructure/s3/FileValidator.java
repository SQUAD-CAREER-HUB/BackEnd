package org.squad.careerhub.infrastructure.s3;

import static java.util.Objects.requireNonNull;

import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.squad.careerhub.global.error.CareerHubException;
import org.squad.careerhub.global.error.ErrorStatus;

@Component
public class FileValidator {

    private static final List<String> IMAGE_EXTENSIONS = List.of("jpg", "jpeg", "png", "webp", "heic");
    private static final List<String> DOCUMENT_EXTENSIONS = List.of("pdf", "ppt", "pptx", "doc", "docx");

    private static final long IMAGE_MAX_SIZE = 10 * 1024 * 1024;  // 10MB
    private static final long DOCUMENT_MAX_SIZE = 50 * 1024 * 1024; // 50MB

    /**
     * 이미지 파일 검증
     */
    public void validateImageFile(MultipartFile file) {
        validateFileNameNotBlank(file);
        validateFileExtension(file, IMAGE_EXTENSIONS);
        validateFileSize(file, IMAGE_MAX_SIZE);
    }

    /**
     * 문서 파일 검증 (PDF, PPT 등)
     */
    public void validateDocumentFile(MultipartFile file) {
        validateFileNameNotBlank(file);
        validateFileExtension(file, DOCUMENT_EXTENSIONS);
        validateFileSize(file, DOCUMENT_MAX_SIZE);
    }

    /**
     * 모든 타입 허용 검증
     */
    public void validateFile(MultipartFile file) {
        validateFileNameNotBlank(file);

        String extension = getFileExtension(file.getOriginalFilename());

        if (IMAGE_EXTENSIONS.contains(extension.toLowerCase())) {
            validateImageFile(file);
        } else if (DOCUMENT_EXTENSIONS.contains(extension.toLowerCase())) {
            validateDocumentFile(file);
        } else {
            throw new CareerHubException(ErrorStatus.INVALID_FILE_EXTENSION);
        }
    }

    private void validateFileNameNotBlank(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (file.isEmpty() || originalFilename == null || originalFilename.isBlank()) {
            throw new CareerHubException(ErrorStatus.NOT_FOUND_FILE);
        }

    }

    private void validateFileExtension(MultipartFile file, List<String> allowedExtensions) {
        String filename = file.getOriginalFilename();
        int extensionIndex = requireNonNull(filename).lastIndexOf(".");

        if (extensionIndex == -1 || filename.endsWith(".")) {
            throw new CareerHubException(ErrorStatus.INVALID_FILE_EXTENSION);
        }

        String extension = filename.substring(extensionIndex + 1);

        if (!allowedExtensions.contains(extension.toLowerCase())) {
            throw new CareerHubException(ErrorStatus.INVALID_FILE_EXTENSION);
        }
    }

    private void validateFileSize(MultipartFile file, long maxSize) {
        if (file.getSize() > maxSize) {
            throw new CareerHubException(ErrorStatus.FILE_SIZE_EXCEEDED);
        }
    }

    private String getFileExtension(String filename) {
        int extensionIndex = requireNonNull(filename).lastIndexOf(".");
        return filename.substring(extensionIndex + 1);
    }

}