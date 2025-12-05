package org.squad.careerhub.domain.jobposting.service.dto;

import java.util.List;
import lombok.Builder;
import org.squad.careerhub.domain.jobposting.enums.JobPostingExtractStatus;

@Builder
public record JobPostingExtractServiceResponse(
    String company,
    String position,
    String deadline,                    // 문자열 or null
    List<String> recruitmentProcess,    // 전형 단계
    List<String> mainTasks,             // 주요 업무
    List<String> requiredQualifications,// 필수 자격
    List<String> preferredQualifications,// 우대 사항
    JobPostingExtractStatus status      // SUCCESS / PARTIAL / FAILED
) {

    public static JobPostingExtractServiceResponse of(
        String company,
        String position,
        String deadline,
        List<String> recruitmentProcess,
        List<String> mainTasks,
        List<String> requiredQualifications,
        List<String> preferredQualifications,
        JobPostingExtractStatus status
    ) {
        return JobPostingExtractServiceResponse.builder()
            .company(company)
            .position(position)
            .deadline(deadline)
            .recruitmentProcess(recruitmentProcess)
            .mainTasks(mainTasks)
            .requiredQualifications(requiredQualifications)
            .preferredQualifications(preferredQualifications)
            .status(status)
            .build();
    }

    /**
     * Swagger / 테스트용 샘플 (원티드 에어스메디컬 323219 기준)
     */
    public static JobPostingExtractServiceResponse sample() {
        return JobPostingExtractServiceResponse.builder()
            .company("에어스메디컬")
            .position("[SwiftSight] AI Research Scientist")
            .deadline(null) // 상시채용 등 마감일 없을 때
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