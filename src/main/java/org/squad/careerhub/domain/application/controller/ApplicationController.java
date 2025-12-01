package org.squad.careerhub.domain.application.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import org.squad.careerhub.domain.application.service.ApplicationService;

@RequiredArgsConstructor
@RestController
public class ApplicationController extends ApplicationDocsController {

    private final ApplicationService applicationService;

}