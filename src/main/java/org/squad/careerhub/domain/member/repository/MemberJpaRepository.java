package org.squad.careerhub.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.squad.careerhub.domain.member.entity.Member;

public interface MemberJpaRepository extends JpaRepository<Member, Long> {

}