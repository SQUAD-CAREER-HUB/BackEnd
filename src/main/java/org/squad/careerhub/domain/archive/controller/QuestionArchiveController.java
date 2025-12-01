package org.squad.careerhub.domain.archive.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import org.squad.careerhub.domain.archive.service.QuestionArchiveService;

@RequiredArgsConstructor
@RestController
public class QuestionArchiveController extends QuestionArchiveDocsController {

    private final QuestionArchiveService questionArchiveService;

}