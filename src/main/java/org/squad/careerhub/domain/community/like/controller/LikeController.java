package org.squad.careerhub.domain.community.like.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.squad.careerhub.domain.community.like.entity.LikeType;
import org.squad.careerhub.domain.community.like.service.LikeService;
import org.squad.careerhub.global.annotation.LoginMember;

@RequiredArgsConstructor
@RestController
public class LikeController extends LikeDocsController {

    private final LikeService likeService;

    @Override
    @PostMapping("/v1/likes/{targetId}")
    public ResponseEntity<Void> add(
            @PathVariable Long targetId,
            @RequestParam LikeType likeType,
            @LoginMember Long memberId
    ) {
        return ResponseEntity.ok().build();
    }

    @Override
    @DeleteMapping("/v1/likes/{targetId}")
    public ResponseEntity<Void> remove(
            @PathVariable Long targetId,
            @RequestParam LikeType likeType,
            @LoginMember Long memberId
    ) {
        return ResponseEntity.noContent().build();
    }

}