package org.squad.careerhub.domain.community.like.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.squad.careerhub.global.entity.BaseEntity;

@Table(name = "likes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Like extends BaseEntity {

}