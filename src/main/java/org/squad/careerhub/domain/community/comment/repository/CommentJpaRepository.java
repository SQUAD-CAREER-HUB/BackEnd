package org.squad.careerhub.domain.community.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.squad.careerhub.domain.community.comment.entity.Comment;

public interface CommentJpaRepository extends JpaRepository<Comment, Long> {

}