package org.squad.careerhub.domain.application.service.dto.response;

import lombok.Builder;

@Builder
public record FileResponse(
        String url,
        String fileName,
        String fileType
) {

}