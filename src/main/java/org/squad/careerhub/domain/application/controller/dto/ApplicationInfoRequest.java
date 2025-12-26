package org.squad.careerhub.domain.application.controller.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Builder;
import org.squad.careerhub.domain.application.entity.ApplicationMethod;
import org.squad.careerhub.domain.application.service.dto.NewApplicationInfo;

@Schema(description = "지원 정보 요청 DTO")
@Builder
public record ApplicationInfoRequest(
        @Schema(description = "마감일", example = "2025.03.25", type = "string", pattern = "yyyy.MM.dd")
        @JsonFormat(pattern = "yyyy.MM.dd")
        @NotNull(message = "마감일은 필수 입력 항목입니다.")
        LocalDate deadline,

        @Schema(description = "지원 방법", example = "HOMEPAGE")
        @NotNull(message = "지원 방법은 필수 입력 항목입니다.")
        ApplicationMethod applicationMethod
) {

    public NewApplicationInfo toNewApplicationInfo() {
        return NewApplicationInfo.builder()
                .deadline(deadline)
                .applicationMethod(applicationMethod)
                .build();
    }

}

