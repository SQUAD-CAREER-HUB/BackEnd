package org.squad.careerhub.global.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorStatus {

    // COMMON
    BAD_REQUEST      (400, "요청 형식이 올바르지 않습니다."),
    FORBIDDEN_MODIFY (403, "해당 리소스를 수정할 권한이 없습니다."),
    FORBIDDEN_DELETE (403, "해당 리소스를 삭제할 권한이 없습니다."),
    NOT_FOUND        (404, "요청한 리소스를 찾을 수 없습니다."),
    DUPLICATE        (409, "이미 존재하는 리소스입니다."),

    // MEMBER
    NOT_FOUND_MEMBER (404, "회원을 찾을 수 없습니다."),

    // APPLICATION
    INVALID_MEMO_RULE          (400, "지원서 생성 시 기타 전형 진행 중인 경우에만 메모를 작성해야 합니다."),
    INVALID_ETC_STAGE_RULE     (400, "지원서 생성 시 기타 전형 진행 중인 경우에만 기타 전형 정보를 입력해야 합니다."),
    INVALID_SCHEDULE_TYPE_RULE (400, "지원서 작성 시 전형 단계가 면접 유형일 경우에만 면접 일정 생성이 가능합니다."),
    NOT_FOUND_APPLICATION      (404, "지원서를 찾을 수 없습니다."),

    // REVIEW
    INTERVIEW_QUESTION_NOT_BELONG_TO_REVIEW (403, "해당 면접 후기에 속하지 않는 질문입니다."),
    NOT_FOUND_REVIEW                        (404, "리뷰를 찾을 수 없습니다."),
    NOT_FOUND_INTERVIEW_QUESTION            (404, "면접 질문을 찾을 수 없습니다."),



    // OAUTH
    UNSUPPORTED_OAUTH_PROVIDER (400, "지원하지 않는 OAuth 제공자입니다."),

    // JWT
    INVALID_TOKEN        (401, "유효하지 않은 JWT 토큰입니다."),
    INVALID_SOCIAL_TOKEN (401, "유효하지 않은 소셜 토큰입니다."),
    NOT_FOUND_TOKEN      (404, "토큰을 찾을 수 없습니다."),

    // AWS
    INVALID_FILE_EXTENSION (400, "지원하지 않는 파일 확장자입니다."),
    NOT_FOUND_FILE         (404, "파일이 존재하지 않습니다."),
    AWS_S3_ERROR           (500, "AWS S3 내부 에러"),
    FAILED_TO_UPLOAD_FILE  (500, "파일 업로드에 실패하였습니다."),

    // ETC
    BAD_REQUEST_ARGUMENT   (400, "유효하지 않은 인자입니다."),
    UNAUTHORIZED_ERROR     (401, "인증되지 않은 사용자입니다."),
    EMPTY_SECURITY_CONTEXT (401, "Security Context 에 인증 정보가 없습니다."),
    FORBIDDEN_ERROR        (403, "접근 권한이 없습니다."),
    INTERNAL_SERVER_ERROR  (500, "서버 내부 에러"),

    ;

    private final int statusCode;
    private final String message;

}