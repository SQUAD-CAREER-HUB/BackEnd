package org.squad.careerhub.infrastructure.jobposting.dto;

import java.util.List;

public record JobPostingAiResult(
    String company,
    String position,
    String deadline,
    String workplace,
    List<String> recruitmentProcess,
    String status
) { }
