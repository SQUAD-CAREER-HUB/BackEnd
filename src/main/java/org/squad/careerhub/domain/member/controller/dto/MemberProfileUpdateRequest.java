package org.squad.careerhub.domain.member.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import org.squad.careerhub.domain.member.service.dto.MemberProfileUpdateParamRequest;

@Schema(description = "회원 프로필 수정 요청 DTO (Controller 레이어)")
@Builder
public record MemberProfileUpdateRequest(

    @Schema(description = "닉네임", example = "careerhub_dev")
    @NotBlank(message = "닉네임은 필수 입력 값입니다.")
    @Size(max = 30, message = "닉네임은 최대 30자까지 입력 가능합니다.")
    String nickname,

    @Size(max = 400, message = "프로필이미지 url 길이는 400까지 입니다.")
    @NotBlank(message = "프로필이미지는 필수입니다.(기본이미지 또는 설정 이미지)")
    String profileImageUrl

) {

    public MemberProfileUpdateParamRequest toParam (Long memberId) {
        return MemberProfileUpdateParamRequest.of(
            memberId,
            nickname,
            profileImageUrl
        );
    }
}
