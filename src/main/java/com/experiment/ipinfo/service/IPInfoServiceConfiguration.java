package com.experiment.ipinfo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IPInfoServiceConfiguration {
    @Value("${ip-ranges-location}")
    private String location;

    @Bean
    public IPInfoService ipInfoService() {
        return new IPInfoService(location);
    }

    @Bean
    public IPInfoServiceHealthIndicator ipInfoServiceHealthIndicator(IPInfoService ipInfoService) {
        return new IPInfoServiceHealthIndicator(ipInfoService);
    }
}
