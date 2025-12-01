package org.squad.careerhub.domain.community.comment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import org.squad.careerhub.domain.community.comment.service.CommentService;

@RequiredArgsConstructor
@RestController
public class CommentController extends CommentDocsController {

    private final CommentService commentService;

}