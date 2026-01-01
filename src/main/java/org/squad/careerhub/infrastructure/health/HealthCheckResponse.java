package org.squad.careerhub.infrastructure.health;

public record HealthCheckResponse(
        String status,
        String message
) {
}

