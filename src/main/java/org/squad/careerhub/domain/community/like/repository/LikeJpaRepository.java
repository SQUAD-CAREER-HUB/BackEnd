package org.squad.careerhub.domain.community.like.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.squad.careerhub.domain.community.like.entity.Like;

public interface LikeJpaRepository extends JpaRepository<Like, Long> {

}