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

        // NOTE: saveAll은 개수만큼 save 쿼리를 날림 MVP라 현재는 saveAll로 진행하지만 추후에 성능 이슈 있을 시 성능 개선
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
     * @param review   면접 후기 엔티티
     */
    public void updateQuestions(List<UpdateReviewQuestion> requests, Long reviewId, InterviewReview review) {
        if (requests == null || requests.isEmpty()) {
            // 요청이 비어있으면 모든 질문 삭제
            deleteQuestionsByReview(reviewId);
            return;
        }

        List<InterviewQuestion> existingQuestions = interviewQuestionJpaRepository.findByInterviewReviewIdAndStatus(reviewId,
                EntityStatus.ACTIVE);

        Map<Long, InterviewQuestion> existingMap = existingQuestions.stream()
                .collect(Collectors.toMap(InterviewQuestion::getId, Function.identity()));

        Set<Long> requestIds = extractRequestIds(requests);

        softDeleteUnrequestedQuestions(existingQuestions, requestIds);
        saveNewQuestionsAndUpdateExisting(requests, existingMap, review);
    }

    // NOTE: MVP 단계라 현재 반복문으로 삭제 처리, 추후 성능 이슈 있을 시 bulk update 고려
    public void deleteQuestionsByReview(Long reviewId) {
        interviewQuestionJpaRepository.findByInterviewReviewIdAndStatus(reviewId, EntityStatus.ACTIVE)
                .forEach(InterviewQuestion::delete);
    }

    private Set<Long> extractRequestIds(List<UpdateReviewQuestion> requests) {
        return requests.stream()
                .map(UpdateReviewQuestion::id)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    private void softDeleteUnrequestedQuestions(List<InterviewQuestion> existingQuestions, Set<Long> requestIds) {
        existingQuestions.stream()
                .filter(question -> !requestIds.contains(question.getId()))
                .forEach(InterviewQuestion::delete);
    }

    private void saveNewQuestionsAndUpdateExisting(
            List<UpdateReviewQuestion> requests,
            Map<Long, InterviewQuestion> existingQuestionsById,
            InterviewReview review
    ) {
        List<InterviewQuestion> newQuestions = requests.stream()
                .map(request -> createNewQuestionIfNotExists(request, existingQuestionsById, review))
                .filter(Objects::nonNull)
                .toList();

        if (!newQuestions.isEmpty()) {
            interviewQuestionJpaRepository.saveAll(newQuestions);
        }
    }

    private InterviewQuestion createNewQuestionIfNotExists(
            UpdateReviewQuestion request,
            Map<Long, InterviewQuestion> existingMap,
            InterviewReview review
    ) {
        if (request.id() != null) {
            // 수정: 기존 질문 업데이트
            return updateExistingQuestion(request, existingMap);
        } else {
            // 생성: 새로운 질문 생성
            return InterviewQuestion.create(review, request.question());
        }
    }

    private InterviewQuestion updateExistingQuestion(
            UpdateReviewQuestion request,
            Map<Long, InterviewQuestion> existingMap
    ) {
        InterviewQuestion existing = existingMap.get(request.id());
        if (existing == null) {
            throw new CareerHubException(ErrorStatus.INTERVIEW_QUESTION_NOT_BELONG_TO_REVIEW);
        }
        existing.updateQuestion(request.question());
        return null; // 수정은 영속성 컨텍스트에서 자동 반영되므로 null 반환
    }

}