package org.squad.careerhub.domain.application.service;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import org.squad.careerhub.domain.application.service.dto.response.FileResponse;

public interface FileProvider {

    List<FileResponse> uploadFiles(List<MultipartFile> files);

}
