package org.squad.careerhub.infrastructure.jobposting.ai.strategy;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "careerhub.job-posting.ai")
public class JobPostingAiProperties {

    private AiProviderType primary = AiProviderType.DEEPSEEK;

    private boolean fallbackEnabled = true;

    private AiProviderType fallback = AiProviderType.SOLAR;
}