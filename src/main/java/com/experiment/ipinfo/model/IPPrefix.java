package com.experiment.ipinfo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
public class IPPrefix {
    @JsonProperty("ip_prefix")
    private String ipPrefix;
    private String region;
    private String service;
    @JsonProperty("network_border_group")
    private String networkBorderGroup;
}
