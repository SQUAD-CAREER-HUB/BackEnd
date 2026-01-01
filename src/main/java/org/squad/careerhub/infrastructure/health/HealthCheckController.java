package org.squad.careerhub.infrastructure.health;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {

    @GetMapping("/health")
    public ResponseEntity<HealthCheckResponse> healthCheck() {
        return ResponseEntity.ok(new HealthCheckResponse("OK", "Application is running"));
    }

    @GetMapping("/")
    public ResponseEntity<HealthCheckResponse> root() {
        return ResponseEntity.ok(new HealthCheckResponse("OK", "CareerHub API Server"));
    }

}