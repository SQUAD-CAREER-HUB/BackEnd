package org.squad.careerhub.domain.application.entity;

import static java.util.Objects.requireNonNull;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.squad.careerhub.global.entity.BaseEntity;

/**
 * 지원서 첨부 파일 Entity
 **/

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class ApplicationAttachment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    private Application application;

    @Column(nullable = false)
    private String fileUrl;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String fileType;

    public static ApplicationAttachment create(
            Application application,
            String fileUrl,
            String fileName,
            String fileType
    ) {
        ApplicationAttachment applicationAttachment = new ApplicationAttachment();

        applicationAttachment.application = requireNonNull(application);
        applicationAttachment.fileUrl = requireNonNull(fileUrl);
        applicationAttachment.fileName = requireNonNull(fileName);
        applicationAttachment.fileType = requireNonNull(fileType);

        return applicationAttachment;
    }

}