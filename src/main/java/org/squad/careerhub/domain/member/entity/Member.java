package org.squad.careerhub.domain.member.entity;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.squad.careerhub.global.entity.BaseEntity;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Member extends BaseEntity {

    @Embedded
    private MemberSocialAccount socialAccount;

}