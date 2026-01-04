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
    NOT_FOUND_MEMBER                         (404, "회원을 찾을 수 없습니다."),
    NOT_FOUND_ACTIVE_MEMBER_BY_REFRESH_TOKEN (404, "요청하신 Refresh Token 으로 활성화 된 회원을 찾을 수 없습니다."),


    // APPLICATION
    INVALID_DOCS_STAGE_RULE               (400, "지원서 생성 시 전형 단계가 서류 전형일 경우에만 서류 전형 정보를 입력할 수 있습니다."),
    INVALID_ETC_STAGE_RULE                (400, "지원서 생성 시 기타 전형 진행 중인 경우에만 기타 전형 정보를 입력해야 합니다."),
    INVALID_SCHEDULE_TYPE_RULE            (400, "지원서 작성 시 전형 단계가 면접 유형일 경우에만 면접 일정 생성이 가능합니다."),
    INVALID_FINAL_APPLICATION_STATUS_RULE (400 , "지원서 작성 시 전형 단계가 지원 종료일 경우에만 지원서 최종 상태를 입력할 수 있습니다."),
    NOT_FOUND_APPLICATION                 (404, "지원서를 찾을 수 없습니다."),
    NOT_FOUND_APPLICATION_BY_AUTHOR       (404, "해당 작성자의 지원서를 찾을 수 없습니다."),

    // REVIEW
    INTERVIEW_QUESTION_NOT_BELONG_TO_REVIEW (403, "해당 면접 후기에 속하지 않는 질문입니다."),
    NOT_FOUND_REVIEW                        (404, "리뷰를 찾을 수 없습니다."),
    NOT_FOUND_INTERVIEW_QUESTION            (404, "면접 질문을 찾을 수 없습니다."),

    // JOB POSTING
    URL_ERROR                       (400, "허용되지 않는 URL입니다."),
    JOB_POSTING_ROBOTS_BLOCKED      (400, "robots.txt에 의해 크롤링이 허용되지 않는 URL입니다."),
    JOB_POSTING_NEED_LOGIN          (400, "로그인이 필요한 채용공고입니다."),
    JOB_POSTING_JS_RENDER_REQUIRED  (400, "JS 렌더링이 필요한 채용공고라 현재 방식으로는 분석할 수 없습니다."),
    JOB_POSTING_READ_FAILED         (400, "채용공고 내용을 가져오지 못했습니다."),
    JOB_POSTING_EXTRACTION_FAILED   (400, "채용공고 정보 추출에 실패했습니다."),
    JOB_POSTING_AI_FAILED           (500, "채용공고 AI 분석에 실패하였습니다."),
    JOB_POSTING_AI_QUOTA_EXCEEDED   (429, "채용공고 분석 AI 무료 사용량이 초과되었습니다. 잠시 후 다시 시도해주세요."),

    // SCHEDULE (STAGE_SCHEDULE)
    NOT_FOUND_SCHEDULE                 (404, "일정을 찾을 수 없습니다."),
    INVALID_SCHEDULE_AUTHOR_MISMATCH   (403, "일정 작성자가 지원서 작성자와 일치하지 않습니다."),
    INVALID_SUBMISSION_STATUS_RULE     (400, "서류 전형(DOCUMENT)에서만 제출 상태를 설정할 수 있습니다."),

    // OAUTH
    UNSUPPORTED_OAUTH_PROVIDER (400, "지원하지 않는 OAuth 제공자입니다."),

    // JWT
    INVALID_TOKEN                      (401, "유효하지 않은 JWT 토큰입니다."),
    INVALID_SOCIAL_TOKEN               (401, "유효하지 않은 소셜 토큰입니다."),
    NOT_FOUND_TOKEN                    (404, "토큰을 찾을 수 없습니다."),
    CONCURRENT_REQUESTS_LIMIT_EXCEEDED (429, "동시에 여러 토큰 재발급 요청이 감지되었습니다. 잠시 후 다시 시도해주세요."),

    // AWS
    INVALID_FILE_EXTENSION (400, "지원하지 않는 파일 확장자입니다."),
    INVALID_S3_URL         (400, "AWS S3 URL이 올바르지 않습니다."),
    NOT_FOUND_FILE         (404, "파일이 존재하지 않습니다."),
    AWS_S3_ERROR           (500, "AWS S3 내부 에러"),
    FAILED_TO_UPLOAD_FILE  (500, "파일 업로드에 실패하였습니다."),
    FILE_SIZE_EXCEEDED     (500, "파일 크기가 허용된 최대 크기를 초과하였습니다."),

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