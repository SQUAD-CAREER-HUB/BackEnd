package org.squad.careerhub.domain.community.comment.entity;

import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.squad.careerhub.global.entity.BaseEntity;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Comment extends BaseEntity {

}