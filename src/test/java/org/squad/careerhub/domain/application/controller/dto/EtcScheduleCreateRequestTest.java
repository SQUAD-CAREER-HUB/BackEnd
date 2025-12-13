package org.squad.careerhub.domain.application.controller.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.squad.careerhub.domain.application.service.dto.NewEtcSchedule;

class EtcScheduleCreateRequestTest {

    @Test
    void toNewEtcStage로_NewEtcSchedule을_생성한다() {
        // given
        String stageName = "코딩 테스트";
        LocalDateTime scheduledAt = LocalDateTime.of(2025, 4, 1, 10, 0);
        
        EtcScheduleCreateRequest request = new EtcScheduleCreateRequest(stageName, scheduledAt);

        // when
        NewEtcSchedule newEtcSchedule = request.toNewEtcStage();

        // then
        assertThat(newEtcSchedule).isNotNull();
        assertThat(newEtcSchedule.stageName()).isEqualTo(stageName);
        assertThat(newEtcSchedule.scheduledAt()).isEqualTo(scheduledAt);
    }

    @Test
    void 다양한_전형_이름으로_NewEtcSchedule을_생성한다() {
        // given
        String[] stageNames = {"인적성검사", "과제 제출", "AI 면접", "필기시험"};
        
        for (String stageName : stageNames) {
            EtcScheduleCreateRequest request = new EtcScheduleCreateRequest(
                    stageName,
                    LocalDateTime.now()
            );

            // when
            NewEtcSchedule newEtcSchedule = request.toNewEtcStage();

            // then
            assertThat(newEtcSchedule.stageName()).isEqualTo(stageName);
        }
    }

    @Test
    void 미래_일정으로_NewEtcSchedule을_생성한다() {
        // given
        LocalDateTime futureDate = LocalDateTime.now().plusDays(7);
        EtcScheduleCreateRequest request = new EtcScheduleCreateRequest(
                "온라인 코딩 테스트",
                futureDate
        );

        // when
        NewEtcSchedule newEtcSchedule = request.toNewEtcStage();

        // then
        assertThat(newEtcSchedule.scheduledAt()).isAfter(LocalDateTime.now());
    }

    @Test
    void null_값으로_NewEtcSchedule을_생성한다() {
        // given
        EtcScheduleCreateRequest request = new EtcScheduleCreateRequest(null, null);

        // when
        NewEtcSchedule newEtcSchedule = request.toNewEtcStage();

        // then
        assertThat(newEtcSchedule.stageName()).isNull();
        assertThat(newEtcSchedule.scheduledAt()).isNull();
    }

    @Test
    void 긴_전형_이름으로_NewEtcSchedule을_생성한다() {
        // given
        String longStageName = "온라인 코딩 테스트 및 알고리즘 문제 해결 능력 평가 (2차)";
        EtcScheduleCreateRequest request = new EtcScheduleCreateRequest(
                longStageName,
                LocalDateTime.of(2025, 5, 15, 14, 30)
        );

        // when
        NewEtcSchedule newEtcSchedule = request.toNewEtcStage();

        // then
        assertThat(newEtcSchedule.stageName()).isEqualTo(longStageName);
    }

}