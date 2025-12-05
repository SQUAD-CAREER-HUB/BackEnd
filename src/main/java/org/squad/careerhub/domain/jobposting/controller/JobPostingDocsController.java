package org.squad.careerhub.domain.jobposting.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.squad.careerhub.domain.jobposting.controller.dto.JobPostingExtractResponse;
import org.squad.careerhub.global.error.ErrorStatus;
import org.squad.careerhub.global.swagger.ApiExceptions;

@Tag(name = "Job Posting API", description = "채용 공고 URL 기반 정보 추출 API 문서입니다.")
public abstract class JobPostingDocsController {

    @Operation(
        summary = "채용 공고 정보 조회 (AI 추출) - [JWT O]",
        description = """
                    ### 채용 공고 URL을 기반으로 회사/직무/JD/마감일/전형 정보를 자동 추출합니다.
                    
                    - 로그인한 사용자만 사용할 수 있습니다.
                    - 지원자가 채용 공고 URL을 입력하면 서버에서 Gemini 등의 LLM을 통해 정보를 추출합니다.
                    - 추출 결과는 다음과 같습니다.
                      - company: 회사명 (예: 에어스메디컬)
                      - position: 직무명/포지션명 (예: [SwiftSight] AI Research Scientist)
                      - deadline: 공고 마감일 (yy-MM-dd HH:mm:ss 상시채용 등 날짜가 없으면 null)
                      - recruitmentProcess: 전형 단계 목록 (서류, 1차면접, 과제, 최종면접 등)
                      - mainTasks: 주요 업무 리스트
                      - requiredQualifications: 필수 자격 요건 리스트
                      - preferredQualifications: 우대 사항 리스트
                      - status: AI 추출 상태 (SUCCESS / PARTIAL / FAILED)
                    
                    - AI 추출 실패 또는 일부만 추출된 경우, status 필드로 상태를 반환하며
                      프론트에서 결과 확인 화면을 띄운 뒤 수동 입력을 유도할 수 있습니다.
                    """,
        security = { @SecurityRequirement(name = "Bearer") }
    )
    @ApiResponse(
        responseCode = "200",
        description = "채용 공고 정보 조회 성공",
        content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = JobPostingExtractResponse.class),
            examples = {
                @ExampleObject(
                    name = "원티드 에어스메디컬 공고 예시",
                    summary = "https://www.wanted.co.kr/wd/323219 기준 추출 결과 예시",
                    value = """
                        {
                          "company": "에어스메디컬",
                          "position": "[SwiftSight] AI Research Scientist",
                          "deadline": null,
                          "recruitmentProcess": [],
                          "mainTasks": [
                            "뇌 MRI 등 의료 영상을 대상으로 한 AI 모델을 설계·학습하고 성능을 고도화한다.",
                            "ML 프레임워크를 활용해 연구용 및 서비스용 AI 모델 개발 파이프라인을 효율적으로 구축·개선한다.",
                            "의료 AI 및 딥러닝 관련 최신 연구 트렌드를 파악하고 제품과 연구에 적용한다."
                          ],
                          "requiredQualifications": [
                            "PyTorch를 활용해 딥러닝/AI 모델을 구현하고 튜닝해본 경험이 있다.",
                            "Git 및 클라우드 환경에서 협업 개발과 코드 버전 관리를 원활히 수행할 수 있다.",
                            "연구 결과와 아이디어를 영어로 문서화하고 동료들과 커뮤니케이션할 수 있는 능력이 있다."
                          ],
                          "preferredQualifications": [
                            "의료 영상(예: MRI, CT) 관련 프로젝트 경험",
                            "논문 작성 및 컨퍼런스 발표 경험",
                            "대규모 모델 학습 및 MLOps 환경 구축 경험"
                          ],
                          "status": "PARTIAL"
                        }
                        """
                )
            }
        )
    )
    @ApiExceptions(values = {
        ErrorStatus.BAD_REQUEST,
        ErrorStatus.UNAUTHORIZED_ERROR,
        ErrorStatus.INTERNAL_SERVER_ERROR
    })
    public abstract ResponseEntity<JobPostingExtractResponse> getJobPosting(
        @Parameter(
            name = "url",
            description = "채용 공고 URL (원티드, 사람인, 잡코리아, 랠릿 등)",
            required = true,
            example = "https://www.wanted.co.kr/wd/323219"
        )
        String url
    );
}