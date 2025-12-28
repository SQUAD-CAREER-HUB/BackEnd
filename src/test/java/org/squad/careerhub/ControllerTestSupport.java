package org.squad.careerhub;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.squad.careerhub.domain.application.controller.ApplicationController;
import org.squad.careerhub.domain.application.service.ApplicationService;
import org.squad.careerhub.domain.archive.controller.QuestionArchiveController;
import org.squad.careerhub.domain.archive.service.QuestionArchiveService;
import org.squad.careerhub.domain.community.interviewreview.controller.InterviewReviewController;
import org.squad.careerhub.domain.community.interviewreview.service.InterviewReviewService;
import org.squad.careerhub.domain.jobposting.controller.JobPostingController;
import org.squad.careerhub.domain.jobposting.service.JobPostingService;
import org.squad.careerhub.domain.schedule.controller.ScheduleController;
import org.squad.careerhub.domain.schedule.service.ScheduleService;
import org.squad.careerhub.global.security.TestSecurityConfig;

@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
@WebMvcTest(
        controllers = {
                ApplicationController.class,
                InterviewReviewController.class,
                JobPostingController.class,
                QuestionArchiveController.class,
                JobPostingController.class,
                ScheduleController.class
        })
public abstract class ControllerTestSupport {

    @Autowired
    protected MockMvcTester mvcTester;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockitoBean
    protected InterviewReviewService interviewReviewService;

    @MockitoBean
    protected ApplicationService applicationService;

    @MockitoBean
    protected JobPostingService jobPostingService;

    @MockitoBean
    protected QuestionArchiveService questionArchiveService;

    @MockitoBean
    protected ScheduleService scheduleService;

}