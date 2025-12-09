package org.squad.careerhub.domain.community.interviewreview.entity;

import static java.util.Objects.requireNonNull;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.squad.careerhub.domain.member.entity.Member;
import org.squad.careerhub.global.entity.BaseEntity;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class InterviewReview extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private Member author;

    @Column(nullable = false)
    private String company;

    @Column(nullable = false)
    private String position;

    @Column(nullable = false)
    private String interviewType;

    @Column(length = 5000, nullable = false)
    private String content;

    public static InterviewReview create(
            Member author,
            String company,
            String position,
            String interviewType,
            String content
    ) {
        InterviewReview interviewReview = new InterviewReview();

        interviewReview.author = requireNonNull(author);
        interviewReview.company = requireNonNull(company);
        interviewReview.position = requireNonNull(position);
        interviewReview.interviewType = requireNonNull(interviewType);
        interviewReview.content = requireNonNull(content);

        return interviewReview;
    }

    public void updateReview(
            String company,
            String position,
            String interviewType,
            String content
    ) {
        this.company = company;
        this.position = position;
        this.interviewType = interviewType;
        this.content = content;
    }

}