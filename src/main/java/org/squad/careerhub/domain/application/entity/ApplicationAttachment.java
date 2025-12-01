package org.squad.careerhub.domain.application.entity;

import jakarta.persistence.Entity;
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

}