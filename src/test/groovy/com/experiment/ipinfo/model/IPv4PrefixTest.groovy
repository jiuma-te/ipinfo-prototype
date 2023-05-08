package com.experiment.ipinfo.model


import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.io.Resources
import com.google.common.base.Charsets
import spock.lang.Specification

class IPv4PrefixTest extends Specification {
    def 'ip v4 prefix record can be deserialization'() {
        given:
        def json = '''
   {
      "ip_prefix": "54.245.168.0/26",
      "region": "us-west-2",
      "service": "ROUTE53_HEALTHCHECKS",
      "network_border_group": "us-west-2"
    }
'''
        when:
        def v4Prefix = new ObjectMapper().readValue(json, IPv4Prefix.class);

        then:
        v4Prefix.getIpPrefix() == "54.245.168.0/26"
        v4Prefix.region == "us-west-2"
        v4Prefix.service == "ROUTE53_HEALTHCHECKS"
        v4Prefix.networkBorderGroup == "us-west-2"
    }

    def 'ip v6 prefix record can be deserialization'() {
        given:
        def json = '''
   {
      "ipv6_prefix": "2600:1ff2:4000::/40",
      "region": "us-west-2",
      "service": "AMAZON",
      "network_border_group": "us-west-2"
    }
'''
        when:
        def v6Prefix = new ObjectMapper().readValue(json, IPv6Prefix.class);

        then:
        v6Prefix.getIpPrefix() == "2600:1ff2:4000::/40"
        v6Prefix.region == "us-west-2"
        v6Prefix.service == "AMAZON"
        v6Prefix.networkBorderGroup == "us-west-2"
    }

    def 'iprange should be able to process downloaded file'() {
        given:
        def json = Resources.toString(Resources.getResource("ip-ranges.json"), Charsets.UTF_8)
        when:
        def ipRanges = new ObjectMapper().readValue(json, IPRanges.class)
        then:
        ipRanges
        ipRanges.syncToken == 1681339991
        ipRanges.IPv4Prefixes.size() == 7240
        ipRanges.ipv6Prefixes.size() == 1805
    }
}
