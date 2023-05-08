package com.experiment.ipinfo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Data
@Builder
@Jacksonized
public class IPRanges {
    private long syncToken;
    private String createDate;

    @JsonProperty("prefixes")
    private List<IPv4Prefix> IPv4Prefixes;

    @JsonProperty("ipv6_prefixes")
    private List<IPv6Prefix> ipv6Prefixes;
}
