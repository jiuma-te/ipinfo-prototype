package com.experiment.ipinfo.service;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;

public class IPInfoServiceHealthIndicator implements HealthIndicator {
    private final IPInfoService ipInfoService;

    public IPInfoServiceHealthIndicator(IPInfoService ipInfoService) {
        this.ipInfoService = ipInfoService;
    }

    public Health health() {
        Health.Builder builder = Health.up();
        if (! ipInfoService.cloudInfoInitialized()) {
            builder.outOfService();
        }

        return builder.build();
    }
}
