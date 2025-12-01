package org.squad.careerhub.domain.community.report.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import org.squad.careerhub.domain.community.report.service.ReportService;

@RequiredArgsConstructor
@RestController
public class ReportController extends ReportDocsController {

    private final ReportService reportService;

}