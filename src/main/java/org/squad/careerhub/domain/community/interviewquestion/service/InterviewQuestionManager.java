package org.squad.careerhub.domain.community.interviewquestion.service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.squad.careerhub.domain.community.interviewquestion.entity.InterviewQuestion;
import org.squad.careerhub.domain.community.interviewquestion.repository.InterviewQuestionJpaRepository;
import org.squad.careerhub.domain.community.interviewquestion.service.dto.UpdateReviewQuestion;
import org.squad.careerhub.domain.community.interviewreview.entity.InterviewReview;
import org.squad.careerhub.global.entity.EntityStatus;
import org.squad.careerhub.global.error.CareerHubException;
import org.squad.careerhub.global.error.ErrorStatus;

@RequiredArgsConstructor
@Component
public class InterviewQuestionManager {

    private final InterviewQuestionJpaRepository interviewQuestionJpaRepository;

    public void createQuestions(List<String> interviewQuestions, InterviewReview review) {
        if (interviewQuestions == null) {
            return;
        }
        List<InterviewQuestion> questions = interviewQuestions.stream()
                .map(question -> InterviewQuestion.create(review, question))
                .toList();

        // NOTE: saveAll은 개수만큼 save 쿼리를 날림 MVP라 현재는 saveAll로 진행하지만 추후에 성능 이슈 있을 시 batch 처리 고려
        interviewQuestionJpaRepository.saveAll(questions);
    }

    /**
     * 면접 질문 목록을 업데이트합니다.
     * <ul>
     *   <li>기존 질문이 요청에 없으면 - 삭제 (Soft Delete)</li>
     *   <li>요청에 id가 있고 기존에 존재하면 - 수정</li>
     *   <li>요청에 id가 없으면 - 신규 생성</li>
     * </ul>
     *
     * @param requests 수정할 질문 목록
     * @param reviewId 면접 후기 ID
     * @param review 면접 후기 엔티티
     */
    public void updateQuestions(List<UpdateReviewQuestion> requests, Long reviewId, InterviewReview review) {
        List<InterviewQuestion> existingQuestions = interviewQuestionJpaRepository.findByInterviewReviewIdAndStatus(reviewId, EntityStatus.ACTIVE);

        Map<Long, InterviewQuestion> existingMap = existingQuestions.stream()
                .collect(Collectors.toMap(InterviewQuestion::getId, Function.identity()));

        Set<Long> requestIds = requests.stream()
                .map(UpdateReviewQuestion::id)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // 1. 삭제: 요청에 없는 기존 질문
        existingQuestions.stream()
                .filter(q -> !requestIds.contains(q.getId()))
                .forEach(InterviewQuestion::delete);

        // 2. 수정 또는 생성
        for (UpdateReviewQuestion request : requests) {
            if (request.id() != null) {
                // ID가 있는 경우
                InterviewQuestion existing = existingMap.get(request.id());
                if (existing != null) {
                    // 수정
                    existing.updateQuestion(request.question());
                } else {
                    // 존재하지 않는 ID
                    throw new CareerHubException(ErrorStatus.NOT_FOUND_INTERVIEW_QUESTION);
                }
            } else {
                // ID가 없는 경우 → 생성
                InterviewQuestion newQuestion = InterviewQuestion.create(review, request.question());
                interviewQuestionJpaRepository.save(newQuestion);
            }
        }
    }

    // NOTE: MVP 단계라 현재 반복문으로 삭제 처리, 추후 성능 이슈 있을 시 bulk update 고려
    public void deleteQuestionsByReview(Long reviewId) {
        interviewQuestionJpaRepository.findByInterviewReviewIdAndStatus(reviewId, EntityStatus.ACTIVE)
                .forEach(InterviewQuestion::delete);
    }

}