package org.squad.careerhub.domain.schedule.entity;

import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.squad.careerhub.global.entity.BaseEntity;

/**
 * 면접 일정 관리 Entity
 **/

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class InterviewSchedule extends BaseEntity {

}