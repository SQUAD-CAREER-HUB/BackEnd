package org.squad.careerhub.domain.jobposting.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;
import org.squad.careerhub.domain.jobposting.enums.JobPostingExtractStatus;
import org.squad.careerhub.domain.jobposting.service.dto.JobPostingSnapshotResponse;

@Schema(description = "채용 공고 정보 추출 응답 DTO (Controller 레이어)")
@Builder
public record JobPostingExtractResponse(

    @Schema(description = "회사명", example = "에어스메디컬")
    String company,

    @Schema(description = "직무명", example = "[SwiftSight] AI Research Scientist")
    String position,

    @Schema(
        description = "공고 마감일 (상시채용 등 날짜 없으면 null)",
        example = "2025-11-20 23:59:59"
    )
    String deadline,

    @Schema(
        description = "채용 전형 단계 목록",
        example = "[\"서류\", \"1차 실무 면접\", \"2차 컬처핏\", \"최종 면접\"]"
    )
    List<String> recruitmentProcess,

    @Schema(
        description = "주요 업무",
        example = """
            [
              "뇌 MRI 등 의료 영상을 대상으로 한 AI 모델을 설계·학습하고 성능을 고도화한다.",
              "ML 프레임워크를 활용해 연구용 및 서비스용 AI 모델 개발 파이프라인을 효율적으로 구축·개선한다.",
              "의료 AI 및 딥러닝 관련 최신 연구 트렌드를 파악하고 제품과 연구에 적용한다."
            ]
            """
    )
    List<String> mainTasks,

    @Schema(
        description = "필수 자격 요건",
        example = """
            [
              "PyTorch를 활용해 딥러닝/AI 모델을 구현하고 튜닝해본 경험이 있다.",
              "Git 및 클라우드 환경에서 협업 개발과 코드 버전 관리를 원활히 수행할 수 있다.",
              "연구 결과와 아이디어를 영어로 문서화하고 동료들과 커뮤니케이션할 수 있는 능력이 있다."
            ]
            """
    )
    List<String> requiredQualifications,

    @Schema(
        description = "우대 사항",
        example = """
                        [
                          "의료 영상(예: MRI, CT) 관련 프로젝트 경험",
                          "논문 작성 및 컨퍼런스 발표 경험",
                          "대규모 모델 학습 및 MLOps 환경 구축 경험"
                        ]
                        """
    )
    List<String> preferredQualifications,

    @Schema(
        description = "AI 추출 상태 (SUCCESS: 전체 추출 성공, PARTIAL: 일부만 추출, FAILED: 추출 실패)",
        example = "PARTIAL"
    )
    JobPostingExtractStatus status
) {

    public static JobPostingExtractResponse from(JobPostingSnapshotResponse serviceDto) {
        return JobPostingExtractResponse.builder()
            .company(serviceDto.company())
            .position(serviceDto.position())
            .deadline(serviceDto.deadline())
            .recruitmentProcess(serviceDto.recruitmentProcess())
            .mainTasks(serviceDto.mainTasks())
            .requiredQualifications(serviceDto.requiredQualifications())
            .preferredQualifications(serviceDto.preferredQualifications())
            .status(serviceDto.status())
            .build();
    }

    /**
     * Swagger 예시 응답용 샘플 데이터
     * (원티드 공고: https://www.wanted.co.kr/wd/323219 기준)
     */
    public static JobPostingExtractResponse mock() {
        return JobPostingExtractResponse.builder()
            .company("에어스메디컬")
            .position("[SwiftSight] AI Research Scientist")
            .deadline(null)
            .recruitmentProcess(List.of())
            .mainTasks(List.of(
                "뇌 MRI 등 의료 영상을 대상으로 한 AI 모델을 설계·학습하고 성능을 고도화한다.",
                "ML 프레임워크를 활용해 연구용 및 서비스용 AI 모델 개발 파이프라인을 효율적으로 구축·개선한다.",
                "의료 AI 및 딥러닝 관련 최신 연구 트렌드를 파악하고 제품과 연구에 적용한다."
            ))
            .requiredQualifications(List.of(
                "PyTorch를 활용해 딥러닝/AI 모델을 구현하고 튜닝해본 경험이 있다.",
                "Git 및 클라우드 환경에서 협업 개발과 코드 버전 관리를 원활히 수행할 수 있다.",
                "연구 결과와 아이디어를 영어로 문서화하고 동료들과 커뮤니케이션할 수 있는 능력이 있다."
            ))
            .preferredQualifications(List.of(
                "의료 영상(예: MRI, CT) 관련 프로젝트 경험",
                "논문 작성 및 컨퍼런스 발표 경험",
                "대규모 모델 학습 및 MLOps 환경 구축 경험"
            ))
            .status(JobPostingExtractStatus.PARTIAL)
            .build();
    }
}