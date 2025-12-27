package org.squad.careerhub.domain.archive.entity;

import static java.util.Objects.requireNonNull;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.squad.careerhub.domain.application.entity.Application;
import org.squad.careerhub.domain.community.interviewquestion.entity.InterviewQuestion;
import org.squad.careerhub.global.entity.BaseEntity;

/**
 * 면접 질문 모음 Entity
 **/

@Table(uniqueConstraints = @UniqueConstraint(
        name = "uk_question_archive_application_interview_question",
        columnNames = {"application_id", "interview_question_id"}
))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class QuestionArchive extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    private Application application;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interview_question_id", nullable = false)
    private InterviewQuestion interviewQuestion;

    private String content;

    public static QuestionArchive create(
            Application application,
            InterviewQuestion interviewQuestion
    ) {
        QuestionArchive questionArchive = new QuestionArchive();

        questionArchive.application = requireNonNull(application);
        questionArchive.interviewQuestion = requireNonNull(interviewQuestion);
        questionArchive.content = null;

        return questionArchive;
    }

    public void updateContent(String content) {
        this.content = requireNonNull(content);
    }

}