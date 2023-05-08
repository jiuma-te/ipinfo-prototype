package com.experiment.ipinfo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IPInfoServiceConfiguration {
    @Value("${provider.cloud.aws.ip-ranges-url}")
    private String ipRangesURL;

    @Value("${provider.cloud.aws.geo-ip-feed}")
    private String geoIPFeedURL;

    @Bean
    public IPInfoService ipInfoService() {
        return new IPInfoService(ipRangesURL, geoIPFeedURL);
    }

    @Bean
    public IPInfoServiceHealthIndicator ipInfoServiceHealthIndicator(IPInfoService ipInfoService) {
        return new IPInfoServiceHealthIndicator(ipInfoService);
    }
}
