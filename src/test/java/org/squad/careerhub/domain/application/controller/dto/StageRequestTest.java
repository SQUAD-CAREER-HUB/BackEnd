package org.squad.careerhub.domain.application.controller.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.squad.careerhub.domain.application.entity.StageType;
import org.squad.careerhub.domain.application.entity.SubmissionStatus;
import org.squad.careerhub.domain.application.service.dto.NewStage;
import org.squad.careerhub.domain.schedule.controller.dto.InterviewScheduleCreateRequest;
import org.squad.careerhub.domain.schedule.enums.InterviewType;

class StageRequestTest {

    @Test
    void 서류_전형_StageRequest를_NewStage로_변환한다() {
        // given
        StageRequest request = StageRequest.builder()
                .stageType(StageType.DOCUMENT)
                .submissionStatus(SubmissionStatus.NOT_SUBMITTED)
                .build();

        // when
        NewStage newStage = request.toNewStage();

        // then
        assertThat(newStage).isNotNull();
        assertThat(newStage.stageType()).isEqualTo(StageType.DOCUMENT);
        assertThat(newStage.submissionStatus()).isEqualTo(SubmissionStatus.NOT_SUBMITTED);
        assertThat(newStage.newEtcSchedule()).isNull();
        assertThat(newStage.newInterviewSchedules()).isEmpty();
    }

    @Test
    void 면접_전형_StageRequest를_NewStage로_변환한다() {
        // given
        var interviewSchedule = InterviewScheduleCreateRequest.builder()
                .name("1차 기술 면접")
                .type(InterviewType.TECH)
                .scheduledAt(LocalDateTime.of(2025, 4, 1, 14, 0))
                .location("서울 본사 3층")
                .build();

        StageRequest request = StageRequest.builder()
                .stageType(StageType.INTERVIEW)
                .interviewSchedules(List.of(interviewSchedule))
                .build();

        // when
        NewStage newStage = request.toNewStage();

        // then
        assertThat(newStage.stageType()).isEqualTo(StageType.INTERVIEW);
        assertThat(newStage.newInterviewSchedules()).hasSize(1);
        assertThat(newStage.newInterviewSchedules().get(0).name()).isEqualTo("1차 기술 면접");
    }

    @Test
    void 기타_전형_StageRequest를_NewStage로_변환한다() {
        // given
        var etcSchedule = new EtcScheduleCreateRequest(
                "코딩 테스트",
                LocalDateTime.of(2025, 3, 28, 10, 0)
        );

        StageRequest request = StageRequest.builder()
                .stageType(StageType.ETC)
                .etcSchedule(etcSchedule)
                .build();

        // when
        NewStage newStage = request.toNewStage();

        // then
        assertThat(newStage.stageType()).isEqualTo(StageType.ETC);
        assertThat(newStage.newEtcSchedule()).isNotNull();
        assertThat(newStage.newEtcSchedule().stageName()).isEqualTo("코딩 테스트");
    }

    @Test
    void etcSchedule이_null인_경우_NewStage의_newEtcSchedule도_null이다() {
        // given
        StageRequest request = StageRequest.builder()
                .stageType(StageType.DOCUMENT)
                .submissionStatus(SubmissionStatus.SUBMITTED)
                .etcSchedule(null)
                .build();

        // when
        NewStage newStage = request.toNewStage();

        // then
        assertThat(newStage.newEtcSchedule()).isNull();
    }

    @Test
    void interviewSchedules가_null인_경우_빈_리스트로_변환된다() {
        // given
        StageRequest request = StageRequest.builder()
                .stageType(StageType.DOCUMENT)
                .submissionStatus(SubmissionStatus.NOT_SUBMITTED)
                .interviewSchedules(null)
                .build();

        // when
        NewStage newStage = request.toNewStage();

        // then
        assertThat(newStage.newInterviewSchedules()).isEmpty();
        assertThat(newStage.newInterviewSchedules()).isNotNull();
    }

    @Test
    void 여러_면접_일정을_가진_StageRequest를_변환한다() {
        // given
        var interview1 = InterviewScheduleCreateRequest.builder()
                .name("1차 면접")
                .type(InterviewType.TECH)
                .scheduledAt(LocalDateTime.of(2025, 4, 1, 14, 0))
                .location("본사")
                .build();

        var interview2 = InterviewScheduleCreateRequest.builder()
                .name("2차 면접")
                .type(InterviewType.FIT)
                .scheduledAt(LocalDateTime.of(2025, 4, 8, 15, 0))
                .location("본사")
                .build();

        StageRequest request = StageRequest.builder()
                .stageType(StageType.INTERVIEW)
                .interviewSchedules(List.of(interview1, interview2))
                .build();

        // when
        NewStage newStage = request.toNewStage();

        // then
        assertThat(newStage.newInterviewSchedules()).hasSize(2);
        assertThat(newStage.newInterviewSchedules().get(0).name()).isEqualTo("1차 면접");
        assertThat(newStage.newInterviewSchedules().get(1).name()).isEqualTo("2차 면접");
    }

    @Test
    void 빈_면접_일정_리스트를_가진_StageRequest를_변환한다() {
        // given
        StageRequest request = StageRequest.builder()
                .stageType(StageType.INTERVIEW)
                .interviewSchedules(List.of())
                .build();

        // when
        NewStage newStage = request.toNewStage();

        // then
        assertThat(newStage.newInterviewSchedules()).isEmpty();
    }

    @Test
    void 최종합격_전형_StageRequest를_변환한다() {
        // given
        StageRequest request = StageRequest.builder()
                .stageType(StageType.FINAL_PASS)
                .build();

        // when
        NewStage newStage = request.toNewStage();

        // then
        assertThat(newStage.stageType()).isEqualTo(StageType.FINAL_PASS);
        assertThat(newStage.submissionStatus()).isNull();
        assertThat(newStage.newEtcSchedule()).isNull();
        assertThat(newStage.newInterviewSchedules()).isEmpty();
    }

    @Test
    void 최종불합격_전형_StageRequest를_변환한다() {
        // given
        StageRequest request = StageRequest.builder()
                .stageType(StageType.FINAL_FAIL)
                .build();

        // when
        NewStage newStage = request.toNewStage();

        // then
        assertThat(newStage.stageType()).isEqualTo(StageType.FINAL_FAIL);
    }

}