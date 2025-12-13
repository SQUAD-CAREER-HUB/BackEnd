package org.squad.careerhub.infrastructure.s3;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.squad.careerhub.domain.application.service.FileProvider;

@RequiredArgsConstructor
@Component
public class S3Provider implements FileProvider {

    // NOTE : 실제 S3 업로드 로직은 생략되어 있습니다.
    @Override
    public List<FileResponse> uploadImages(List<MultipartFile> files) {

        return List.of(new FileResponse[]{
            new FileResponse("https://sample-bucket.s3.amazonaws.com/sample-file-key", "fileName1", "image/png"),
            new FileResponse("https://sample-bucket.s3.amazonaws.com/sample-file-key", "fileName2", "image/png"),
        });
    }

}