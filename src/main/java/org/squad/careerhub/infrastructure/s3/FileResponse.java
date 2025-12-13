package org.squad.careerhub.infrastructure.s3;

import lombok.Builder;

@Builder
public record FileResponse(
        String url,
        String fileName,
        String fileType
) {

}