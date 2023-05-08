package com.experiment.ipinfo.loader

import com.google.common.base.Charsets
import com.google.common.io.Resources
import spock.lang.Specification

class GeoIPFeedParserTest extends Specification {
    def "valid aws geo ip feed should be parsed without error"() {
        given:
        def csv= Resources.toString(Resources.getResource("geo-ip-feed.csv"), Charsets.UTF_8)
        def geoIpParser = new GeoIPFeedParser()
        when:
        def result = geoIpParser.parse(csv)
        then:
        result.size() == 3347
        def firstRecord = result.get(0)
        firstRecord.countryCode == 'NZ'
        firstRecord.ipPrefix == "3.2.32.0/26"
        firstRecord.region == 'NZ-AUK'
        firstRecord.city == "Auckland"
    }
}
