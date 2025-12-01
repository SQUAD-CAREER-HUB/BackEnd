package org.squad.careerhub.domain.application.entity;

import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.squad.careerhub.global.entity.BaseEntity;

/**
 *  지원 상태 변경 이력을 관리하는 Entity
 **/

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class ApplicationStatusHistory extends BaseEntity {

}