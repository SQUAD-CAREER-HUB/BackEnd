package org.squad.careerhub.domain.community.like.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import org.squad.careerhub.domain.community.like.service.LikeService;

@RequiredArgsConstructor
@RestController
public class LikeController extends LikeDocsController {

    private final LikeService likeService;

}