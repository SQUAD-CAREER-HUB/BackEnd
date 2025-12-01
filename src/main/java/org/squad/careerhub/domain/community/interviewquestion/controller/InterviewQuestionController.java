package org.squad.careerhub.domain.community.interviewquestion.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import org.squad.careerhub.domain.community.interviewquestion.service.InterviewQuestionService;

@RequiredArgsConstructor
@RestController
public class InterviewQuestionController extends InterviewQuestionDocsController {

    private final InterviewQuestionService interviewQuestionService;

}