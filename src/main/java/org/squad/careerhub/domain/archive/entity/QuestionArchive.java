package org.squad.careerhub.domain.archive.entity;

import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.squad.careerhub.global.entity.BaseEntity;

/**
 * 내 면접 질문 모음 Entity
 **/

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class QuestionArchive extends BaseEntity {

}