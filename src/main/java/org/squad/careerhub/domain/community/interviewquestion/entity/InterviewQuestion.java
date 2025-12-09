package org.squad.careerhub.domain.community.interviewquestion.entity;

import static java.util.Objects.requireNonNull;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.squad.careerhub.domain.community.interviewreview.entity.InterviewReview;
import org.squad.careerhub.global.entity.BaseEntity;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class InterviewQuestion extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interview_review_id", nullable = false)
    private InterviewReview interviewReview;

    @Column(nullable = false)
    private String question;

    public static InterviewQuestion create(InterviewReview interviewReview, String question) {
        InterviewQuestion interviewQuestion = new InterviewQuestion();

        interviewQuestion.interviewReview = requireNonNull(interviewReview);
        interviewQuestion.question = requireNonNull(question);

        return interviewQuestion;
    }

    public void updateQuestion(String question) {
        this.question = requireNonNull(question);
    }

}