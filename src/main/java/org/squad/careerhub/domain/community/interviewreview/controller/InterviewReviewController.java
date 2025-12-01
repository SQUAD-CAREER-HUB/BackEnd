package org.squad.careerhub.domain.community.interviewreview.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import org.squad.careerhub.domain.application.service.ApplicationService;
import org.squad.careerhub.domain.community.interviewreview.service.InterviewReviewService;

@RequiredArgsConstructor
@RestController
public class InterviewReviewController extends InterviewReviewDocsController {

    private final InterviewReviewService interviewReviewService;

}