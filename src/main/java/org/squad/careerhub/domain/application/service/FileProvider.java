package org.squad.careerhub.domain.application.service;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import org.squad.careerhub.infrastructure.s3.FileResponse;

public interface FileProvider {

    List<FileResponse> uploadImages(List<MultipartFile> files);

}
